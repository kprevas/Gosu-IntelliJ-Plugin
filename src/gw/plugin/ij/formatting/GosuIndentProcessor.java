package gw.plugin.ij.formatting;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.tree.IElementType;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;
import gw.plugin.ij.lang.psi.stubs.index.GosuFullClassNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuIndentProcessor extends GosuElementTypes
{
  /**
   * Calculates indent, based on code style, between parent block and child node
   *
   * @param parent        parent block
   * @param child         child node
   * @param prevChildNode previous child node
   * @return indent
   */
  @NotNull
  public static Indent getChildIndent(@NotNull final GosuBlock parent, @Nullable final ASTNode prevChildNode, @NotNull final ASTNode child) {
    ASTNode parentAstNode = parent.getNode();
    final PsiElement psiParent = parentAstNode.getPsi();

    if( psiParent instanceof GosuClassDefinition )
    {
      if( !isOneof(child, TT_class, TT_OP_brace_left, TT_OP_brace_right ) )
      {
        return Indent.getNormalIndent();
      }
    }

    if( psiParent instanceof GosuMethod )
    {
      if( !isOneof(child, TT_function, TT_OP_brace_left, TT_OP_brace_right ) )
      {
        return Indent.getNormalIndent();
      }
    }

    if( psiParent instanceof GosuStatementListImpl )
    {
      if( !isOneof(child, TT_OP_brace_left, TT_OP_brace_right ) )
      {
        return Indent.getNormalIndent();
      }
    }

    return Indent.getNoneIndent();
  }

  /**
   * Returns indent for simple expressions
   *
   * @param psiParent
   * @param child
   * @return
   */
  private static Indent getExpressionIndent(PsiElement psiParent, ASTNode child) {
    return Indent.getNoneIndent();
  }


  public static boolean isOneof( ASTNode child, GosuElementType... types )
  {
    IElementType eltType = child.getElementType();
    for( int i = 0; i < types.length; i++ )
    {
      if( eltType.equals( types[i] ) )
      {
        return true;
      }
    }
    return false;
  }
}
