package gw.plugin.ij.lang.psi.impl.statements.typedef.members;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodReceiver;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.ElementPresentationUtil;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.impl.PsiSuperMethodImplUtil;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.presentation.java.JavaPresentationUtil;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.ui.RowIcon;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import gw.lang.parser.IDynamicFunctionSymbol;
import gw.lang.parser.statements.IFunctionStatement;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameter;
import gw.plugin.ij.lang.psi.api.statements.params.IGosuParameterList;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMember;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuTypeElement;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameter;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameterList;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuDeclaredElementImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.PsiImplUtil;
import gw.plugin.ij.lang.psi.impl.statements.params.GosuParameterListImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuMethodBaseImpl<T extends NamedStub> extends GosuDeclaredElementImpl<IFunctionStatement, T> implements GosuMethod
{

  protected GosuMethodBaseImpl( final T stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  public GosuMethodBaseImpl( GosuCompositeElement node )
  {
    super( node );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitMethod( this );
  }

  public int getTextOffset()
  {
    return getNameIdentifierGosu().getTextRange().getStartOffset();
  }

  // Can be null in error state
  // @NotNull
  public PsiIdentifier getNameIdentifierGosu()
  {
    return (PsiIdentifier)findElement( this, GosuElementTypes.ELEM_TYPE_NameInDeclaration );
//    PsiElement child = getFirstChild();
//    while( child != null )
//    {
//      if( child.getNode().getElementType() == GosuElementTypes.ELEM_TYPE_NameInDeclaration )
//      {
//        return (PsiIdentifier)child;
//      }
//      child = child.getNextSibling();
//    }
//    throw new IllegalStateException( "No NameInDeclaration found in Gosu parse tree" );
  }

  @Nullable
  public IGosuParameter[] getParameters()
  {
    GosuParameterListImpl parameterList = findChildByClass( GosuParameterListImpl.class );
    if( parameterList != null )
    {
      return parameterList.getParameters();
    }

    return IGosuParameter.EMPTY_ARRAY;
  }

  public GosuTypeElement getReturnTypeElementGosu()
  {
    return findChildByClass( GosuTypeElement.class );
  }

  public boolean processDeclarations( @NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      PsiElement lastParent,
                                      @NotNull PsiElement place )
  {
    for( final GosuTypeParameter typeParameter : getTypeParameters() )
    {
      if( !processElement( processor, typeParameter, state ) )
      {
        return false;
      }
    }

    for( final IGosuParameter parameter : getParameters() )
    {
      if( !processElement( processor, parameter, state ) )
      {
        return false;
      }
    }

    processor.handleEvent( DECLARATION_SCOPE_PASSED, this );
    return true;
  }
  public static final PsiScopeProcessor.Event DECLARATION_SCOPE_PASSED = new PsiScopeProcessor.Event() {};
  boolean processElement( PsiScopeProcessor processor, PsiNamedElement namedElement, ResolveState state )
  {
    NameHint nameHint = processor.getHint( NameHint.KEY );
    String name = nameHint == null ? null : nameHint.getName( state );
    if( name == null || name.equals( namedElement.getName() ) )
    {
      return processor.execute( namedElement, state );
    }

    return true;

  }

  public IGosuMember[] getMembers()
  {
    return new IGosuMember[]{this};
  }

//  private static final Function<GosuMethod, PsiType> ourTypesCalculator = new NullableFunction<GosuMethod, PsiType>()
//  {
//    public PsiType fun( GosuMethod method )
//    {
//      PsiType nominal = method.getReturnType();
//      if( nominal != null && nominal.equals( PsiType.VOID ) )
//      {
//        return nominal;
//      }
//
//      if( GppTypeConverter.hasTypedContext( method ) )
//      {
//        if( nominal != null )
//        {
//          return nominal;
//        }
//
//        return PsiType.getJavaLangObject( method.getManager(), method.getResolveScope() );
//      }
//
//      PsiType inferred = getInferredType( method );
//      if( nominal == null )
//      {
//        if( inferred == null )
//        {
//          return PsiType.getJavaLangObject( method.getManager(), method.getResolveScope() );
//        }
//        return inferred;
//      }
//      if( inferred != null && inferred != PsiType.NULL )
//      {
//        if( inferred instanceof PsiClassType && nominal instanceof PsiClassType )
//        {
//          final PsiClassType.ClassResolveResult declaredResult = ((PsiClassType)nominal).resolveGenerics();
//          final PsiClass declaredClass = declaredResult.getElement();
//          if( declaredClass != null )
//          {
//            final PsiClassType.ClassResolveResult initializerResult = ((PsiClassType)inferred).resolveGenerics();
//            final PsiClass initializerClass = initializerResult.getElement();
//            if( initializerClass != null &&
//                com.intellij.psi.util.PsiUtil.isRawSubstitutor( initializerClass, initializerResult.getSubstitutor() ) )
//            {
//              if( declaredClass == initializerClass )
//              {
//                return nominal;
//              }
//              final PsiSubstitutor declaredResultSubstitutor = declaredResult.getSubstitutor();
//              final PsiSubstitutor superSubstitutor =
//                TypeConversionUtil.getClassSubstitutor( declaredClass, initializerClass, declaredResultSubstitutor );
//
//              if( superSubstitutor != null )
//              {
//                return JavaPsiFacade.getInstance( method.getProject() ).getElementFactory()
//                  .createType( declaredClass, TypesUtil.composeSubstitutors( declaredResultSubstitutor, superSubstitutor ) );
//              }
//            }
//          }
//        }
//        if( nominal.isAssignableFrom( inferred ) )
//        {
//          return inferred;
//        }
//      }
//      return nominal;
//    }
//
//    @Nullable
//    private PsiType getInferredType( GosuMethod method )
//    {
//      final GosuOpenBlock block = method.getBlock();
//      if( block == null )
//      {
//        return null;
//      }
//
//      if( GosuPsiManager.isTypeBeingInferred( method ) )
//      {
//        return null;
//      }
//
//      return GosuPsiManager.inferType( method, new MethodTypeInferencer( block ) );
//    }
//  };

  @Nullable
  public PsiType getReturnType()
  {
    if( isConstructor() )
    {
      return null;
    }

    final GosuTypeElement element = getReturnTypeElementGosu();
    if( element != null )
    {
      return element.getType();
    }

    return JavaPsiFacade.getInstance( getManager().getProject() ).getElementFactory().createTypeByFQClassName( void.class.getName(), getResolveScope() );
  }

  @Nullable
  public GosuTypeElement setReturnType( @Nullable PsiType newReturnType )
  {
    //## todo:
    return null;
//    GosuTypeElement typeElement = getReturnTypeElementGosu();
//    if( newReturnType == null )
//    {
//      if( typeElement != null )
//      {
//        typeElement.delete();
//      }
//      return null;
//    }
//    GosuTypeElement newTypeElement = GosuPsiElementFactory.getInstance( getProject() ).createTypeElement( newReturnType );
//    if( typeElement == null )
//    {
//      IGosuModifierList list = getModifierList();
//      newTypeElement = (GosuTypeElement)addAfter( newTypeElement, list );
//    }
//    else
//    {
//      newTypeElement = (GosuTypeElement)typeElement.replace( newTypeElement );
//    }
//
//    newTypeElement.accept( new GosuRecursiveElementVisitor()
//    {
//      @Override
//      public void visitCodeReferenceElement( GosuCodeReferenceElement refElement )
//      {
//        super.visitCodeReferenceElement( refElement );
//        PsiUtil.shortenReference( refElement );
//      }
//    } );
//    return newTypeElement;
  }

  @Override
  public Icon getIcon( int flags )
  {
    RowIcon baseIcon = createLayeredIcon( GosuIcons.METHOD, ElementPresentationUtil.getFlags( this, false ) );
    return ElementPresentationUtil.addVisibilityIcon( this, flags, baseIcon );
  }

  @Override
  public ItemPresentation getPresentation()
  {
    return JavaPresentationUtil.getMethodPresentation( this );
  }

  @Nullable
  public PsiTypeElement getReturnTypeElement()
  {
    return null;
  }

  @NotNull
  public IGosuParameterList getParameterList()
  {
    IGosuParameterList parameterList = (IGosuParameterList)findElement( this, GosuElementTypes.PARAMETERS_LIST );
    assert parameterList != null;
    return parameterList;
  }

  @NotNull
  public PsiReferenceList getThrowsList()
  {
    return null;
//    GosuThrowsClause clause = findChildByClass( GosuThrowsClause.class );
//    assert clause != null : this;
//    return clause;
  }

  @Nullable
  public PsiCodeBlock getBody()
  {
    return findChildByClass( PsiCodeBlock.class );
  }

  public boolean isConstructor()
  {
    IDynamicFunctionSymbol dfs = getParsedElement().getDynamicFunctionSymbol();
    return dfs != null && dfs.isConstructor();
  }

  public boolean isVarArgs()
  {
    IGosuParameter[] parameters = getParameters();
    return parameters.length > 0 && parameters[parameters.length - 1].isVarArgs();
  }

  @NotNull
  public MethodSignature getSignature( @NotNull PsiSubstitutor substitutor )
  {
    return MethodSignatureBackedByPsiMethod.create( this, substitutor );
  }

  @Nullable
  public PsiIdentifier getNameIdentifier()
  {
    return getNameIdentifierGosu();
  }

  @NotNull
  public PsiMethod[] findDeepestSuperMethods()
  {
    return PsiSuperMethodImplUtil.findDeepestSuperMethods( this );
  }

  @NotNull
  public PsiMethod[] findSuperMethods( boolean checkAccess )
  {
    return PsiSuperMethodImplUtil.findSuperMethods( this, checkAccess );

    /*PsiClass containingClass = getContainingClass();

    Set<PsiMethod> methods = new HashSet<PsiMethod>();
    findSuperMethodRecursively(methods, containingClass, false, new HashSet<PsiClass>(), createMethodSignature(this),
                                new HashSet<MethodSignature>());

    return methods.toArray(new PsiMethod[methods.size()]);*/
  }

  @NotNull
  public PsiMethod[] findSuperMethods( PsiClass parentClass )
  {
    return PsiSuperMethodImplUtil.findSuperMethods( this, parentClass );
  }

  @NotNull
  public List<MethodSignatureBackedByPsiMethod> findSuperMethodSignaturesIncludingStatic( boolean checkAccess )
  {
    return PsiSuperMethodImplUtil.findSuperMethodSignaturesIncludingStatic( this, checkAccess );
  }

  @NotNull
  public PsiMethod[] findSuperMethods()
  {
    return PsiSuperMethodImplUtil.findSuperMethods( this );
  }

  @Nullable
  public PsiMethod findDeepestSuperMethod()
  {
    final PsiMethod[] methods = findDeepestSuperMethods();
    if( methods.length > 0 )
    {
      return methods[0];
    }
    return null;
  }

  @NotNull
  public IGosuModifierList getModifierList()
  {
    return this;
  }

  public boolean hasModifierProperty( @NonNls @NotNull String name )
  {
    if( name.equals( PsiModifier.ABSTRACT ) )
    {
      final PsiClass containingClass = getContainingClass();
      if( containingClass != null && containingClass.isInterface() )
      {
        return true;
      }
    }

    return super.hasModifierProperty( name );
  }

  @NotNull
  public String getName()
  {
    return PsiImplUtil.getName( this );
  }

  @NotNull
  public HierarchicalMethodSignature getHierarchicalMethodSignature()
  {
    return PsiSuperMethodImplUtil.getHierarchicalMethodSignature( this );
  }

  public PsiElement setName( @NonNls @NotNull String name ) throws IncorrectOperationException
  {
    //## todo:
    return null;

//    PsiImplUtil.setName( name, getNameIdentifierGosu() );
//    return this;
  }

  public boolean hasTypeParameters()
  {
    return getTypeParameters().length > 0;
  }

  @Nullable
  public GosuTypeParameterList getTypeParameterList()
  {
    return findChildByClass( GosuTypeParameterList.class );
  }

  @NotNull
  public GosuTypeParameter[] getTypeParameters()
  {
    final GosuTypeParameterList list = getTypeParameterList();
    if( list != null )
    {
      return list.getTypeParameters();
    }

    return GosuTypeParameter.EMPTY_ARRAY;
  }

  public PsiClass getContainingClass()
  {
    PsiElement parent = getParent();
    if( parent instanceof PsiClass )
    {
      return (PsiClass)parent;
    }

    final PsiFile file = getContainingFile();
    if( file instanceof GosuFileBase )
    {
      return ((GosuFileBase)file).getPsiClass();
    }

    return null;
  }

  @Nullable
  public PsiDocComment getDocComment()
  {
    return null;
    //return GosuDocCommentUtil.findDocComment( this );
  }

  public boolean isDeprecated()
  {
    return false;
  }

  @NotNull
  public SearchScope getUseScope()
  {
    return com.intellij.psi.impl.PsiImplUtil.getMemberUseScope( this );
  }

  public PsiElement getOriginalElement()
  {
    final PsiClass containingClass = getContainingClass();
    if( containingClass == null )
    {
      return this;
    }
    PsiClass originalClass = (PsiClass)containingClass.getOriginalElement();
    final PsiMethod originalMethod = originalClass.findMethodBySignature( this, false );
    return originalMethod != null ? originalMethod : this;
  }


  public void delete() throws IncorrectOperationException
  {
    PsiElement parent = getParent();
    if( parent instanceof GosuClassFileImpl || parent instanceof GosuTypeDefinition )
    {
      super.delete();
      return;
    }
    throw new IncorrectOperationException( "Invalid enclosing type definition" );
  }

  public String[] getNamedParametersArray()
  {
    return ArrayUtil.EMPTY_STRING_ARRAY;
//    GosuOpenBlock body = getBlock();
//    if( body == null )
//    {
//      return ArrayUtil.EMPTY_STRING_ARRAY;
//    }
//
//    IGosuParameter[] parameters = getParameters();
//    if( parameters.length == 0 )
//    {
//      return ArrayUtil.EMPTY_STRING_ARRAY;
//    }
//    IGosuParameter firstParameter = parameters[0];
//
//    PsiType type = firstParameter.getTypeGosu();
//    GosuTypeElement typeElement = firstParameter.getTypeElementGosu();
//    //equalsToText can't be called here because of stub creating
//
//    if( type != null && typeElement != null && type.getPresentableText() != null && !type.getPresentableText().endsWith( "Map" ) )
//    {
//      return ArrayUtil.EMPTY_STRING_ARRAY;
//    }
//
//    GosuNamedArgumentSearchVisitor visitor = new GosuNamedArgumentSearchVisitor( firstParameter.getNameIdentifierGosu().getText() );
//
//    body.accept( visitor );
//    return visitor.getResult();
  }

  public PsiMethodReceiver getMethodReceiver()
  {
    return null;
  }

  public PsiType getReturnTypeNoResolve()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEquivalentTo( PsiElement another )
  {
    return PsiClassImplUtil.isMethodEquivalentTo( this, another );
  }
}