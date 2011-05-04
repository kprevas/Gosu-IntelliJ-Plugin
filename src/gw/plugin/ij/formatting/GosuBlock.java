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
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.ILazyParseableElementType;
import gw.lang.parser.IExpression;
import gw.lang.parser.IParsedElement;
import gw.lang.parser.IStatement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuBaseElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class GosuBlock extends GosuElementTypes implements Block
{
  final protected ASTNode myNode;
  final protected Alignment myAlignment;
  final protected Indent myIndent;
  final protected Wrap myWrap;
  final protected CodeStyleSettings mySettings;

  protected List<Block> mySubBlocks = null;

  public GosuBlock( @NotNull final ASTNode node, @Nullable final Alignment alignment, @NotNull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings ) {
    myNode = node;
    myAlignment = alignment;
    myIndent = indent;
    myWrap = wrap;
    mySettings = settings;
  }

  @NotNull
  public ASTNode getNode() {
    return myNode;
  }

  @NotNull
  public CodeStyleSettings getSettings() {
    return mySettings;
  }

  @NotNull
  public TextRange getTextRange() {
    return myNode.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks() {
    if (mySubBlocks == null) {
      mySubBlocks = GosuBlockGenerator.generateSubBlocks( myNode, myAlignment, myWrap, mySettings, this );
    }
    return mySubBlocks;
  }

  @Nullable
  public Wrap getWrap() {
    return myWrap;
  }

  @Nullable
  public Indent getIndent() {
    return myIndent;
  }

  @Nullable
  public Alignment getAlignment() {
    return myAlignment;
  }

  @Nullable
  public Spacing getSpacing(Block child1, Block child2) {
    return null;
  }

  @NotNull
  public ChildAttributes getChildAttributes(final int newChildIndex) {
    ASTNode astNode = getNode();
    PsiElement psi = astNode.getPsi();
    if( psi instanceof GosuBaseElementImpl )
    {
      GosuBaseElementImpl gpsi = (GosuBaseElementImpl)psi;
      IParsedElement pe = gpsi.getParsedElement();
      if( pe instanceof IStatement )
      {
        return new ChildAttributes( Indent.getNormalIndent(), null );
      }
      else if( pe instanceof IExpression )
      {
        return new ChildAttributes( Indent.getContinuationWithoutFirstIndent(), null );
      }
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }

  public boolean isIncomplete() {
    return isIncomplete(myNode);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@NotNull final ASTNode node) {
    if (node.getElementType() instanceof ILazyParseableElementType) return false;
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
        !(lastChild.getElementType() instanceof ILazyParseableElementType) &&
        (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null && (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

  @Override
  public String toString() {
    return myNode.getTextRange() + ": " + myNode;
  }
}
