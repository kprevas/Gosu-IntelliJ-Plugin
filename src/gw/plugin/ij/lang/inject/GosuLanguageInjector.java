package gw.plugin.ij.lang.inject;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlProcessingInstruction;
import com.intellij.psi.xml.XmlText;
import gw.plugin.ij.GosuClassFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuLanguageInjector implements MultiHostInjector {

  @Override
  public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull PsiElement context) {
    // todo hack to find out we're in a PCF
    if(context.getContainingFile().getFileType() == XmlFileType.INSTANCE) {
      if(((TreeElement)context).getTreeParent() instanceof XmlProcessingInstruction) {
        return;
      }
      String text = context.getText();
      if(context instanceof XmlAttributeValue) {
        if(text.length() < 2) {
          return;
        }
        text = text.substring(1, text.length() - 1);
      }
      if(text.trim().length() == 0) {
        return;
      }
      try {
//        System.out.println("****** Injecting GOSU for registrar " + registrar.getClass().getSimpleName() + registrar.hashCode());
        registrar.startInjecting(GosuClassFileType.instance().getLanguage());
        TextRange textRange = context instanceof XmlAttributeValue ?
                new TextRange(1, context.getTextRange().getLength() - 1) : new TextRange(0, context.getTextRange().getLength());
        registrar.addPlace(null, null, (PsiLanguageInjectionHost) context, textRange);
      } catch(Throwable t) {
        t.printStackTrace();
      } finally {
        try {
          registrar.doneInjecting();
        } catch(Throwable t) {
          System.err.println("ERROR: " + t.getLocalizedMessage() + ", doneInjecting() failed.");
        }
      }
    }
  }

  @NotNull
  @Override
  public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    ArrayList<Class<? extends PsiElement>> elements = new ArrayList<Class<? extends PsiElement>>();
    elements.add(XmlAttributeValue.class);
    elements.add(XmlText.class);
    return elements;
  }
}
