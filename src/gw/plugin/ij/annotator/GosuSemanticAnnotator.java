package gw.plugin.ij.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.expressions.IIdentifierExpression;
import gw.plugin.ij.highlighter.GosuHighlighterColors;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameter;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuIdentifierImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuNameInDeclarationImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuSemanticAnnotator extends GosuElementVisitor implements Annotator {

  private AnnotationHolder _holder;

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    // TODO try-finally protection for holder?  safety vs performance.
    try {
      _holder = holder;
      if (element instanceof IGosuPsiElement) {
        ((IGosuPsiElement) element).accept(this);
      } else if(element instanceof PsiElement) {
        visitPsiElement(element);
      }
    } catch(Throwable t) {
      t.printStackTrace();
    } finally {
      _holder = null;
    }
  }

  public void visitPsiElement(PsiElement element) {
    TextAttributesKey key = null;

    if(element instanceof PsiJavaToken) {
      IElementType tokenType = ((PsiJavaToken) element).getTokenType();
      if(tokenType == GosuTokenTypes.TT_IDENTIFIER) {
        if (isNativeType(element.getText())) {
          key = GosuHighlighterColors.KEYWORD;
        } else if(element instanceof ASTNode) {
          ASTNode parent = ((ASTNode) element).getTreeParent();
          String type = parent.getElementType().toString();
          if(type != null && !type.contains("Declaration")) {
            if("MethodCallExpression".equals(type)) {
              key = GosuHighlighterColors.METHOD_ATTRKEY;
            } else if("TypeLiteral".equals(type)) {
              key = GosuHighlighterColors.CLASS_ATTRKEY;
            }
          }
        }
      }
    }

    if (key != null) {
      Annotation a = _holder.createInfoAnnotation(element, null);
      a.setTextAttributes(key);
    }
  }

  @Override
  public void visitElement(IGosuPsiElement element) {
    TextAttributesKey key = null;

    if (element instanceof GosuTypeLiteralImpl) {
      if (isNativeType(element.getText())) {
        key = GosuHighlighterColors.KEYWORD;
      } else {
        key = GosuHighlighterColors.CLASS_ATTRKEY;
      }
    }

    if (key != null) {
      Annotation a = _holder.createInfoAnnotation(element, null);
      a.setTextAttributes(key);
    } else {
      super.visitElement(element);
    }
  }

  @Override
  public void visitNameInDeclaration(GosuNameInDeclarationImpl name) {
    TextAttributesKey key = null;
    CompositeElement parent = name.getNode().getTreeParent();

    while (parent != null && key == null) {
      PsiElement psi = parent.getPsi();
      if (psi instanceof IGosuField) {
        key = GosuHighlighterColors.FIELD_ATTRKEY;
      } else if (psi instanceof GosuMethod) {
        key = GosuHighlighterColors.METHOD_ATTRKEY;
      } else if (psi instanceof IGosuParameter) {
        key = GosuHighlighterColors.PARAMETER_ATTRKEY;
      } else if (psi instanceof GosuClassDefinition) {
        key = GosuHighlighterColors.CLASS_ATTRKEY;
      }
      parent = parent.getTreeParent();
    }

    if (key != null) {
      Annotation a = _holder.createInfoAnnotation(name, null);
      a.setTextAttributes(key);
    } else {
      super.visitNameInDeclaration(name);
    }
  }

  private boolean isKeyWord(String word) {
    return Arrays.binarySearch(_gosuKeywords, word) >= 0;
  }

  private boolean isNativeType(String word) {
    return Arrays.binarySearch(_nativeTypes, word) >= 0;
  }

  private boolean isNativeConstant(String word) {
    return Arrays.binarySearch(_nativeConstants, word) >= 0;
  }

  private static final String[] _gosuKeywords = {
          "abstract", "and", "application", "as", "block",
          "break", "case", "catch", "class", "classpath", "construct", "contains", "continue",
          "default", "delegate", "do", "else", "enhancement", "enum", "eval", "except", "execution",
          "exists", "extends", "false", "final", "finally", "find", "for", "foreach", "function",
          "get", "hide", "if", "implements", "in", "index", "interface", "internal", "length", "new",
          "not", "null", "or", "outer", "override", "package", "private", "property", "protected",
          "public", "readonly", "represents", "request",
          // "return",
          "session", "set", "startswith", "static", "statictypeof", "super", "switch", "this", "throw",
          "transient", "true", "try", "typeas", "typeis", "typeof", "unless", "uses", "using", "var",
          "void", "where", "while", "Infinity", "NaN"
  };

  private static final String[] _nativeTypes = {
          "boolean", "byte", "char", "double", "float",
          "int", "long", "short", "strictfp", "void",
  };

  private static final String[] _nativeConstants = {
          "false", "null", "true",
  };

  static {
    Arrays.sort(_gosuKeywords);
    Arrays.sort(_nativeTypes);
    Arrays.sort(_nativeConstants);
  }

}
