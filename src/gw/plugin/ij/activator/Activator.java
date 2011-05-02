package gw.plugin.ij.activator;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import gw.config.CommonServices;
import gw.fs.FileFactory;
import gw.fs.IDirectory;
import gw.lang.GosuShop;
import gw.lang.init.GosuInitialization;
import gw.lang.init.GosuPathEntry;
import gw.lang.parser.IGosuParser;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.java.IJavaType;
import gw.lang.reflect.module.*;
import gw.plugin.ij.typesystem.IJNativeModule;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import static gw.lang.reflect.module.IResourceContainer.ContainerType.*;

import static gw.lang.reflect.module.IResourceContainer.*;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class Activator {
  public static final String GOSU_TESTER_MODULE_NAME = "_uberModule";

  public enum StartupStatus {NOT_STARTED, STARTED, FAILED};
  private static volatile StartupStatus status = StartupStatus.NOT_STARTED;

  private static IModule _uberModule; //TODO-dp should not be static
  private ModuleManager moduleManager;
  Module[] allIJModules;

  public void start(Project project) {
    moduleManager = ModuleManager.getInstance(project);
    allIJModules = moduleManager.getModules();
    
    try {
      long t1 = System.currentTimeMillis();
      System.out.println("Taking over the world...");
      // make sure IJavaType is initialized, because if another thread tries
      // to initialize it without the typesystem lock, we'll deadlock
      Class c = IJavaType.class;
      c = IGosuParser.class;

      initializeGosu(project);

      Object o2 = IJavaType.DOUBLE;
      Object o1 = IGosuParser.NaN;

      status = StartupStatus.STARTED;
      System.out.println("Done with above stated action in " + (System.currentTimeMillis() - t1) / 1000 + " sec.");
    } catch (Throwable e) {
      status = StartupStatus.FAILED;
//      log("Gosu editor plugin could not start.", e);
      throw new RuntimeException("Gosu editor plugin could not start.", e);
    }
  }


  void initializeGosu(Project project) {
    CommonServices.getFileSystem().setCachingMode(IFileSystem.CachingMode.NO_CACHING);
    FileFactory.instance().setDefaultPhysicalFileSystem(FileFactory.instance().getRootPhysicalFileSystem());

    List<IModule> modules = defineModules(project);

//      CommonServices.getKernel().redefineService_Privileged(IJavaClassInfoProvider.class, new GosuEditorJavaClassInfoProvider());
    GosuInitialization.initializeMultipleModules(modules);
    TypeSystem.getExecutionEnvironment().addModule(_uberModule);
    updateAllModuleClasspaths();
  }

  private List<IModule> defineModules(Project project) {
    TypeSystem.getExecutionEnvironment().createJreModule(shouldIncludeGosuInJreModule());
    _uberModule = GosuShop.createModule(GOSU_TESTER_MODULE_NAME, false);
    _uberModule.setJavaClasspath(Collections.<URL>emptyList());
    _uberModule.setClassLoader(new ModuleClassLoader(new URL[0], _uberModule));
    _uberModule.addDependency(new Dependency(TypeSystem.getExecutionEnvironment().getJreModule(), false));

    Map<String, List<URL>> classpathMap = createClassPathMap(allIJModules);

    Map<Module, IModule> modules = new HashMap<Module, IModule>();
    List<IModule> allModules = new ArrayList<IModule>();
    for (Module ijModule : allIJModules) {
      IModule module = defineModule(ijModule, classpathMap.get(ijModule.getName()));
      if (module != null) {
        modules.put(ijModule, module);
        allModules.add(module);
      }
    }

    for (Module ijModule : allIJModules) {
      IModule module = modules.get(ijModule);
      for (Module dep : getAllRequiredModules(ijModule)) {
        IModule moduleDep = modules.get(dep);
        if (moduleDep != null) {
          module.addDependency(new Dependency(moduleDep, true));
          removeRedundantLibraries(module, moduleDep);
        }
      }
    }

    addImplicitJreModuleDependency(allModules);
   _uberModule.update();
    return allModules;
  }

  private void addImplicitJreModuleDependency(List<IModule> modules) {
    IModule jreModule = TypeSystem.getExecutionEnvironment().getJreModule();
    if (jreModule.getJavaClassPath() == null) {
      ((IJreModule) jreModule).defineDefaultJavaClassPath();
    }
    for (IModule module : modules) {
      module.addDependency(new Dependency(jreModule, false));
    }
    modules.add(jreModule);
  }

  private void removeRedundantLibraries(IModule module, IModule moduleDep) {
    for (Dependency dep : moduleDep.getDependencies()) {
      switch(dep.getResourceContainer().getContainerType()) {
        case MODULE:
          removeRedundantLibraries(module, (IModule)dep.getResourceContainer());
          break;
        case LIBRARY:
          for(Iterator<Dependency> it = module.getDependencies().iterator(); it.hasNext();) {
            Dependency dep2 = it.next();
            if(dep2.getResourceContainer().getContainerType() == ContainerType.LIBRARY &&
                ((LibraryContainer)dep2.getResourceContainer()).getOutputPath().equals(((LibraryContainer)dep.getResourceContainer()).getOutputPath())) {
              it.remove();
            }
          }
          break;
      }
    }
  }

  public List<Module> getAllRequiredModules(Module p) {
    Set<Module> visitedProjects = new HashSet<Module>();
    List<Module> projects = new ArrayList<Module>();
    getAllRequiredProjects(p, projects, visitedProjects);
    return projects;
  }

  private void getAllRequiredProjects(Module ijModule, List<Module> ijModuleList, Set<Module> visitedModules) {
    visitedModules.add(ijModule);

    Module[] dependencies = ModuleRootManager.getInstance(ijModule).getDependencies();
    for (Module otherModule : dependencies) {
      if (visitedModules.contains(otherModule)) {
        continue;
      } else {
        ijModuleList.add(otherModule);
        getAllRequiredProjects(otherModule, ijModuleList, visitedModules);
      }
    }
  }

  public IModule defineModule(Module ijModule, List<URL> classpath) {
    IModule gosuModule = GosuShop.createModule(ijModule.getName(), false);

    ModuleClassLoaderSupport loaderSupport = new ModuleClassLoaderSupport(ijModule, gosuModule);
    File moduleLocation = new File(ijModule.getProject().getLocation());
    IDirectory eclipseModuleRoot = CommonServices.getFileSystem().getIDirectory(moduleLocation);
    gosuModule.addRoot(eclipseModuleRoot);
    gosuModule.setJavaClasspath(loaderSupport.getProjectClasspaths());
    gosuModule.setClassLoader(new ModuleClassLoader(classpath.toArray(new URL[classpath.size()]), gosuModule));
    // why is this needeed ?
    gosuModule.setTypeloaderClassLoader(new URLClassLoader(classpath.toArray(new URL[classpath.size()]), getClass().getClassLoader()));
    List<IDirectory> sourceFolders = getSourceFolders(ijModule);
    gosuModule.setSourcePath(sourceFolders);
    IDirectory sourceRoot = computeCommonRoot(sourceFolders);
    if (sourceRoot != null) {
      gosuModule.addRoot(sourceRoot);
    }

    if (eclipseModuleRoot.file("module.xml").exists()) {
      GosuPathEntry pathEntry = GosuShop.createPathEntryFromModuleFile(eclipseModuleRoot.file("module.xml"));
      gosuModule.addPathEntry(pathEntry);
    }

    //Fix this
//    ModuleRootManager rootManager = ModuleRootManager.getInstance(ijModule);
//    File outputPath = rootManager.getOutputLocation().removeFirstSegments(1).toFile();
//    outputPath = new File(moduleLocation, outputPath.getPath());
//    gosuModule.setOutputPath(CommonServices.getFileSystem().getIDirectory(outputPath));

    gosuModule.setNativeModule(new IJNativeModule(ijModule));
    _uberModule.addDependency(new Dependency(gosuModule, false));
    return gosuModule;
  }

  private IDirectory computeCommonRoot(List<IDirectory> sourceFolders) {
    if (sourceFolders.size() == 0) {
      return null;
    } else if (sourceFolders.size() == 1) {
      return sourceFolders.get(0);
    } else {
      String[] paths = new String[sourceFolders.size()];
      int minLength = Integer.MAX_VALUE;
      for (int i = 0; i < paths.length; i++) {
        //TODO-dp is it ok to call getFileSystemPathString() ?
        paths[i] = sourceFolders.get(i).getPath().getFileSystemPathString();
        if (paths[i].length() < minLength) {
          minLength = paths[i].length();
        }
      }
      int charIndex;
      outer: for (charIndex = 0; charIndex < minLength; charIndex++) {
        char c0 = paths[0].charAt(charIndex);
        for (int i = 1; i < paths.length; i++) {
          if (paths[i].charAt(charIndex) != c0) {
            break outer;
          }
        }
      }
      String dirName = paths[0].substring(0, charIndex);
      File dir = new File(dirName);
      if (!dir.exists()) {
        return null;
      } else {
        return CommonServices.getFileSystem().getIDirectory(dir);
      }
    }
  }

  private List<IDirectory> getSourceFolders(Module ijModule) {
    ModuleRootManager rootManager = ModuleRootManager.getInstance(ijModule);

    VirtualFile[] sourceRoots = rootManager.getSourceRoots();
    List<IDirectory> allSourcePaths = new ArrayList<IDirectory>();
    for (VirtualFile sourceRoot: sourceRoots) {
      String sourcePath = sourceRoot.getPath();
      IDirectory iDirectory = CommonServices.getFileSystem().getIDirectory( new File(sourcePath) );
      allSourcePaths.add(iDirectory);
    }

    return allSourcePaths;
  }

  void updateAllModuleClasspaths() {
    List<? extends IModule> modules = TypeSystem.getExecutionEnvironment().getModules();
    List<Module> projects = new ArrayList<Module>();
    for (IModule module : modules) {
      Object nativeModule = module.getNativeModule();
      if (nativeModule != null) {
        projects.add((Module) nativeModule);
      }
    }

    Map<String, List<URL>> classpathMap = createClassPathMap(projects.toArray(new Module[projects.size()]));
    for (IModule module : modules) {
      if (module.getNativeModule() != null) {
        module.setJavaClasspath(classpathMap.get(module.getName()));
      }
    }
  }

  private Map<String, List<URL>> createClassPathMap(Module[] allIJModules) {
    Map<String, List<URL>> classpathMap = new HashMap<String, List<URL>>();

    for (Module module : allIJModules) {
      String name = module.getName();
      List<URL> classPathURLs = ModuleClassLoaderSupport.getClassPathURLs(module);
      classpathMap.put(name, classPathURLs);
    }

    // simplify classpaths
    for (Module module : allIJModules) {
      List<URL> referencedTotalClasspath = getReferencedTotalClasspath(module, classpathMap);
      if (referencedTotalClasspath != null) {
        List<URL> claspath = classpathMap.get(module.getName());
        for (Iterator<URL> i = claspath.iterator(); i.hasNext();) {
          URL url = i.next();
          if (referencedTotalClasspath.contains(url)) {
            i.remove();
          }
        }
      }
    }

    return classpathMap;
  }

  private List<URL> getReferencedTotalClasspath(Module ijModule, Map<String, List<URL>> classpathMap) {
    List<URL> totalClasspath = new ArrayList<URL>();
    List<Module> referencedModules = getAllRequiredModules(ijModule);
    for (Module m : referencedModules) {
      totalClasspath.addAll(classpathMap.get(m.getName()));
    }
    return totalClasspath;
  }

  private boolean shouldIncludeGosuInJreModule() {
    return true;
  }

  public void stop(Project project) {
//    IExecutionEnvironment env = TypeSystem.getExecutionEnvironment();
//    for (IModule module : env.getModules()) {
//      env.removeModule(module);
//    }
    GosuInitialization.uninitialize();
    status = Activator.StartupStatus.NOT_STARTED;
  }

  public static IModule getUberModule() {
    return _uberModule;
  }

  public StartupStatus getStatus() {
    return status;
  }

}
