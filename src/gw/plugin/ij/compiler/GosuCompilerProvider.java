package gw.plugin.ij.compiler;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import gw.plugin.ij.GosuClassFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCompilerProvider extends AbstractProjectComponent {

    protected GosuCompilerProvider(Project project) {
        super(project);
    }

    public void projectOpened() {

//      compilerManager.addTranslatingCompiler(new GroovycStubGenerator(myProject),
//                                             new HashSet<FileType>(Arrays.asList(StdFileTypes.JAVA, GroovyFileType.GROOVY_FILE_TYPE)),
//                                             new HashSet<FileType>(Arrays.asList(StdFileTypes.JAVA)));

      CompilerManager compilerManager = CompilerManager.getInstance(myProject);
      compilerManager.addCompilableFileType(GosuClassFileType.instance());
      compilerManager.addTranslatingCompiler(new GosuCompiler(myProject),
                                             new HashSet<FileType>(Arrays.asList(GosuClassFileType.instance(), StdFileTypes.CLASS)),
                                             new HashSet<FileType>(Arrays.asList(StdFileTypes.CLASS)));

//      myProject.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter() {
//        @Override
//        public void fileOpened(FileEditorManager source, final VirtualFile file) {
//          if (file.getName().endsWith(".java") && file.getPath().contains(GroovycStubGenerator.GROOVY_STUBS)) {
//            final PsiClass psiClass = GroovycStubGenerator.findClassByStub(myProject, file);
//            if (psiClass != null) {
//              final FileEditorManager fileEditorManager = FileEditorManager.getInstance(myProject);
//              final FileEditor[] editors = fileEditorManager.getEditors(file);
//              if (editors.length != 0) {
//                decorateStubFile(file, fileEditorManager, editors[0]);
//              }
//
//            }
//          }
//        }
//      });
    }

    private void decorateStubFile(final VirtualFile file, FileEditorManager fileEditorManager, FileEditor editor) {
      final EditorNotificationPanel panel = new EditorNotificationPanel();
      panel.setText("This stub is generated for Groovy class to make Groovy-Java cross-compilation possible");
      panel.createActionLabel("Go to the Groovy class", new Runnable() {
        @Override
        public void run() {
//          final PsiClass original = GroovycStubGenerator.findClassByStub(myProject, file);
//          if (original != null) {
//            original.navigate(true);
//          }
        }
      });
      panel.createActionLabel("Exclude from stub generation", new Runnable() {
        @Override
        public void run() {
//          final PsiClass psiClass = GroovycStubGenerator.findClassByStub(myProject, file);
//          if (psiClass != null) {
//            ExcludeFromStubGenerationAction.doExcludeFromStubGeneration(psiClass.getContainingFile());
//          }
        }
      });
      fileEditorManager.addTopComponent(editor, panel);
    }

    @NotNull
    public String getComponentName() {
      return "GosuCompilerLoader";
    }

    public void initComponent() {
        System.out.println();
    }

    public void disposeComponent() {
        System.out.println();
    }

    public void projectClosed() {
        System.out.println();
    }

}
