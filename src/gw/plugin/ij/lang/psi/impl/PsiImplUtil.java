package gw.plugin.ij.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureUtil;
import gw.plugin.ij.lang.psi.GosuNamedElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class PsiImplUtil
{
  private static final Logger LOG = Logger.getInstance("gw.plugin.ij.lang.psi.impl.PsiImplUtil");
  private static final String MAIN_METHOD = "main";

  private PsiImplUtil() {
  }

  @Nullable
  public static PsiElement realPrevious(PsiElement previousLeaf) {
    while (previousLeaf != null &&
        (previousLeaf instanceof PsiWhiteSpace ||
            previousLeaf instanceof PsiComment ||
            previousLeaf instanceof PsiErrorElement)) {
      previousLeaf = previousLeaf.getPrevSibling();
    }
    return previousLeaf;
  }

  public static boolean isExtendsSignature(MethodSignature superSignatureCandidate, MethodSignature subSignature) {
    return MethodSignatureUtil.isSubsignature(superSignatureCandidate, subSignature);
  }

  @Nullable
  public static PsiElement getOriginalElement(PsiClass clazz, PsiFile containingFile) {
    VirtualFile vFile = containingFile.getVirtualFile();
    final JavaPsiFacade facade = JavaPsiFacade.getInstance(clazz.getProject());
    final ProjectFileIndex idx = ProjectRootManager.getInstance(facade.getProject()).getFileIndex();

    if (vFile == null || !idx.isInLibrarySource(vFile)) return clazz;
    final String qName = clazz.getQualifiedName();
    if (qName == null) return null;
    final List<OrderEntry> orderEntries = idx.getOrderEntriesForFile(vFile);
    PsiClass original = facade.findClass(qName, new GlobalSearchScope(facade.getProject()) {
      public int compare(VirtualFile file1, VirtualFile file2) {
        return 0;
      }

      public boolean contains(VirtualFile file) {
        // order for file and vFile has non empty intersection.
        List<OrderEntry> entries = idx.getOrderEntriesForFile(file);
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < entries.size(); i++) {
          final OrderEntry entry = entries.get(i);
          if (orderEntries.contains(entry)) return true;
        }
        return false;
      }

      public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return false;
      }

      public boolean isSearchInLibraries() {
        return true;
      }
    });

    return original != null ? original : clazz;
  }

  public static PsiMethod[] mapToMethods(@Nullable List<CandidateInfo> list) {
    if (list == null) return PsiMethod.EMPTY_ARRAY;
    PsiMethod[] result = new PsiMethod[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = (PsiMethod) list.get(i).getElement();

    }
    return result;
  }

  public static String getName(GosuNamedElement namedElement) {
    PsiElement nameElement = namedElement.getNameIdentifierGosu();
    ASTNode node = nameElement.getNode();
    if( node == null )
    {
      return "";
    }
//    if (node.getElementType() == mIDENT) return nameElement.getText();
//    else {
//      if (node.getElementType() == mSTRING_LITERAL) {
//        String text = nameElement.getText();
//        return text.endsWith("'") ? text.substring(1, text.length() - 1) : text.substring(1);
//      } else {
//        LOG.assertTrue(node.getElementType() == mGSTRING_LITERAL);
//        String text = nameElement.getText();
//        return text.endsWith("\"") ? text.substring(1, text.length() - 1) : text.substring(1);
//      }
//    }
    return nameElement.getText();
  }

  public static boolean isMainMethod(GosuMethod method) {
    return method.getName().equals(MAIN_METHOD) &&
        method.hasModifierProperty(PsiModifier.STATIC);
  }
}
