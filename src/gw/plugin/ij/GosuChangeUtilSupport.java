package gw.plugin.ij;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.codeStyle.ReferenceAdjuster;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.TreeCopyHandler;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import gw.plugin.ij.lang.psi.impl.expressions.GosuReferenceExpressionImpl;

import java.util.Map;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuChangeUtilSupport implements TreeCopyHandler {
  private static final Key<PsiMember> REFERENCED_MEMBER_KEY = Key.create("REFERENCED_MEMBER_KEY");

  public void encodeInformation(final TreeElement element, final ASTNode original, final Map<Object, Object> encodingState) {
//    if (original instanceof CompositeElement) {
//      if (original.getElementType() == GosuElementTypes.ELEM_TYPE_IIdentifierExpression ||
//          original.getElementType() == GosuElementTypes.ELEM_TYPE_TypeLiteral
//          ) {
//        final GosuResolveResult result = ((GosuReferenceExpressionImpl)original.getPsi()).advancedResolve();
//        if (result != null) {
//          final PsiElement target = result.getElement();
//
//          if (target instanceof PsiClass ||
//            (target instanceof PsiMethod || target instanceof PsiField) &&
//                   ((PsiMember) target).hasModifierProperty(PsiModifier.STATIC) &&
//                    result.getCurrentFileResolveContext() instanceof GrImportStatement) {
//            element.putCopyableUserData(REFERENCED_MEMBER_KEY, (PsiMember) target);
//          }
//        }
//      }
//    }
  }

  public TreeElement decodeInformation(TreeElement element, final Map<Object, Object> decodingState) {
    if (element instanceof CompositeElement) {
      if (element.getElementType() == GosuElementTypes.ELEM_TYPE_IIdentifierExpression ||
          element.getElementType() == GosuElementTypes.ELEM_TYPE_TypeLiteral) {
//        GosuReferenceExpressionImpl ref = (GosuReferenceExpressionImpl) SourceTreeToPsiMap.treeElementToPsi(element);
//        final PsiMember refMember = element.getCopyableUserData(REFERENCED_MEMBER_KEY);
//        if (refMember != null) {
//          element.putCopyableUserData(REFERENCED_MEMBER_KEY, null);
//          PsiElement refElement1 = ref.resolve();
//          if (!refMember.getManager().areElementsEquivalent(refMember, refElement1)) {
//            try {
//              if (!(refMember instanceof PsiClass) || ref.getQualifier() == null) {
//                // can restore only if short (otherwise qualifier should be already restored)
//                ref = (GosuReferenceExpressionImpl) ref.bindToElement(refMember);
//              }
//            } catch (IncorrectOperationException ignored) {
//            }
//            return (TreeElement) SourceTreeToPsiMap.psiElementToTree(ref);
//          } else {
//            // shorten references to the same package and to inner classes that can be accessed by short name
//            new ReferenceAdjuster(true, false).process(element, false, false);
//          }
//        }
        return element;
      }
    }
    return null;
  }

}