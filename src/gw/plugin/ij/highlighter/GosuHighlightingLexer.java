package gw.plugin.ij.highlighter;

import com.intellij.lexer.JavaDocLexer;
import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.StringLiteralLexer;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaDocTokenType;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import gw.plugin.ij.lang.GosuLexer;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuHighlightingLexer extends LayeredLexer
{
  public GosuHighlightingLexer( LanguageLevel languageLevel )
  {
    super( new GosuLexer() );
//    registerSelfStoppingLayer( new StringLiteralLexer( '\"', JavaTokenType.STRING_LITERAL ),
//                               new IElementType[]{JavaTokenType.STRING_LITERAL}, IElementType.EMPTY_ARRAY );
//
//    registerSelfStoppingLayer( new StringLiteralLexer( '\'', JavaTokenType.STRING_LITERAL ),
//                               new IElementType[]{JavaTokenType.CHARACTER_LITERAL}, IElementType.EMPTY_ARRAY );
//

//    LayeredLexer docLexer = new LayeredLexer( new JavaDocLexer( languageLevel.hasEnumKeywordAndAutoboxing() ) );
//
//    HtmlHighlightingLexer lexer = new HtmlHighlightingLexer();
//    lexer.setHasNoEmbeddments( true );
//    docLexer.registerLayer( lexer,
//                            new IElementType[]{JavaDocTokenType.DOC_COMMENT_DATA} );
//
//    registerSelfStoppingLayer( docLexer,
//                               new IElementType[]{JavaTokenType.DOC_COMMENT},
//                               new IElementType[]{JavaDocTokenType.DOC_COMMENT_END} );
  }
}
