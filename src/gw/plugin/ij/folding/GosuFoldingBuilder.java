package gw.plugin.ij.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.impl.source.tree.FileElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFoldingBuilder implements FoldingBuilder, DumbAware {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
    List<FoldingDescriptor> folds = new ArrayList<FoldingDescriptor>();
    constructFolds(node, folds);
    return folds.toArray(new FoldingDescriptor[folds.size()]);
  }

  private void constructFolds(ASTNode node, List<FoldingDescriptor> folds) {
    for(ASTNode currentNode = node; currentNode != null; ) {
      if(currentNode.toString().contains("UsesStatementList")) {
        computeAndAddFold(folds, currentNode, "PsiWhiteSpace");
      } else if(currentNode.toString().contains("_multiline_comment_")) {
        if(currentNode.getText().contains("\n") || currentNode.getText().contains("\r")) {
          folds.add(new FoldingDescriptor(currentNode, currentNode.getTextRange()));
        }
      } else if(currentNode.toString().contains("method definition")) {
        computeAndAddFold(folds, currentNode, "{");
      } else if(currentNode.toString().contains("class definition")) {
        if(!(currentNode.getTreeParent() instanceof FileElement)) {
          computeAndAddFold(folds, currentNode, "{");
        }
        ASTNode child = currentNode.getFirstChildNode();
        if(child != null) {
          constructFolds(child, folds);
        }
      } else {
        ASTNode child = currentNode.getFirstChildNode();
        if(child != null) {
          constructFolds(child, folds);
        }
      }

      currentNode = currentNode.getTreeNext();
    }
  }

  private void computeAndAddFold(List<FoldingDescriptor> folds, ASTNode currentNode, String headerTerminatorKey) {
    ASTNode candidate = findNodeByType(currentNode, headerTerminatorKey);
    int delta = candidate != null ? candidate.getStartOffset() - currentNode.getStartOffset() : 0;
    TextRange range = new TextRange(currentNode.getStartOffset() + delta, currentNode.getStartOffset() + currentNode.getTextLength());
    if( range.getLength() > 0 ) {
      folds.add(new FoldingDescriptor(currentNode, range));
    }
  }

  private ASTNode findNodeByType(ASTNode node, String type) {
    ASTNode candidate = null;
    for(ASTNode rover = node.getFirstChildNode(); rover != null && candidate == null; rover = rover.getTreeNext()) {
      if(rover.toString().contains(type)) {
        candidate = rover;
      } else {
        candidate = findNodeByType(rover, type);
      }
    }
    return candidate;
  }

  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    if(node.toString().contains("UsesStatementList")) {
      return "...";
    } else if(node.toString().contains("_multiline_comment_")) {
      return "/* ... */";
    } else if(node.toString().contains("method definition")) {
      return "{ ... }";
    } else if(node.toString().contains("class definition")) {
      return "{ ... }";
    }
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return node.toString().contains("UsesStatementList");
  }

}
