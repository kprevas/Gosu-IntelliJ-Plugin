package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.GosuTokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
*/
public class GosuIdentifierImpl extends LeafPsiElement implements PsiIdentifier, PsiJavaToken
{
  public GosuIdentifierImpl( IElementType tokType, CharSequence text )
  {
    super( tokType, text );
  }

  public IElementType getTokenType()
  {
    return GosuTokenTypes.TT_IDENTIFIER;
  }

  public void accept( @NotNull PsiElementVisitor visitor )
  {
    visitor.visitElement( this );
  }

  @Override
  public PsiElement replace( @NotNull PsiElement newElement ) throws IncorrectOperationException
  {
    PsiElement result = super.replace( newElement );

    // We want to reformat method parameters on method name change as well because there is a possible situation that they are aligned
    // and method name change breaks the alignment.
    // Example:
    //     public void test(int i,
    //                      int j) {}
    // Suppose we're renaming the method to test123. We get the following if parameter list is not reformatted:
    //     public void test123(int i,
    //                     int j) {}
    PsiElement methodCandidate = result.getParent();
    if( methodCandidate instanceof PsiMethod )
    {
      PsiMethod method = (PsiMethod)methodCandidate;
      CodeEditUtil.markToReformat( method.getParameterList().getNode(), true );
    }

    return result;
  }

  public String toString()
  {
    return "PsiIdentifier: " + getText();
  }
}
