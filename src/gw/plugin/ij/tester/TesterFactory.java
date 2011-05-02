package gw.plugin.ij.tester;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.jgoodies.forms.layout.FormLayout;
import gw.plugin.ij.GosuClassFileType;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class TesterFactory implements ToolWindowFactory {
  public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
    LightVirtualFile virtualFile = new LightVirtualFile("Foo.gsp", GosuClassFileType.instance(),
        "for (i in 1..10) {\n" +
        "  if (i % 3 == 0) {\n" +
        "    print(\"Hello World \" + i + \"!\")\n" +
        "  }\n" +
        "}"
    );
    final PsiAwareTextEditorImpl editor = (PsiAwareTextEditorImpl) PsiAwareTextEditorProvider.getInstance().createEditor(project, virtualFile);
    final EditorImpl editorImpl = (EditorImpl) editor.getEditor();
    final Document document = editorImpl.getDocument();
    FileDocumentManagerImpl.registerDocument(document, virtualFile);
//    final EditorGutterComponentImpl gutter = editorImpl.getGutter();

    editorImpl.addEditorMouseListener(new EditorMouseListener() {
      @Override
      public void mousePressed(EditorMouseEvent event) {
      }

      @Override
      public void mouseClicked(EditorMouseEvent e) {
      }

      @Override
      public void mouseReleased(EditorMouseEvent event) {
        if (event.getArea().toString().equals("LINE_MARKERS_AREA")) {
          final int line = editorImpl.yPositionToLogicalLineNumber(event.getMouseEvent().getPoint().y);
          DebuggerManagerEx debugManager = (DebuggerManagerEx) DebuggerManager.getInstance(project);
          debugManager.getBreakpointManager().addLineBreakpoint(document, line);
        }
      }

      @Override
      public void mouseEntered(EditorMouseEvent e) {
      }

      @Override
      public void mouseExited(EditorMouseEvent e) {
      }
    });

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(new JButton("Run"), BorderLayout.NORTH);
    JPanel buttonPanel2 = new JPanel(new BorderLayout());
    buttonPanel2.add(new JButton("Clear"), BorderLayout.NORTH);
    buttonPanel.add(buttonPanel2, BorderLayout.CENTER);


    JPanel panel = new JPanel(new BorderLayout());
    panel.add(buttonPanel, BorderLayout.WEST);
    panel.add(new JTextArea(10, 50), BorderLayout.EAST);
    panel.add(editor.getComponent(), BorderLayout.CENTER);
    toolWindow.getComponent().add(panel);
  }
}
