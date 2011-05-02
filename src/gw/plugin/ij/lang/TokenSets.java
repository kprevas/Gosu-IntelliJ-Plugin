package gw.plugin.ij.lang;

import com.intellij.psi.tree.TokenSet;

/**
 * Utility classdef, tha contains various useful TokenSets
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class TokenSets extends GosuTokenTypes
{
  public static TokenSet COMMENTS_TOKEN_SET = TokenSet.create(
    TT_COMMENT_LINE,
    TT_COMMENT_MULTILINE
  );

  public static TokenSet SEPARATORS = TokenSet.create(
    TT_OP_semicolon
  );

  public static final TokenSet PROPERTY_NAMES = TokenSet.create( TT_IDENTIFIER );

  public static TokenSet VISIBILITY_MODIFIERS = TokenSet.create(
    TT_private,
    TT_internal,
    TT_protected,
    TT_public
  );

  public static TokenSet MODIFIERS = TokenSet.create(
    TT_abstract,
    TT_private,
    TT_internal,
    TT_public,
    TT_protected,
    TT_static,
    TT_transient,
    TT_final
  );

  public static TokenSet WHITE_SPACE_TOKEN_SET = TokenSet.create(
    TT_WHITESPACE
  );

  public static TokenSet STRING_LITERALS = TokenSet.create(
    TT_DOUBLE_QUOTED_STRING,
    TT_SINGLE_QUOTED_STRING
  );

//
//  public static TokenSet SUSPICIOUS_EXPRESSION_STATEMENT_START_TOKEN_SET = TokenSet.create(
//    mMINUS,
//    mPLUS,
//    mLBRACK,
//    mLPAREN,
//    mLCURLY
//  );
//
//  public static final TokenSet NUMBERS = TokenSet.create( mNUM_INT,
//                                                          mNUM_BIG_DECIMAL,
//                                                          mNUM_BIG_INT,
//                                                          mNUM_DOUBLE,
//                                                          mNUM_FLOAT,
//                                                          mNUM_LONG );
//
//
//  public static final TokenSet CONSTANTS = TokenSet.create(
//    mNUM_INT,
//    mNUM_BIG_DECIMAL,
//    mNUM_BIG_INT,
//    mNUM_DOUBLE,
//    mNUM_FLOAT,
//    mNUM_LONG,
//    kTRUE,
//    kFALSE,
//    kNULL,
//    mSTRING_LITERAL,
//    mGSTRING_LITERAL,
//    mREGEX_LITERAL
//  );
//
//  public static final TokenSet BUILT_IN_TYPE = TokenSet.create(
//    kVOID,
//    kBOOLEAN,
//    kBYTE,
//    kCHAR,
//    kSHORT,
//    kINT,
//    kFLOAT,
//    kLONG,
//    kDOUBLE
//  );
//
//  public static final TokenSet PROPERTY_NAMES = TokenSet.create( mIDENT, mSTRING_LITERAL, mGSTRING_LITERAL );
//
//  public static TokenSet REFERENCE_NAMES = TokenSet.orSet( KEYWORDS, PROPERTY_NAMES );
//
//
//
//
//  public static TokenSet STRING_LITERALS = TokenSet.create(
//    mSTRING_LITERAL,
//    mREGEX_LITERAL,
//    mGSTRING_LITERAL,
//    mGSTRING_CONTENT,
//    mGSTRING_BEGIN,
//    mGSTRING_END
//  );
//
//  public static TokenSet FOR_IN_DELIMITERS = TokenSet.create( kIN, mCOLON );
}
