package gw.plugin.ij.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageUtil;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import gw.plugin.ij.GosuClassFileType;
import gw.plugin.ij.GosuLanguage;
import gw.plugin.ij.GosuProgramFileType;
import gw.plugin.ij.lang.GosuLexer;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.TokenSets;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.stubs.elements.GosuStubFileElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.ParserDefinition.SpaceRequirements.MUST_LINE_BREAK;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuParserDefinition implements ParserDefinition
{
  public static final IStubFileElementType GOSU_FILE = new GosuStubFileElementType( GosuLanguage.instance() );

  @NotNull
  public Lexer createLexer( Project project )
  {
    return new GosuLexer();
  }

  public PsiParser createParser( Project project )
  {
    throw new UnsupportedOperationException( "Should never be called, parse tree transformation happens in GosuStubFileElementType" );
  }

  public IFileElementType getFileNodeType()
  {
    return GOSU_FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens()
  {
    return TokenSets.WHITE_SPACE_TOKEN_SET;
  }

  @NotNull
  public TokenSet getCommentTokens()
  {
    return TokenSets.COMMENTS_TOKEN_SET;
  }

  @NotNull
  public TokenSet getStringLiteralElements()
  {
    return TokenSets.STRING_LITERALS;
  }

  @NotNull
  public PsiElement createElement( ASTNode node )
  {
    return GosuPsiCreator.createElement( node );
  }

  public PsiFile createFile( FileViewProvider viewProvider )
  {
    String strExt = viewProvider.getVirtualFile().getExtension();
    if( strExt.endsWith( GosuClassFileType.EXT ) )
    {
      return new GosuClassFileImpl( viewProvider );
    }
    if( strExt.endsWith( GosuProgramFileType.EXT ) )
    {
      return new GosuProgramFileImpl( viewProvider );
    }
//    if( strExt.endsWith( GosuEnhancementFileType.EXT ) )
//    {
//      return new GosuEnhancementFileImpl( viewProvider );
//    }
//    if( strExt.endsWith( GosuTemplateFileType.EXT ) )
//    {
//      return new GosuTemplateFileImpl( viewProvider );
//    }

    throw new UnsupportedOperationException( "Don't now how to create file for " + strExt );
  }

  public ParserDefinition.SpaceRequirements spaceExistanceTypeBetweenTokens( ASTNode left, ASTNode right )
  {
    if( right.getElementType() == GosuTokenTypes.TT_uses && left.getElementType() != GosuTokenTypes.TT_WHITESPACE )
    {
      return MUST_LINE_BREAK;
    }
    if( left.getElementType() == GosuTokenTypes.TT_OP_semicolon || left.getElementType() == GosuTokenTypes.TT_COMMENT_LINE  )
    {
      return MUST_LINE_BREAK;
    }

    Lexer lexer = new GosuLexer();
    return LanguageUtil.canStickTokensTogetherByLexer( left, right, lexer );
  }
}
