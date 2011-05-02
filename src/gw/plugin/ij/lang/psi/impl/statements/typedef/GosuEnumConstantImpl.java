package gw.plugin.ij.lang.psi.impl.statements.typedef;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.statements.arguments.IGosuArgumentList;
import gw.plugin.ij.lang.psi.api.statements.arguments.IGosuNamedArgument;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuEnumConstant;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.statements.GosuFieldImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFieldStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuEnumConstantImpl extends GosuFieldImpl implements GosuEnumConstant, PsiPolyVariantReference
{
  public GosuEnumConstantImpl( GosuCompositeElement node )
  {
    super( node );
  }

  public GosuEnumConstantImpl( GosuFieldStub stub )
  {
    super( stub, GosuElementTypes.ENUM_CONSTANT );
  }

  public boolean hasModifierProperty( @NonNls @NotNull String property )
  {
    if( property.equals( PsiModifier.STATIC ) )
    {
      return true;
    }
    if( property.equals( PsiModifier.PUBLIC ) )
    {
      return true;
    }
    if( property.equals( PsiModifier.FINAL ) )
    {
      return true;
    }
    return false;
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitEnumConstant( this );
  }

  @Nullable
  public GosuTypeElement getTypeElementGosu()
  {
    return null;
  }

  @NotNull
  public PsiType getType()
  {
    return JavaPsiFacade.getInstance( getProject() ).getElementFactory().createType( getContainingClass(), PsiSubstitutor.EMPTY );
  }

  public void setType( @Nullable PsiType type )
  {
    throw new RuntimeException( "Cannot set type for enum constant" );
  }

  @Nullable
  public IGosuExpression getInitializerGosu()
  {
    return null;
  }

  public boolean isProperty()
  {
    return false;
  }

//  @NotNull
//  public GosuResolveResult resolveConstructorGenerics()
//  {
//    return PsiImplUtil.extractUniqueResult( multiResolveConstructor() );
//  }
//
//  public GosuResolveResult[] multiResolveConstructor()
//  {
//    return multiResolveConstructorImpl( false );
//  }
//
//  private GosuResolveResult[] multiResolveConstructorImpl( boolean allVariants )
//  {
//    PsiType[] argTypes = PsiUtil.getArgumentTypes( getFirstChild(), false );
//    PsiClass clazz = getContainingClass();
//    PsiType thisType = JavaPsiFacade.getInstance( getProject() ).getElementFactory().createType( clazz, PsiSubstitutor.EMPTY );
//    MethodResolverProcessor processor =
//      new MethodResolverProcessor( clazz.getName(), this, true, thisType, argTypes, PsiType.EMPTY_ARRAY, allVariants );
//    clazz.processDeclarations( processor, ResolveState.initial(), null, this );
//    return processor.getCandidates();
//  }

  public GosuResolveResult[] multiResolveClass()
  {
    throw new UnsupportedOperationException();
    //## todo:
//    final PsiClass psiClass = getContainingClass();
//    GosuResolveResult result = new GosuResolveResultImpl( psiClass, this, PsiSubstitutor.EMPTY, true, true );
//    return new GosuResolveResult[]{result};
  }

  @Nullable
  public IGosuArgumentList getArgumentList()
  {
    return findChildByClass( IGosuArgumentList.class );
  }

  public IGosuExpression removeArgument( final int number )
  {
    final IGosuArgumentList list = getArgumentList();
    return list != null ? list.removeArgument( number ) : null;
  }

  public IGosuNamedArgument addNamedArgument( final IGosuNamedArgument namedArgument ) throws IncorrectOperationException
  {
    return null;
  }

  public IGosuNamedArgument[] getNamedArguments()
  {
    final IGosuArgumentList argumentList = getArgumentList();
    return argumentList == null ? IGosuNamedArgument.EMPTY_ARRAY : argumentList.getNamedArguments();
  }

  public IGosuExpression[] getExpressionArguments()
  {
    final IGosuArgumentList argumentList = getArgumentList();
    return argumentList == null ? IGosuExpression.EMPTY_ARRAY : argumentList.getExpressionArguments();

  }

//  @NotNull
//  @Override
//  public GosuResolveResult[] getCallVariants( @Nullable IGosuExpression upToArgument )
//  {
//    return multiResolveConstructorImpl( true );
//  }
//
//  @NotNull
//  @Override
//  public GosuClosableBlock[] getClosureArguments()
//  {
//    return GosuClosableBlock.EMPTY_ARRAY;
//  }

//  @Override
//  public PsiMethod resolveMethod()
//  {
//    return PsiImplUtil.extractUniqueElement( multiResolveConstructor() );
//  }

  @Override
  public PsiReference getReference()
  {
    return this;
  }

  @NotNull
  public ResolveResult[] multiResolve( boolean incompleteCode )
  {
    throw new UnsupportedOperationException();
//    return multiResolveConstructor();
  }

  public PsiElement getElement()
  {
    return this;
  }

  public TextRange getRangeInElement()
  {
    return getNameIdentifierGosu().getTextRange().shiftRight( -getTextOffset() );
  }

  public PsiElement resolve()
  {
    throw new UnsupportedOperationException();
//    return resolveMethod();
  }

//  @NotNull
//  @Override
//  public GosuResolveResult advancedResolve()
//  {
//    return resolveConstructorGenerics();
//  }

  @NotNull
  public String getCanonicalText()
  {
    return getText(); //todo
  }

  public PsiElement handleElementRename( String newElementName ) throws IncorrectOperationException
  {
    return getElement();
  }

  public PsiElement bindToElement( @NotNull PsiElement element ) throws IncorrectOperationException
  {
    throw new IncorrectOperationException( "invalid operation" );
  }

  public boolean isReferenceTo( PsiElement element )
  {
    return element instanceof GosuMethod && ((GosuMethod)element).isConstructor() && getManager().areElementsEquivalent( resolve(), element );
  }

  @NotNull
  public Object[] getVariants()
  {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public boolean isSoft()
  {
    return false;
  }
}
