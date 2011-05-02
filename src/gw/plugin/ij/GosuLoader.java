package gw.plugin.ij;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.psi.impl.source.tree.ChangeUtil;
import com.intellij.util.Function;
import gw.plugin.ij.activator.Activator;
import gw.plugin.ij.compiler.GosuCompiler;
import gw.plugin.ij.debugger.GosuPositionManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuLoader extends ProjectManagerAdapter implements ApplicationComponent {

  private Activator activator;
  private GosuCompiler gosuCompiler;

  public void initComponent() {
    activator = new Activator();
    ProjectManager.getInstance().addProjectManagerListener(this);
    ChangeUtil.registerCopyHandler(new GosuChangeUtilSupport());
  }

  public void projectOpened(final Project project) {
    //TODO-dp REMOVE THIS HACK!
    if (project.getProjectFile().getName().endsWith("PL.ipr")) {
      return;
    }

    activator.start(project);
    registerFileTypes();
    addCompiler(project);
    //registerPositionManager();
  }

  public void projectClosed(Project project) {
    if (activator.getStatus() == Activator.StartupStatus.STARTED) {
      activator.stop(project);
      unregisterFileTypes();
      removeCompiler(project);
    }
  }

  private void addCompiler(Project project) {
    CompilerManager compilerManager = CompilerManager.getInstance(project);
    compilerManager.addCompilableFileType(GosuClassFileType.instance());
    gosuCompiler = new GosuCompiler(project);
    compilerManager.addTranslatingCompiler(gosuCompiler,
        new HashSet<FileType>(Arrays.asList(GosuClassFileType.instance(), StdFileTypes.CLASS)),
        new HashSet<FileType>(Arrays.asList(StdFileTypes.CLASS)));
  }

  private void removeCompiler(Project project) {
    CompilerManager compilerManager = CompilerManager.getInstance(project);
    compilerManager.removeCompilableFileType(GosuClassFileType.instance());
    compilerManager.removeCompiler(gosuCompiler);
    gosuCompiler = null;
  }

  private void registerPositionManager() {
    ProjectManager.getInstance().addProjectManagerListener(
        new ProjectManagerAdapter() {
          public void projectOpened(final Project project) {
            DebuggerManager.getInstance(project).registerPositionManagerFactory(
                new Function<DebugProcess, PositionManager>() {
                  public PositionManager fun(DebugProcess debugProcess) {
                    return new GosuPositionManager(debugProcess);
                  }
                });
          }
        });
  }

  private void registerFileTypes() {
    ApplicationManager.getApplication().runWriteAction(
        new Runnable() {
          public void run() {
            for (FileType ft : GosuFileTypes.TYPES) {
              FileTypeManager.getInstance().registerFileType(ft, ft.getDefaultExtension());
            }
          }
        }
    );
  }

  private void unregisterFileTypes() {
    ApplicationManager.getApplication().runWriteAction(
        new Runnable() {
          public void run() {
            for (FileType ft : GosuFileTypes.TYPES) {
              FileTypeManager.getInstance().removeAssociatedExtension(ft, ft.getDefaultExtension());
            }
          }
        }
    );
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "gosu3.support.loader";
  }

}
