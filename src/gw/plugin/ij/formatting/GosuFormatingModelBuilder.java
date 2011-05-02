package gw.plugin.ij.formatting;

import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.lang.java.JavaFormattingModelBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.FormattingDocumentModelImpl;
import com.intellij.psi.formatter.PsiBasedFormattingModel;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import gw.plugin.ij.GosuLanguage;
import gw.plugin.ij.lang.GosuTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFormatingModelBuilder extends JavaFormattingModelBuilder
{
  @NotNull
  public FormattingModel createModel( final PsiElement element, final CodeStyleSettings settings )
  {
    ASTNode node = element.getNode();
    assert node != null;
    PsiFile containingFile = element.getContainingFile().getViewProvider().getPsi( GosuLanguage.instance() );
    assert containingFile != null : element.getContainingFile();
    ASTNode astNode = containingFile.getNode();
    assert astNode != null;
    final GosuBlock block = new GosuBlock( astNode, null, Indent.getAbsoluteNoneIndent(), null, settings );
    return new GosuFormattingModel( containingFile, block, FormattingDocumentModelImpl.createOn( containingFile ) );
  }

  @Nullable
  public TextRange getRangeAffectingIndent( PsiFile file, int offset, ASTNode elementAtOffset )
  {
    return null;
  }

  private static class GosuFormattingModel extends PsiBasedFormattingModel
  {

    GosuFormattingModel( PsiFile file, @NotNull Block rootBlock, FormattingDocumentModelImpl documentModel )
    {
      super( file, rootBlock, documentModel );
    }

    @Override
    protected String replaceWithPsiInLeaf( TextRange textRange, String whiteSpace, ASTNode leafElement )
    {
      if( !myCanModifyAllWhiteSpaces )
      {
        if( leafElement.getElementType() == GosuTokenTypes.TT_WHITESPACE )
        {
          return null;
        }
      }

      IElementType elementTypeToUse = TokenType.WHITE_SPACE;
      ASTNode prevNode = TreeUtil.prevLeaf( leafElement );
      if( prevNode != null && GosuTokenTypes.TT_WHITESPACE == prevNode.getElementType() )
      {
        elementTypeToUse = prevNode.getElementType();
      }
      FormatterUtil.replaceWhiteSpace( whiteSpace, leafElement, elementTypeToUse, textRange );
      return whiteSpace;
    }
  }
}
