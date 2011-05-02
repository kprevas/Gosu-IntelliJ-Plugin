package gw.plugin.ij.lang;

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.ISourceCodeTokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuIjTokenMap
{
  private static final GosuIjTokenMap INSTANCE = new GosuIjTokenMap();

  private Map<Integer, IElementType> _map;

  
  public static GosuIjTokenMap instance()
  {
    return INSTANCE;
  }

  private GosuIjTokenMap()
  {
    _map = new HashMap();

    _map.put( ISourceCodeTokenizer.TT_EOL, JavaTokenType.WHITE_SPACE );
    _map.put( ISourceCodeTokenizer.TT_EOF, JavaTokenType.WHITE_SPACE );
    _map.put( ISourceCodeTokenizer.TT_WHITESPACE, JavaTokenType.WHITE_SPACE );
    _map.put( ISourceCodeTokenizer.TT_COMMENT, JavaTokenType.C_STYLE_COMMENT );
    _map.put( ISourceCodeTokenizer.TT_NUMBER, JavaTokenType.DOUBLE_LITERAL );
    _map.put( ISourceCodeTokenizer.TT_WORD, JavaTokenType.IDENTIFIER );
    _map.put( ISourceCodeTokenizer.TT_OPERATOR, JavaTokenType.OR );
    _map.put( ISourceCodeTokenizer.TT_KEYWORD, JavaTokenType.ABSTRACT_KEYWORD );
    _map.put( ISourceCodeTokenizer.TT_NOTHING, TokenType.DUMMY_HOLDER );
    _map.put( ISourceCodeTokenizer.TT_INTEGER, JavaTokenType.INTEGER_LITERAL );
    _map.put( (int)'"', JavaTokenType.STRING_LITERAL );
    _map.put( (int)'\'', JavaTokenType.CHARACTER_LITERAL );
    _map.put( (int)'(', JavaTokenType.LPARENTH );
    _map.put( (int)')', JavaTokenType.RPARENTH );
    _map.put( (int)'[', JavaTokenType.LBRACKET );
    _map.put( (int)']', JavaTokenType.RBRACKET );
    _map.put( (int)'{', JavaTokenType.LBRACE );
    _map.put( (int)'}', JavaTokenType.RBRACE );
    _map.put( (int)'.', JavaTokenType.DOT );
    _map.put( (int)';', JavaTokenType.SEMICOLON );
    _map.put( (int)',', JavaTokenType.COMMA );
  }

  public IElementType getTokenType( int iGosuTT )
  {
    IElementType tokenType = _map.get( iGosuTT );
    if( tokenType == null )
    {
      tokenType = JavaTokenType.IDENTIFIER;
    }
    return tokenType;
  }
}
