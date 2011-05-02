package gw.plugin.ij.highlighter;

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NonNls;

import java.awt.*;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuHighlighterColors {
    @NonNls
    static final String LINE_COMMENT_ID = "GOSU_LINE_COMMENT";
    @NonNls
    static final String BLOCK_COMMENT_ID = "GOSU_BLOCK_COMMENT";
    @NonNls
    static final String DOC_COMMENT_ID = "GOSU_DOC_COMMENT";
    @NonNls
    static final String DOC_COMMENT_TAG_ID = "GOSU_DOC_TAG";
    @NonNls
    static final String KEYWORD_ID = "GOSU_KEYWORD";
    @NonNls
    static final String WORD_ID = "GOSU_WORD";
    @NonNls
    static final String NUMBER_ID = "GOSU_NUMBER";
    @NonNls
    static final String STRING_ID = "GOSU_STRING";
    @NonNls
    static final String BRACES_ID = "GOSU_BRACES";

    @NonNls
    static final String OPERATOR_ID = "GOSU_OPERATOR";
    @NonNls
    static final String BAD_CHARACTER_ID = "GOSU_BAD_CHAR";

    // semantic colors
//    @NonNls
//    static final String CLASS_ID = "GOSU_CLASS";
//    @NonNls
//    static final String INTERFACE_ID = "GOSU_INTERFACE";
//    @NonNls
//    static final String ENUM_ID = "GOSU_ENUM";
//    @NonNls
//    static final String FIELD_ID = "GOSU_FIELD";
//    @NonNls
//    static final String VARIABLE_ID = "GOSU_VARIABLE";
//    @NonNls
//    static final String METHOD_ID = "GOSU_METHOD";
//    @NonNls
//    static final String PARAMETER_ID = "GOSU_PARAMETER";
//    @NonNls
//    static final String PACKAGE_ID = "GOSU_PACKAGE";

    @NonNls
    static final String UNRESOLVED_ACCESS_ID = "GOSU_UNRESOLVED_REF";

    public static TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID,
            SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());

    public static TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(BLOCK_COMMENT_ID,
            SyntaxHighlighterColors.JAVA_BLOCK_COMMENT.getDefaultAttributes());

    public static TextAttributesKey DOC_COMMENT_CONTENT = TextAttributesKey.createTextAttributesKey(DOC_COMMENT_ID,
            SyntaxHighlighterColors.DOC_COMMENT.getDefaultAttributes());

    public static TextAttributesKey DOC_COMMENT_TAG = TextAttributesKey.createTextAttributesKey(DOC_COMMENT_TAG_ID,
            SyntaxHighlighterColors.DOC_COMMENT_TAG.getDefaultAttributes());

    public static final TextAttributes KEYWORD_ATTRIBUTES = SyntaxHighlighterColors.KEYWORD.getDefaultAttributes().clone();

    static {
//        KEYWORD_ATTRIBUTES.setForegroundColor(new Color(0, 128, 67));
//        KEYWORD_ATTRIBUTES.setFontType(Font.BOLD);
    }

    public static TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(KEYWORD_ID, KEYWORD_ATTRIBUTES);


    public static final TextAttributes WORD_ATTRIBUTES = SyntaxHighlighterColors.KEYWORD.getDefaultAttributes().clone();

    static {
        WORD_ATTRIBUTES.setForegroundColor(new Color(0, 0, 0));
        WORD_ATTRIBUTES.setFontType(Font.PLAIN);
    }

    public static TextAttributesKey WORD = TextAttributesKey.createTextAttributesKey(WORD_ID, WORD_ATTRIBUTES);

    public static TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID,
            SyntaxHighlighterColors.NUMBER.getDefaultAttributes());

    public static TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID,
            SyntaxHighlighterColors.STRING.getDefaultAttributes());

    public static TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(BRACES_ID,
            SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());

    public static TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey(OPERATOR_ID,
            SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes());

    public static TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID,
            CodeInsightColors.UNMATCHED_BRACE_ATTRIBUTES.getDefaultAttributes());

//    public static TextAttributesKey CLASS_ATTRKEY = TextAttributesKey.createTextAttributesKey(CLASS_ID,
//            CodeInsightColors.CLASS_NAME_ATTRIBUTES.getDefaultAttributes());
//
//    public static TextAttributesKey INTERFACE_ATTRKEY = TextAttributesKey.createTextAttributesKey(INTERFACE_ID,
//            CodeInsightColors.INTERFACE_NAME_ATTRIBUTES.getDefaultAttributes());
//
//    public static TextAttributesKey FIELD_ATTRKEY = TextAttributesKey.createTextAttributesKey(FIELD_ID,
//            CodeInsightColors.INSTANCE_FIELD_ATTRIBUTES.getDefaultAttributes());
//
//    public static TextAttributesKey VARIABLE_ATTRKEY = TextAttributesKey.createTextAttributesKey(VARIABLE_ID,
//            CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES.getDefaultAttributes());
//
//    public static TextAttributesKey METHOD_ATTRKEY = TextAttributesKey.createTextAttributesKey(FIELD_ID,
//            CodeInsightColors.METHOD_DECLARATION_ATTRIBUTES.getDefaultAttributes());
//
//    public static TextAttributesKey PARAMETER_ATTRKEY = TextAttributesKey.createTextAttributesKey(FIELD_ID,
//            CodeInsightColors.PARAMETER_ATTRIBUTES.getDefaultAttributes());

    public static TextAttributesKey CLASS_ATTRKEY = CodeInsightColors.CLASS_NAME_ATTRIBUTES;

    public static TextAttributesKey INTERFACE_ATTRKEY = CodeInsightColors.INTERFACE_NAME_ATTRIBUTES;

    public static TextAttributesKey FIELD_ATTRKEY = CodeInsightColors.INSTANCE_FIELD_ATTRIBUTES;

    public static TextAttributesKey VARIABLE_ATTRKEY = CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES;

    public static TextAttributesKey METHOD_ATTRKEY = CodeInsightColors.METHOD_DECLARATION_ATTRIBUTES;

    public static TextAttributesKey PARAMETER_ATTRKEY = CodeInsightColors.PARAMETER_ATTRIBUTES;

    public static TextAttributesKey UNHANDLED_ATTRKEY = CodeInsightColors.PARAMETER_ATTRIBUTES;


    public static final TextAttributes UNRESOLVED_ACCESS_ATTRIBUTES = HighlighterColors.TEXT.getDefaultAttributes().clone();

    static {
        UNRESOLVED_ACCESS_ATTRIBUTES.setForegroundColor(Color.BLACK);
        UNRESOLVED_ACCESS_ATTRIBUTES.setEffectColor(Color.GRAY);
        UNRESOLVED_ACCESS_ATTRIBUTES.setEffectType(EffectType.LINE_UNDERSCORE);
    }

    public static TextAttributesKey UNRESOLVED_ACCESS = TextAttributesKey.createTextAttributesKey(UNRESOLVED_ACCESS_ID, UNRESOLVED_ACCESS_ATTRIBUTES);

    private GosuHighlighterColors() {
    }
}