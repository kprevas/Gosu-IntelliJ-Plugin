package gw.plugin.ij.lang;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import gw.lang.parser.ISourceCodeTokenizer;
import gw.lang.parser.IToken;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuTokenTypes
{
  private static Map<String, IElementType> TYPE_BY_TOKEN = new HashMap<String, IElementType>();

  public static final GosuElementType TT_IDENTIFIER = addTokenType( "_identifier_" );

  public static final GosuElementType TT_NUMBER = addTokenType( "_literal_" );

  public static final GosuElementType TT_COMMENT_MULTILINE = addTokenType( "_multiline_comment_" );
  public static final GosuElementType TT_COMMENT_LINE = addTokenType( "_line_comment_" );


  public static final IElementType TT_WHITESPACE = TokenType.WHITE_SPACE;

  public static final GosuElementType TT_DOUBLE_QUOTED_STRING = addTokenType( "_double_quoted_string_" );
  public static final GosuElementType TT_SINGLE_QUOTED_STRING = addTokenType( "_single_quoted_string_" );

  //
  // Keywords
  //

  public static final GosuElementType TT_true = addTokenType( "true" );
  public static final GosuElementType TT_false = addTokenType( "false" );
  public static final GosuElementType TT_NaN = addTokenType( "NaN" );
  public static final GosuElementType TT_Infinity = addTokenType( "Infinity" );
  public static final GosuElementType TT_and = addTokenType( "and" );
  public static final GosuElementType TT_or = addTokenType( "or" );
  public static final GosuElementType TT_not = addTokenType( "not" );
  public static final GosuElementType TT_null = addTokenType( "null" );
  public static final GosuElementType TT_length = addTokenType( "length" );
  public static final GosuElementType TT_exists = addTokenType( "exists" );
  public static final GosuElementType TT_in = addTokenType( "in" );
  public static final GosuElementType TT_startswith = addTokenType( "startswith" );
  public static final GosuElementType TT_contains = addTokenType( "contains" );
  public static final GosuElementType TT_where = addTokenType( "where" );
  public static final GosuElementType TT_find = addTokenType( "find" );
  public static final GosuElementType TT_var = addTokenType( "var" );
  public static final GosuElementType TT_delegate = addTokenType( "delegate" );
  public static final GosuElementType TT_represents = addTokenType( "represents" );
  public static final GosuElementType TT_as = addTokenType( "as" );
  public static final GosuElementType TT_typeof = addTokenType( "typeof" );
  public static final GosuElementType TT_statictypeof = addTokenType( "statictypeof" );
  public static final GosuElementType TT_typeis = addTokenType( "typeis" );
  public static final GosuElementType TT_typeas = addTokenType( "typeas" );
  public static final GosuElementType TT_package = addTokenType( "package" );
  public static final GosuElementType TT_uses = addTokenType( "uses" );
  public static final GosuElementType TT_if = addTokenType( "if" );
  public static final GosuElementType TT_else = addTokenType( "else" );
  public static final GosuElementType TT_except = addTokenType( "except" );
  public static final GosuElementType TT_unless = addTokenType( "unless" );
  public static final GosuElementType TT_foreach = addTokenType( "foreach" );
  public static final GosuElementType TT_for = addTokenType( "for" );
  public static final GosuElementType TT_index = addTokenType( "index" );
  public static final GosuElementType TT_while = addTokenType( "while" );
  public static final GosuElementType TT_do = addTokenType( "do" );
  public static final GosuElementType TT_continue = addTokenType( "continue" );
  public static final GosuElementType TT_break = addTokenType( "break" );
  public static final GosuElementType TT_return = addTokenType( "return" );
  public static final GosuElementType TT_construct = addTokenType( "construct" );
  public static final GosuElementType TT_function = addTokenType( "function" );
  public static final GosuElementType TT_property = addTokenType( "property" );
  public static final GosuElementType TT_get = addTokenType( "get" );
  public static final GosuElementType TT_set = addTokenType( "set" );
  public static final GosuElementType TT_try = addTokenType( "try" );
  public static final GosuElementType TT_catch = addTokenType( "catch" );
  public static final GosuElementType TT_finally = addTokenType( "finally" );
  public static final GosuElementType TT_this = addTokenType( "this" );
  public static final GosuElementType TT_throw = addTokenType( "throw" );
  public static final GosuElementType TT_new = addTokenType( "new" );
  public static final GosuElementType TT_switch = addTokenType( "switch" );
  public static final GosuElementType TT_case = addTokenType( "case" );
  public static final GosuElementType TT_default = addTokenType( "default" );
  public static final GosuElementType TT_eval = addTokenType( "eval" );
  public static final GosuElementType TT_private = addTokenType( "private" );
  public static final GosuElementType TT_internal = addTokenType( "internal" );
  public static final GosuElementType TT_protected = addTokenType( "protected" );
  public static final GosuElementType TT_public = addTokenType( "public" );
  public static final GosuElementType TT_abstract = addTokenType( "abstract" );
  public static final GosuElementType TT_override = addTokenType( "override" );
  public static final GosuElementType TT_hide = addTokenType( "hide" );
  public static final GosuElementType TT_final = addTokenType( "final" );
  public static final GosuElementType TT_static = addTokenType( "static" );
  public static final GosuElementType TT_extends = addTokenType( "extends" );
  public static final GosuElementType TT_transient = addTokenType( "transient" );
  public static final GosuElementType TT_implements = addTokenType( "implements" );
  public static final GosuElementType TT_readonly = addTokenType( "readonly" );
  public static final GosuElementType TT_class = addTokenType( "class" );
  public static final GosuElementType TT_interface = addTokenType( "interface" );
  public static final GosuElementType TT_enum = addTokenType( "enum" );
  public static final GosuElementType TT_super = addTokenType( "super" );
  public static final GosuElementType TT_outer = addTokenType( "outer" );
  public static final GosuElementType TT_execution = addTokenType( "execution" );
  public static final GosuElementType TT_request = addTokenType( "request" );
  public static final GosuElementType TT_session = addTokenType( "session" );
  public static final GosuElementType TT_application = addTokenType( "application" );
  public static final GosuElementType TT_void = addTokenType( "void" );
  public static final GosuElementType TT_block = addTokenType( "block" );
  public static final GosuElementType TT_enhancement = addTokenType( "enhancement" );
  public static final GosuElementType TT_classpath = addTokenType( "classpath" );
  public static final GosuElementType TT_typeloader = addTokenType( "typeloader" );
  public static final GosuElementType TT_using = addTokenType( "using" );

  //
  // Operators
  //

  public static final GosuElementType TT_OP_assign = addTokenType( "=" );
  public static final GosuElementType TT_OP_greater = addTokenType( ">" );
  public static final GosuElementType TT_OP_less = addTokenType( "<" );

  public static final GosuElementType TT_OP_not_logical = addTokenType( "!" );
  public static final GosuElementType TT_OP_not_bitwise = addTokenType( "~" );
  public static final GosuElementType TT_OP_question = addTokenType( "?" );
  public static final GosuElementType TT_OP_colon = addTokenType( ":" );
  public static final GosuElementType TT_OP_ternary = addTokenType( "?:" );

  public static final GosuElementType TT_OP_equals = addTokenType( "==" );
  public static final GosuElementType TT_OP_less_equals = addTokenType( "<=" );
  public static final GosuElementType TT_OP_greater_equals = addTokenType( ">=" );
  public static final GosuElementType TT_OP_not_equals = addTokenType( "!=" );
  public static final GosuElementType TT_OP_not_equals_for_losers = addTokenType( "<>");

  public static final GosuElementType TT_OP_logical_and = addTokenType( "&&" );
  public static final GosuElementType TT_OP_logical_or = addTokenType( "||" );
        
  public static final GosuElementType TT_OP_increment = addTokenType( "++" );
  public static final GosuElementType TT_OP_decrement = addTokenType( "--" );

  public static final GosuElementType TT_OP_identity = addTokenType( "===" );
  public static final GosuElementType TT_OP_expansion = addTokenType( "*." );

  // Arithmetic operators
  public static final GosuElementType TT_OP_plus = addTokenType( "+" );
  public static final GosuElementType TT_OP_minus = addTokenType( "-" );
  public static final GosuElementType TT_OP_multiply = addTokenType( "*" );
  public static final GosuElementType TT_OP_divide = addTokenType( "/" );
  public static final GosuElementType TT_OP_modulo = addTokenType( "%" );
  public static final GosuElementType TT_OP_bitwise_and = addTokenType( "&" );
  public static final GosuElementType TT_OP_bitwise_or = addTokenType( "|" );
  public static final GosuElementType TT_OP_bitwise_xor = addTokenType( "^" );

  // Null-safe arithmetic operators
  public static final GosuElementType TT_OP_nullsafe_plus = addTokenType( "?+" );
  public static final GosuElementType TT_OP_nullsafe_minus = addTokenType( "?-" );
  public static final GosuElementType TT_OP_nullsafe_multiply = addTokenType( "?*" );
  public static final GosuElementType TT_OP_nullsafe_divide = addTokenType( "?/" );
  public static final GosuElementType TT_OP_nullsafe_modulo = addTokenType( "?%" );

  // Compound operators
  public static final GosuElementType TT_OP_assign_plus = addTokenType( "+=" );
  public static final GosuElementType TT_OP_assign_minus = addTokenType( "-=" );
  public static final GosuElementType TT_OP_assign_multiply = addTokenType( "*=" );
  public static final GosuElementType TT_OP_assign_divide = addTokenType( "/=" );
  public static final GosuElementType TT_OP_assign_modulo = addTokenType( "%=" );
  public static final GosuElementType TT_OP_assign_and = addTokenType( "&=" );
  public static final GosuElementType TT_OP_assing_or = addTokenType( "|=" );
  public static final GosuElementType TT_OP_assign_xor = addTokenType( "^=" );

  // Block operators
  public static final GosuElementType TT_OP_escape = addTokenType( "\\" );
  public static final GosuElementType TT_OP_assign_closure = addTokenType( "->" );

  // Member-access operators
  public static final GosuElementType TT_OP_dot = addTokenType( "." );
  public static final GosuElementType TT_OP_nullsafe_dot = addTokenType( "?." );

  // Null-safe array access
  public static final GosuElementType TT_OP_nullsafe_array_access = addTokenType( "?[" );

  // Interval operators
  public static final GosuElementType TT_OP_interval = addTokenType( ".." );
  public static final GosuElementType TT_OP_interval_left_open = addTokenType( "|.." );
  public static final GosuElementType TT_OP_interval_right_open = addTokenType( "..|" );
  public static final GosuElementType TT_OP_interval_open = addTokenType( "|..|" );

  // Feature Literals
  public static final GosuElementType TT_OP_feature_access = addTokenType( "#" );

  public static final GosuElementType TT_OP_shift_left = addTokenType( "<<" );
  public static final GosuElementType TT_OP_shift_right = addTokenType( ">>" );
  public static final GosuElementType TT_OP_shift_right_unsigned = addTokenType( ">>>" );
  public static final GosuElementType TT_OP_assign_shift_left = addTokenType( "<<=" );
  public static final GosuElementType TT_OP_assign_shift_right = addTokenType( ">>=" );
  public static final GosuElementType TT_OP_assign_shift_right_unsigned = addTokenType( ">>>=" );

  // Delimiters
  public static final GosuElementType TT_OP_brace_left = addTokenType( "{" );
  public static final GosuElementType TT_OP_brace_right = addTokenType( "}" );
  public static final GosuElementType TT_OP_paren_left = addTokenType( "(" );
  public static final GosuElementType TT_OP_paren_right = addTokenType( ")" );
  public static final GosuElementType TT_OP_bracket_left = addTokenType( "[" );
  public static final GosuElementType TT_OP_bracket_right = addTokenType( "]" );

  public static final GosuElementType TT_OP_quote_double = addTokenType( "\"" );
  public static final GosuElementType TT_OP_quote_single = addTokenType( "'" );

  // Separators
  public static final GosuElementType TT_OP_at = addTokenType( "@" );
  public static final GosuElementType TT_OP_dollar = addTokenType( "$" );

  public static final GosuElementType TT_OP_comma = addTokenType( "," );
  public static final GosuElementType TT_OP_semicolon = addTokenType( ";" );


  private static GosuElementType addTokenType( String strToken )
  {
    GosuElementType type = new GosuElementType( strToken );
    TYPE_BY_TOKEN.put( strToken, type );

//## todo: copy keyword config wrt crappy mixed case from Keyword.java
//    TYPE_BY_TOKEN.put( strToken.toUpperCase(), type );
//    TYPE_BY_TOKEN.put( Character.toUpperCase( strToken.charAt( 0 ) ) + strToken.substring( 1 ).toLowerCase(), type );

    return type;
  }

  public static IElementType getTypeFrom( IToken token )
  {
    switch( token.getType() )
    {
      case ISourceCodeTokenizer.TT_WORD:
        return TT_IDENTIFIER;

      case ISourceCodeTokenizer.TT_COMMENT:
        return TT_COMMENT_MULTILINE;

      case ISourceCodeTokenizer.TT_WHITESPACE:
        return TT_WHITESPACE;

      case ISourceCodeTokenizer.TT_INTEGER:
      case ISourceCodeTokenizer.TT_NUMBER:
        return TT_NUMBER;

      case (int)'"':
      case (int)'\'':
        return TYPE_BY_TOKEN.get( String.valueOf( (char)token.getType() ) );

      case ISourceCodeTokenizer.TT_KEYWORD:
      case ISourceCodeTokenizer.TT_OPERATOR:
      default:
      {
        IElementType tt = TYPE_BY_TOKEN.get( token.getText() );
        if( tt == null )
        {
          throw new IllegalStateException( "Unhandled token type: " + token );
        }
        return tt;
      }
    }
  }
}
