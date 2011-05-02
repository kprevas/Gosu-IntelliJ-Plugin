/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gw.plugin.ij.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate myBlock hierarchy
 *
 * @author ilyas
 */
public class GosuBlockGenerator extends GosuElementTypes
{
  public static List<Block> generateSubBlocks(ASTNode node,
                                              Alignment myAlignment,
                                              Wrap myWrap,
                                              CodeStyleSettings mySettings,
                                              GosuBlock block) {
    PsiElement blockPsi = block.getNode().getPsi();
//    //For binary expressions
//    if (blockPsi instanceof GosuBinaryExpression &&
//        !(blockPsi.getParent() instanceof GrBinaryExpression)) {
//      return generateForBinaryExpr(node, myWrap, mySettings);
//    }
//
//    //For multiline strings
//    if ((block.getNode().getElementType() == mSTRING_LITERAL ||
//        block.getNode().getElementType() == mGSTRING_LITERAL) &&
//        block.getTextRange().equals(block.getNode().getTextRange())) {
//      String text = block.getNode().getText();
//      if (text.length() > 6) {
//        if (text.substring(0, 3).equals("'''") && text.substring(text.length() - 3).equals("'''") ||
//            text.substring(0, 3).equals("\"\"\"") & text.substring(text.length() - 3).equals("\"\"\"")) {
//          return generateForMultiLineString(block.getNode(), myAlignment, myWrap, mySettings);
//        }
//      }
//    }
//
//    if (block.getNode().getElementType() == mGSTRING_BEGIN &&
//        block.getTextRange().equals(block.getNode().getTextRange())) {
//      String text = block.getNode().getText();
//      if (text.length() > 3) {
//        if (text.substring(0, 3).equals("\"\"\"")) {
//          return generateForMultiLineGStringBegin(block.getNode(), myAlignment, myWrap, mySettings);
//        }
//      }
//
//    }
//
//    //for gstrings
//    if (block.getNode().getElementType() == GSTRING) {
//      final ArrayList<Block> subBlocks = new ArrayList<Block>();
//      ASTNode[] children = getGosuChildren(node);
//      ASTNode prevChildNode = null;
//      for (ASTNode childNode : children) {
//        if (childNode.getTextRange().getLength() > 0) {
//          final Indent indent = GroovyIndentProcessor.getChildIndent(block, prevChildNode, childNode);
//          subBlocks.add(new GroovyBlock(childNode, myAlignment, indent, myWrap, mySettings));
//        }
//        prevChildNode = childNode;
//      }
//      return subBlocks;
//    }
//
//    //For nested selections
//    if (NESTED.contains(block.getNode().getElementType()) &&
//        blockPsi.getParent() != null &&
//        blockPsi.getParent().getNode() != null &&
//        !NESTED.contains(blockPsi.getParent().getNode().getElementType())) {
//      return generateForNestedExpr(node, myAlignment, myWrap, mySettings);
//    }
//
//    // For Parameter lists
//    if (isListLikeClause(blockPsi)) {
//      final ArrayList<Block> subBlocks = new ArrayList<Block>();
//      ASTNode[] children = node.getChildren(null);
//      ASTNode prevChildNode = null;
//      final Alignment alignment = mustAlign(blockPsi, mySettings, children) ? Alignment.createAlignment() : null;
//      for (ASTNode childNode : children) {
//        if (canBeCorrectBlock(childNode)) {
//          final Indent indent = GroovyIndentProcessor.getChildIndent(block, prevChildNode, childNode);
//          subBlocks.add(new GroovyBlock(childNode, isKeyword(childNode) ? null : alignment, indent, myWrap, mySettings));
//          prevChildNode = childNode;
//        }
//      }
//      return subBlocks;
//    }
//
    // For other cases
    final ArrayList<Block> subBlocks = new ArrayList<Block>();
    ASTNode[] children = node.getChildren( null );
    ASTNode prevChildNode = null;
    for (ASTNode childNode : children) {
      if (canBeCorrectBlock(childNode)) {
        final Indent indent = GosuIndentProcessor.getChildIndent(block, prevChildNode, childNode);
        subBlocks.add(new GosuBlock(childNode, blockPsi instanceof GosuStatementListImpl ? null : myAlignment, indent, myWrap, mySettings));
        prevChildNode = childNode;
      }
    }
    return subBlocks;
  }




  /**
   * @param node Tree node
   * @return true, if the current node can be myBlock node, else otherwise
   */
  private static boolean canBeCorrectBlock(final ASTNode node) {
    return (node.getText().trim().length() > 0);
  }
}
