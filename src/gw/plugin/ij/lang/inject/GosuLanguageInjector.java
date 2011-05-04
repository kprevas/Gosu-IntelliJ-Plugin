package gw.plugin.ij.lang.inject;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlProcessingInstruction;
import com.intellij.psi.xml.XmlText;
import gw.plugin.ij.GosuClassFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuLanguageInjector implements MultiHostInjector {

  private static Pattern htmlEscape = Pattern.compile("&[a-z]+;");

  @Override
  public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull PsiElement context) {
    // todo hack to find out we're in a PCF
    if(context.getContainingFile().getFileType() == XmlFileType.INSTANCE) {
      CompositeElement parent = ((TreeElement) context).getTreeParent();
      if(parent instanceof XmlProcessingInstruction) {
        return;
      }
      String attrName = parent.getFirstChildNode().getText();
      if(attrName.startsWith("xmlns:") || attrName.startsWith("xsi:")) {
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
      if(htmlEscape.matcher(text).find()) {
        return;
      }
      try {
        System.out.println("****** Injecting GOSU for registrar " + registrar.getClass().getSimpleName() + registrar.hashCode());
        registrar.startInjecting(GosuClassFileType.instance().getLanguage());
        TextRange textRange;
        if(context instanceof XmlAttributeValue) {
          textRange = new TextRange(1, context.getTextRange().getLength() - 1);
        } else if(text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
          textRange = new TextRange(9, context.getTextRange().getLength() - 3);
        } else {
          textRange = new TextRange(0, context.getTextRange().getLength());
        }
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
