package gw.plugin.ij.lang.psi.impl.expressions;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaResolveResult;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaReference;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.ConstructorFilter;
import com.intellij.psi.filters.NotFilter;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.source.resolve.ClassResolverProcessor;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.impl.source.resolve.VariableResolverProcessor;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.impl.source.tree.SourceUtil;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.scope.ElementClassFilter;
import com.intellij.psi.scope.MethodProcessorSetupFailedException;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.processor.FilterScopeProcessor;
import com.intellij.psi.scope.processor.MethodResolverProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import gnu.trove.THashSet;
import gw.lang.parser.IParsedElement;
import gw.lang.reflect.gs.IGosuClass;
import gw.plugin.ij.codeInsight.GosuTargetElementEvaluator;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.GosuResolveResult;
import gw.plugin.ij.lang.psi.api.expressions.IGosuExpression;
import gw.plugin.ij.lang.psi.api.expressions.IGosuReferenceExpression;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuReferenceExpressionImpl<T extends IParsedElement> extends GosuPsiElementImpl<T> implements IGosuReferenceExpression
{
  private String _cachedTextSkipWhiteSpaceAndComments;

  public GosuReferenceExpressionImpl( GosuCompositeElement<T> node )
  {
    super( node );
  }

  public PsiReference getReference()
  {
    return this;
  }

  public String getReferenceName()
  {
    PsiElement nameElement = getReferenceNameElement();
    if( nameElement != null )
    {
      return nameElement.getText();
    }
    return null;
  }

  abstract public PsiElement getReferenceNameElement();

  public PsiElement getElement()
  {
    return this;
  }

  public TextRange getRangeInElement()
  {
    final PsiElement refNameElement = getReferenceNameElement();
    if( refNameElement != null )
    {
      final int offsetInParent = refNameElement.getStartOffsetInParent();
      return new TextRange( offsetInParent, offsetInParent + refNameElement.getTextLength() );
    }
    return new TextRange( 0, getTextLength() );
  }

  abstract public PsiElement resolve();

  protected PsiElement resolveType( String strFullName )
  {
    if( strFullName != null )
    {
      final JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
//      if( getContext() != null )
//      {
//        return facade.getResolveHelper().resolveReferencedClass( strFullName, getContext() );
//      }
//      else
//      {
      return facade.findClass( strFullName, getResolveScope() );
//      }
    }
    else
    {
      return null;
    }
  }

  protected PsiElement resolveField( String strField )
  {
    return resolveField( strField, getContext() );
  }

  protected PsiElement resolveField( String strField, IGosuClass gsClass )
  {
    return resolveField( strField, resolveType( gsClass.getName() ) );
  }

  protected PsiElement resolveField( String strField, String strFqn )
  {
    return resolveField( strField, resolveType( strFqn ) );
  }

  protected PsiElement resolveField( String strField, PsiElement context )
  {

    if( strField != null )
    {
      final JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
      if( getContext() != null )
      {
        return facade.getResolveHelper().resolveReferencedVariable( strField, context );
      }
      else
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

  protected CandidateInfo[] resolveMethod( PsiCallExpression callExpr )
  {
    JavaPsiFacade facade = JavaPsiFacade.getInstance( getProject() );
    return facade.getResolveHelper().getReferencedMethodCandidates( callExpr, false );
  }

  protected PsiElement handleElementRenameInner( String newElementName ) throws IncorrectOperationException
  {
    //## todo:  this will be nice

//    PsiElement nameElement = getReferenceNameElement();
//    if( nameElement != null )
//    {
//      ASTNode node = nameElement.getNode();
//      ASTNode newNameNode = GosuPsiElementFactory.getInstance( getProject() ).createReferenceNameFromText( newElementName ).getNode();
//      assert newNameNode != null && node != null;
//      node.getTreeParent().replaceChild( node, newNameNode );
//    }

    return this;
  }

  public PsiElement handleElementRename( String newElementName ) throws IncorrectOperationException
  {
    return handleElementRenameInner( newElementName );
  }

  public PsiElement bindToElement( PsiElement element ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @NotNull
  public String getCanonicalText()
  {
    return getRangeInElement().substring( getElement().getText() );
  }

  public boolean isReferenceTo( PsiElement element )
  {
    return getManager().areElementsEquivalent( element, GosuTargetElementEvaluator.correctSearchTargets( resolve() ) );
  }

  @NotNull
  public Object[] getVariants()
  {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public void processVariants( PsiScopeProcessor processor ){
    OrFilter filter = new OrFilter();
    filter.addFilter( ElementClassFilter.CLASS );
    if( isQualified() )
    {
      filter.addFilter( ElementClassFilter.PACKAGE_FILTER );
    }
    filter.addFilter( new AndFilter( ElementClassFilter.METHOD, new NotFilter( new ConstructorFilter() ) ) );
    filter.addFilter( ElementClassFilter.VARIABLE );

    FilterScopeProcessor proc =
      new FilterScopeProcessor( filter, processor )
      {
        private final Set<String> myVarNames = new THashSet<String>();

        @Override
        public boolean execute( final PsiElement element, final ResolveState state )
        {
          if( element instanceof PsiLocalVariable || element instanceof PsiParameter )
          {
            myVarNames.add( ((PsiVariable)element).getName() );
          }
          else if( element instanceof PsiField && myVarNames.contains( ((PsiVariable)element).getName() ) )
          {
            return true;
          }
          else if( element instanceof PsiClass && seemsScrambled( (PsiClass)element ) )
          {
            return true;
          }

          return super.execute( element, state );
        }

      };
    PsiScopesUtil.resolveAndWalk( proc, this, null, true );
  }

  private static boolean seemsScrambled( PsiClass element )
  {
    if( !(element instanceof PsiCompiledElement) )
    {
      return false;
    }

    final String qualifiedName = element.getQualifiedName();
    return qualifiedName != null &&
           qualifiedName.length() <= 2 &&
           qualifiedName.length() > 0 &&
           Character.isLowerCase( qualifiedName.charAt( 0 ) );
  }

  public boolean isSoft()
  {
    return false;
  }

  @NotNull
  public GosuResolveResult[] multiResolve( boolean incomplete )
  {
//    //## todo: actual multiResolve impl
//    return new GosuResolveResult[]{new GosuResolveResultImpl( resolve(), !incomplete )};

    final PsiManagerEx manager = getManager();
    if (manager == null) {
      return null;
    }
    ResolveResult[] results = manager.getResolveCache().resolveWithCaching(this, OurGenericsResolver.INSTANCE, true, incomplete);
    return makeGosuResolveResults( (ResolveResult[])results );
  }

  protected GosuResolveResult[] makeGosuResolveResults( ResolveResult[] results )
  {
    GosuResolveResult[] gsResults = new GosuResolveResult[results.length];
    for( int i = 0; i < results.length; i++ )
    {
      final ResolveResult r = results[i];
      gsResults[i] = new JavaToGosuResolveResult( (JavaResolveResult)r );
    }
    return gsResults;
  }

  public IGosuExpression replaceWithExpression( IGosuExpression expression, boolean removeUnnecessaryParentheses )
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @Override
  public abstract PsiReferenceParameterList getParameterList();

  @NotNull
  @Override
  public PsiType[] getTypeParameters()
  {
    return new PsiType[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getQualifiedName()
  {
    throw new UnsupportedOperationException( "Men at work" );
  }

  @NotNull
  @Override
  public JavaResolveResult advancedResolve(boolean incompleteCode) {
    final JavaResolveResult[] results = multiResolve( incompleteCode );
    if (results.length == 1) return results[0];
    return JavaResolveResult.EMPTY;
  }

  private JavaResolveResult[] resolve(IElementType parentType) {
    if (parentType == null) {
      parentType = getNode().getTreeParent() != null ? getNode().getTreeParent().getElementType() : null;
    }
    if (parentType == JavaElementType.REFERENCE_EXPRESSION) {
      JavaResolveResult[] result = resolveToVariable();
      if (result.length > 0) {
        return result;
      }

      final PsiElement classNameElement = getReferenceNameElement();
      if (!(classNameElement instanceof PsiIdentifier)) return JavaResolveResult.EMPTY_ARRAY;
      result = resolveToClass(classNameElement);
      if (result.length > 0) {
        return result;
      }

      return resolveToPackage();
    }
    if (parentType == JavaElementType.METHOD_CALL_EXPRESSION) {
      return resolveToMethod();
    }
    return resolveToVariable();
  }

  private JavaResolveResult[] resolveToMethod() {
    final PsiMethodCallExpression methodCall = (PsiMethodCallExpression)getParent();
    final MethodResolverProcessor processor = new MethodResolverProcessor(methodCall);
    try {
      PsiScopesUtil.setupAndRunProcessor(processor, methodCall, false);
    }
    catch (MethodProcessorSetupFailedException e) {
      return JavaResolveResult.EMPTY_ARRAY;
    }
    return processor.getResult();
  }

  private JavaResolveResult[] resolveToPackage() {
    final String packageName = getCachedTextSkipWhiteSpaceAndComments();
    final PsiManager manager = getManager();
    final PsiPackage aPackage = JavaPsiFacade.getInstance(manager.getProject()).findPackage(packageName);
    if (aPackage == null) {
      return JavaPsiFacade.getInstance(manager.getProject()).isPartOfPackagePrefix(packageName)
             ? CandidateInfo.RESOLVE_RESULT_FOR_PACKAGE_PREFIX_PACKAGE
             : JavaResolveResult.EMPTY_ARRAY;
    }
    return new JavaResolveResult[]{new CandidateInfo(aPackage, PsiSubstitutor.EMPTY)};
  }

  private JavaResolveResult[] resolveToClass(PsiElement classNameElement) {
    final String className = classNameElement.getText();

    final ClassResolverProcessor processor = new ClassResolverProcessor(className, this);
    PsiScopesUtil.resolveAndWalk(processor, this, null);
    return processor.getResult();
  }

  private JavaResolveResult[] resolveToVariable() {
    final VariableResolverProcessor processor = new VariableResolverProcessor(this);
    PsiScopesUtil.resolveAndWalk(processor, this, null);
    return processor.getResult();
  }

  private static final class OurGenericsResolver implements ResolveCache.PolyVariantResolver<PsiJavaReference> {
    public static final OurGenericsResolver INSTANCE = new OurGenericsResolver();

    private static JavaResolveResult[] _resolve(boolean incompleteCode, GosuReferenceExpressionImpl expression) {
      CompositeElement treeParent = expression.getNode().getTreeParent();
      IElementType parentType = treeParent != null ? treeParent.getElementType() : null;
      final JavaResolveResult[] result = expression.resolve( parentType );

      if (incompleteCode && parentType != JavaElementType.REFERENCE_EXPRESSION && result.length == 0) {
        return expression.resolve( JavaElementType.REFERENCE_EXPRESSION );
      }
      return result;
    }

    public JavaResolveResult[] resolve(PsiJavaReference ref, boolean incompleteCode) {
      final JavaResolveResult[] result = _resolve(incompleteCode, (GosuReferenceExpressionImpl)ref );
      if (result.length > 0 && result[0].getElement() instanceof PsiClass) {
        final PsiType[] parameters = ((PsiJavaCodeReferenceElement)ref).getTypeParameters();
        final JavaResolveResult[] newResult = new JavaResolveResult[result.length];
        for (int i = 0; i < result.length; i++) {
          final CandidateInfo resolveResult = (CandidateInfo)result[i];
          newResult[i] = new CandidateInfo(resolveResult, resolveResult.getSubstitutor().putAll(
            (PsiClass)resolveResult.getElement(), parameters));
        }
        return newResult;
      }
      return result;
    }
  }

  private String getCachedTextSkipWhiteSpaceAndComments() {
    String whiteSpaceAndComments = _cachedTextSkipWhiteSpaceAndComments;
    if (whiteSpaceAndComments == null) {
      _cachedTextSkipWhiteSpaceAndComments = whiteSpaceAndComments = SourceUtil.getTextSkipWhiteSpaceAndComments( getNode() );
    }
    return whiteSpaceAndComments;
  }
}
