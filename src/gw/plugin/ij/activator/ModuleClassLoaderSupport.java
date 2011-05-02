package gw.plugin.ij.activator;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import gw.config.CommonServices;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.module.IModule;
import gw.lang.reflect.module.ModuleClassLoader;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class ModuleClassLoaderSupport {
  private ModuleClassLoader _classLoader;
  private List<URL> _paths;

  public ModuleClassLoaderSupport(Module ijModule, IModule gosuModule) {
    _paths = getClassPathURLs(ijModule);
    _classLoader = new ModuleClassLoader(_paths.toArray(new URL[_paths.size()]), gosuModule);
  }

  public ModuleClassLoader getProjectClassLoader() {
    return _classLoader;
  }

  public List<URL> getProjectClasspaths() {
    return _paths;
  }

  public static List<URL> getClassPathURLs(Module ijModule) {
    Map<Module, List<URL>> map = new Hashtable<Module, List<URL>>();
    List<URL> classPathURLs = getClassPathURLs(ijModule, map);
    return classPathURLs;
  }

  private static List<URL> getClassPathURLs(Module ijModule, Map<Module, List<URL>> resolvedPaths) {
    List<URL> paths = new ArrayList<URL>();
    List<URL> jreUrlPaths = new ArrayList<URL>();

    if (resolvedPaths.containsKey(ijModule)) {
      return resolvedPaths.get(ijModule);
    }

    ModuleRootManager rootManager = ModuleRootManager.getInstance(ijModule);
    OrderEntry[] orderEntries = rootManager.getOrderEntries();

    try {
      Set<File> jrePaths = getJreContainerPaths(ijModule);

      // Build output, relative to project
      VirtualFile[] outputRoots = CompilerModuleExtension.getInstance(ijModule).getOutputRoots(true);

      for (OrderEntry orderEntry : orderEntries) {
        if (orderEntry instanceof LibraryOrderEntry) {
          LibraryOrderEntry library = (LibraryOrderEntry)orderEntry;
          VirtualFile[] files = library.getLibrary().getFiles(OrderRootType.CLASSES);
          for (VirtualFile virtualFile : files) {
            String fileName = virtualFile.getPath();
            fileName = stripExtraCharacters(fileName);
            File file = new File(fileName);
            boolean bJrePath = jrePaths.contains(file);
            if (file.exists()) {
              URL url = file.toURI().toURL();
              if (!bJrePath) {
                paths.add(url);
              } else {
                jreUrlPaths.add(url);
              }
            } else {
              File libPath = new File(file.getPath());
              if (libPath.exists()) {
                paths.add(libPath.toURI().toURL());
              }
            }
          }
        }
      }

      for (VirtualFile outputRoot : outputRoots) {
        URL url = new URL(outputRoot.getUrl());
        paths.add(url);
      }

      resolvedPaths.put(ijModule, paths);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (!jreUrlPaths.isEmpty()) {
      IModule jreModule = TypeSystem.getExecutionEnvironment().getJreModule();
      if (jreModule.includesGosuCoreAPI()) {
        try {
          String coreGosuResources = getCoreGosuResourcePath(ijModule);
          jreUrlPaths.add(new File(coreGosuResources).toURI().toURL());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      configureJreModule(jreUrlPaths);
    }

    return paths;
  }

  private static String stripExtraCharacters(String fileName) {
    //TODO-dp this is not robust enough (eliminated !/ at the end of the jar)
    if (fileName.endsWith("!/")) {
      fileName = fileName.substring(0, fileName.length() - 2);
    }
    return fileName;
  }

  private static Set<File> getJreContainerPaths(Module ijModule) {
    Set<File> jrePaths = new HashSet<File>();
    ModuleRootManager rootManager = ModuleRootManager.getInstance(ijModule);
    Sdk sdk = rootManager.getSdk();
    VirtualFile[] jarFiles = ((ProjectJdkImpl) sdk).getRoots(OrderRootType.CLASSES);

    for (VirtualFile jarFile : jarFiles) {
      String path = stripExtraCharacters(jarFile.getPath());
      jrePaths.add(new File(path));
    }

    return jrePaths;
  }


  public static String getCoreGosuResourcePath(Module module) {
    String coreGosuResources = null;
//    IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(new Path(GosuEditorPlugin.CORE_GOSU_RESOURCES), module);
//    if (classpathContainer != null) {
//      IClasspathEntry[] classpathEntries = classpathContainer.getClasspathEntries();
//      if (classpathEntries != null && classpathEntries.length > 0) {
//        coreGosuResources = classpathEntries[0];
//      }
//    }
    return coreGosuResources;
  }

  public static void configureJreModule(List<URL> jreUrlPaths) {
    IModule jreModule = TypeSystem.getExecutionEnvironment().getJreModule();
    jreModule.setJavaClasspath(jreUrlPaths);
    jreModule.setClassLoader(new ModuleClassLoader(new URL[0], // jreUrlPaths.toArray(newURL[jreUrlPaths.size()]),
        CommonServices.getEntityAccess().getPluginClassLoader(), jreModule));
    //TODO-dp
//    IPath jreSrcPath = JavaCore.getClasspathVariable(JavaRuntime.JRESRC_VARIABLE);
//    File file = jreSrcPath != null ? jreSrcPath.toFile().getAbsoluteFile() : null;
//    if (file != null && file.exists() && jreModule.getSourcePath() == null) {
//      jreModule.setSourcePath(Collections.singletonList(CommonServices.getFileSystem().getIDirectory(file)));
//    }
  }

}
