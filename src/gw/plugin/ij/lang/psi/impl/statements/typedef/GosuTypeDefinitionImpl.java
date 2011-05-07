package gw.plugin.ij.lang.psi.impl.statements.typedef;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.ElementBase;
import com.intellij.psi.impl.ElementPresentationUtil;
import com.intellij.psi.impl.InheritanceImplUtil;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.impl.PsiSuperMethodImplUtil;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.ui.RowIcon;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.VisibilityIcons;
import gw.lang.parser.statements.IClassStatement;
import gw.lang.reflect.IType;
import gw.plugin.ij.GosuIcons;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.api.auxilary.IGosuModifierList;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuExtendsClause;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuImplementsClause;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMembersDeclaration;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameter;
import gw.plugin.ij.lang.psi.api.types.GosuTypeParameterList;
import gw.plugin.ij.lang.psi.impl.GosuDeclaredElementImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.PsiImplUtil;
import gw.plugin.ij.lang.psi.impl.statements.GosuFieldImpl;
import gw.plugin.ij.lang.psi.stubs.GosuTypeDefinitionStub;
import gw.plugin.ij.lang.psi.util.GosuClassImplUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuTypeDefinitionImpl extends GosuDeclaredElementImpl<IClassStatement, GosuTypeDefinitionStub> implements GosuTypeDefinition
{
  private volatile PsiClass[] myInnerClasses;
  private volatile List<PsiMethod> myMethods;
  private IGosuField[] myFields;
  private volatile GosuMethod[] myGosuMethods;
  private volatile GosuMethod[] myConstructors;

  public GosuTypeDefinitionImpl( GosuCompositeElement<IClassStatement> node )
  {
    super( node );
  }

  protected GosuTypeDefinitionImpl( GosuTypeDefinitionStub stub, IStubElementType nodeType )
  {
    super( stub, nodeType );
  }

  public void accept( GosuElementVisitor visitor )
  {
    visitor.visitTypeDefinition( this );
  }

  public int getTextOffset()
  {
    return getNameIdentifierGosu().getTextRange().getStartOffset();
  }

  @Nullable
  public String getQualifiedName()
  {
//    return ((IClassStatement)((GosuCompositeElement)getNode()).getParsedElement()).getGosuClass().getName();

    GosuTypeDefinitionStub stub = getStub();
    if( stub != null )
    {
      return stub.getQualifiedName();
    }

    PsiElement parent = getParent();
    if( parent instanceof GosuFile )
    {
      String packageName = ((GosuFile)parent).getPackageName();
      return packageName.length() > 0 ? packageName + "." + getName() : getName();
    }

    final PsiClass containingClass = getContainingClass();
    if( containingClass != null )
    {
      return containingClass.getQualifiedName() + "." + getName();
    }

    return null;
  }

  @NotNull
  public GosuMembersDeclaration[] getMemberDeclarations()
  {
    return findChildrenByClass( GosuMembersDeclaration.class );
  }

  public ItemPresentation getPresentation()
  {
    return new ItemPresentation()
    {
      @Nullable
      public String getPresentableText()
      {
        return getName();
      }

      @Nullable
      public String getLocationString()
      {
        PsiFile file = getContainingFile();
        if( file instanceof GosuFile )
        {
          GosuFile gsFile = (GosuFile)file;

          return gsFile.getPackageName().length() > 0 ? "(" + gsFile.getPackageName() + ")" : "";
        }
        return "";
      }

      @Nullable
      public Icon getIcon( boolean open )
      {
        return GosuTypeDefinitionImpl.this.getIcon( ICON_FLAG_VISIBILITY | ICON_FLAG_READ_STATUS );
      }

      @Nullable
      public TextAttributesKey getTextAttributesKey()
      {
        return null;
      }
    };
  }

  @Nullable
  public GosuExtendsClause getExtendsClause()
  {
    return (GosuExtendsClause)findChildByType( GosuElementTypes.EXTENDS_CLAUSE );
  }

  @Nullable
  public GosuImplementsClause getImplementsClause()
  {
    return (GosuImplementsClause)findChildByType( GosuElementTypes.IMPLEMENTS_CLAUSE );
  }

  public String[] getSuperClassNames()
  {
//    final GosuTypeDefinitionStub stub = getStub();
//    if( stub != null )
//    {
//      return stub.getSuperClassNames();
//    }
    return ArrayUtil.mergeArrays( getExtendsNames(), getImplementsNames(), String.class );
  }

  protected String[] getImplementsNames()
  {
    List<? extends IType> interfaces = ((IClassStatement)((GosuCompositeElement)getNode()).getParsedElement()).getGosuClass().getInterfaces();
    if( interfaces != null )
    {
      String[] ifaces = new String[interfaces.size()];
      for( int i = 0; i < interfaces.size(); i++ )
      {
        ifaces[i] = interfaces.get( i ).getName();
      }
      return ifaces;
    }
    return new String[0];

//    GosuImplementsClause implementsClause = getImplementsClause();
//    GosuCodeReferenceElement[] implementsRefs =
//      implementsClause != null ? implementsClause.getReferenceElements() : GosuCodeReferenceElement.EMPTY_ARRAY;
//    ArrayList<String> implementsNames = new ArrayList<String>( implementsRefs.length );
//    for( GosuCodeReferenceElement ref : implementsRefs )
//    {
//      String name = ref.getReferenceName();
//      if( name != null )
//      {
//        implementsNames.add( name );
//      }
//    }
//
//    return ArrayUtil.toStringArray( implementsNames );
  }

  protected String[] getExtendsNames()
  {
    IType superType = ((IClassStatement)((GosuCompositeElement)getNode()).getParsedElement()).getGosuClass().getSupertype();
    if( superType != null )
    {
      return new String[]{superType.getName()};
    }
    return new String[0];

//    GosuExtendsClause extendsClause = getExtendsClause();
//    GosuCodeReferenceElement[] extendsRefs =
//      extendsClause != null ? extendsClause.getReferenceElements() : GosuCodeReferenceElement.EMPTY_ARRAY;
//    ArrayList<String> extendsNames = new ArrayList<String>( extendsRefs.length );
//    for( GosuCodeReferenceElement ref : extendsRefs )
//    {
//      String name = ref.getReferenceName();
//      if( name != null )
//      {
//        extendsNames.add( name );
//      }
//    }
//    return ArrayUtil.toStringArray( extendsNames );
  }

  @NotNull
  public PsiIdentifier getNameIdentifierGosu()
  {
    return getNameIdentifier();
  }

  public void checkDelete() throws IncorrectOperationException
  {
  }

  public void delete() throws IncorrectOperationException
  {
    PsiElement parent = getParent();
    if( parent instanceof GosuClassFileImpl )
    {
      GosuClassFileImpl file = (GosuClassFileImpl)parent;
      if( file.getTypeDefinitions().length == 1 )
      {
        file.delete();
        return;
      }
    }

    ASTNode astNode = parent.getNode();
    if( astNode != null )
    {
      astNode.removeChild( getNode() );
    }
  }

  public boolean processDeclarations( @NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @Nullable PsiElement lastParent,
                                      @NotNull PsiElement place )
  {
    //## todo:
    return PsiClassImplUtil.processDeclarationsInClass( this, processor, state, null, lastParent, place, false );
  }

  public String getName()
  {
    final GosuTypeDefinitionStub stub = getStub();
    if( stub != null )
    {
      return stub.getName();
    }
    return PsiImplUtil.getName( this );
  }

  @Override
  public boolean isEquivalentTo( PsiElement another )
  {
    return GosuClassImplUtil.isClassEquivalentTo( this, another );
  }

  public boolean isInterface()
  {
    return false;
  }

  public boolean isAnnotationType()
  {
    return false;
  }

  public boolean isEnum()
  {
    return false;
  }

  @Nullable
  public PsiReferenceList getExtendsList()
  {
    return null;
  }

  @Nullable
  public PsiReferenceList getImplementsList()
  {
    return null;
  }

  @NotNull
  public PsiClassType[] getExtendsListTypes()
  {
    return GosuClassImplUtil.getExtendsListTypes( this );
  }

  @NotNull
  public PsiClassType[] getImplementsListTypes()
  {
    return GosuClassImplUtil.getImplementsListTypes( this );
  }

  @Nullable
  public PsiClass getSuperClass()
  {
    return GosuClassImplUtil.getSuperClass( this );
  }

  public PsiClass[] getInterfaces()
  {
    return GosuClassImplUtil.getInterfaces( this );
  }

  @NotNull
  public final PsiClass[] getSupers()
  {
    return GosuClassImplUtil.getSupers( this );
  }

  @NotNull
  public PsiClassType[] getSuperTypes()
  {
    return GosuClassImplUtil.getSuperTypes( this );
  }

  @NotNull
  public PsiMethod[] getMethods()
  {
    List<PsiMethod> cached = myMethods;
    if( cached == null )
    {
      cached = new ArrayList<PsiMethod>();
      cached.addAll( Arrays.asList( findChildrenByClass( GosuMethod.class ) ) );

      myMethods = cached;
    }
    // yay
    List<PsiMethod> result = new ArrayList<PsiMethod>( cached );
    return result.toArray( new PsiMethod[result.size()] );
  }

  public void subtreeChanged()
  {
    myMethods = null;
    myInnerClasses = null;
    myConstructors = null;
    myGosuMethods = null;

    myFields = null;
    for( IGosuField field : getFields() )
    {
      ((GosuFieldImpl)field).clearCaches();
    }

    super.subtreeChanged();
  }

  @NotNull
  public PsiMethod[] getConstructors()
  {
    GosuMethod[] cached = myConstructors;
    if( cached == null )
    {
      List<GosuMethod> result = new ArrayList<GosuMethod>();
      for( final PsiMethod method : getMethods() )
      {
        if( method.isConstructor() )
        {
          result.add( (GosuMethod)method );
        }
      }

      myConstructors = cached = result.toArray( new GosuMethod[result.size()] );
    }
    return cached;
  }

  @NotNull
  public PsiClass[] getInnerClasses()
  {
    PsiClass[] inners = myInnerClasses;
    if( inners == null )
    {
      myInnerClasses = inners = findChildrenByClass( PsiClass.class );
    }

    return inners;
  }

  @NotNull
  public PsiClassInitializer[] getInitializers()
  {
    return PsiClassInitializer.EMPTY_ARRAY;
  }

  @NotNull
  public PsiField[] getAllFields()
  {
    return GosuClassImplUtil.getAllFields( this );
  }

  @NotNull
  public PsiMethod[] getAllMethods()
  {
    return GosuClassImplUtil.getAllMethods( this );
  }

  @NotNull
  public PsiClass[] getAllInnerClasses()
  {
    return PsiClassImplUtil.getAllInnerClasses( this );
  }

  @Nullable
  public PsiField findFieldByName( String name, boolean checkBases )
  {
    return GosuClassImplUtil.findFieldByName( this, name, checkBases );
  }

  @Nullable
  public PsiMethod findMethodBySignature( PsiMethod patternMethod, boolean checkBases )
  {
    return GosuClassImplUtil.findMethodBySignature( this, patternMethod, checkBases );
  }

  @NotNull
  public PsiMethod[] findMethodsBySignature( PsiMethod patternMethod, boolean checkBases )
  {
    return GosuClassImplUtil.findMethodsBySignature( this, patternMethod, checkBases );
  }

  @NotNull
  public PsiMethod[] findCodeMethodsBySignature( PsiMethod patternMethod, boolean checkBases )
  {
    return GosuClassImplUtil.findCodeMethodsBySignature( this, patternMethod, checkBases );
  }

  @NotNull
  public PsiMethod[] findMethodsByName( @NonNls String name, boolean checkBases )
  {
    return GosuClassImplUtil.findMethodsByName( this, name, checkBases );
  }

  @NotNull
  public PsiMethod[] findCodeMethodsByName( @NonNls String name, boolean checkBases )
  {
    return GosuClassImplUtil.findCodeMethodsByName( this, name, checkBases );
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName( String name, boolean checkBases )
  {
    return GosuClassImplUtil.findMethodsAndTheirSubstitutorsByName( this, name, checkBases );
  }

  @NotNull
  public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors()
  {
    return GosuClassImplUtil.getAllMethodsAndTheirSubstitutors( this );
  }

  @Nullable
  public PsiClass findInnerClassByName( String name, boolean checkBases )
  {
    return null;
  }

  public boolean isAnonymous()
  {
    return false;
  }

  @Nullable
  public PsiIdentifier getNameIdentifier()
  {
    return (PsiIdentifier)findElement( this, GosuElementTypes.ELEM_TYPE_ClassDeclaration );
  }

  @Nullable
  public PsiElement getScope()
  {
    return null;
  }

  public boolean isInheritor( @NotNull PsiClass baseClass, boolean checkDeep )
  {
    return InheritanceImplUtil.isInheritor( this, baseClass, checkDeep );
  }

  public boolean isInheritorDeep( PsiClass baseClass, @Nullable PsiClass classToByPass )
  {
    return InheritanceImplUtil.isInheritorDeep( this, baseClass, classToByPass );
  }

  @Nullable
  public PsiClass getContainingClass()
  {
    return getParent() instanceof PsiClass ? (PsiClass)getParent() : null;
  }

  @NotNull
  public Collection<HierarchicalMethodSignature> getVisibleSignatures()
  {
    return PsiSuperMethodImplUtil.getVisibleSignatures( this );
  }

  public PsiElement setName( @NonNls @NotNull String newName ) throws IncorrectOperationException
  {
    String oldName = getName();
    boolean isRenameFile = isRenameFileOnClassRenaming();

    PsiIdentifier nameIdentifier = (PsiIdentifier) getNameIdentifier().getFirstChild();
    com.intellij.psi.impl.PsiImplUtil.setName(nameIdentifier, newName);

    if (isRenameFile) {
      PsiFile file = (PsiFile)getParent();
      String fileName = file.getName();
      int dotIndex = fileName.lastIndexOf('.');
      file.setName(dotIndex >= 0 ? newName + "." + fileName.substring(dotIndex + 1) : newName);
    }

    // rename constructors
    for (PsiMethod method : getConstructors()) {
      if (method.getName().equals(oldName)) {
        method.setName(newName);
      }
    }

    return this;
  }

  @Nullable
  public IGosuModifierList getModifierList()
  {
    return this;
  }

  @Nullable
  public PsiDocComment getDocComment()
  {
    //## todo:
    return null;
    //return GosuDocCommentUtil.findDocComment( this );
  }

  public boolean isDeprecated()
  {
    //## todo:
    return false;
    //return PsiImplUtil.isDeprecatedByDocTag( this ) || PsiImplUtil.isDeprecatedByAnnotation( this );
  }

  public boolean hasTypeParameters()
  {
    return getTypeParameters().length > 0;
  }

  @Nullable
  public GosuTypeParameterList getTypeParameterList()
  {
    return (GosuTypeParameterList)findChildByType( GosuElementTypes.TYPE_PARAMETER_LIST );
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

  @Nullable
  public Icon getIcon( int flags )
  {
    Icon icon = getIconInner();
    final boolean isLocked = (flags & ICON_FLAG_READ_STATUS) != 0 && !isWritable();
    RowIcon rowIcon = ElementBase.createLayeredIcon( icon, ElementPresentationUtil.getFlags( this, isLocked ) );
    if( (flags & ICON_FLAG_VISIBILITY) != 0 )
    {
      VisibilityIcons.setVisibilityIcon( getModifierList(), rowIcon );
    }
    return rowIcon;
  }

  private Icon getIconInner()
  {
    return GosuIcons.FILE_CLASS;
  }

  private boolean isRenameFileOnClassRenaming()
  {
    final PsiFile file = getContainingFile();
    if( !(file instanceof GosuFile) )
    {
      return false;
    }
    final GosuFile gosuFile = (GosuFile)file;
    final String name = getName();
    final VirtualFile vFile = gosuFile.getVirtualFile();
    return vFile != null && name != null && name.equals( vFile.getNameWithoutExtension() );
  }

  @Nullable
  public PsiElement getOriginalElement()
  {
    return PsiImplUtil.getOriginalElement( this, getContainingFile() );
  }

  public PsiElement addAfter( @NotNull PsiElement element, PsiElement anchor ) throws IncorrectOperationException
  {
    if( anchor == null )
    {
      return add( element );
    }
    if( anchor.getParent() == this )
    {

      final PsiElement nextChild = anchor.getNextSibling();
      if( nextChild == null )
      {
        add( element );
        return element;
      }

      ASTNode node = element.getNode();
      assert node != null;
      //body.getNode().addLeaf(GosuElementTypes.mNLS, "\n", nextChild.getNode());
      return addBefore( element, nextChild );
    }
    else
    {
      return super.addAfter( element, anchor );
    }
  }

  public PsiElement addBefore( @NotNull PsiElement element, PsiElement anchor ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Work in progress" );
//    if( anchor == null )
//    {
//      add( element );
//      return element;
//    }
//
//    ASTNode node = element.getNode();
//    assert node != null;
//    final ASTNode bodyNode = getNode();
//    final ASTNode anchorNode = anchor.getNode();
//    bodyNode.addChild( node, anchorNode );
//    bodyNode.addLeaf( GosuTokenTypes.mWS, " ", node );
//    bodyNode.addLeaf( GosuTokenTypes.mNLS, "\n", anchorNode );
//    return element;
  }

  public PsiElement add( @NotNull PsiElement psiElement ) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException( "Work in progress" );
//
//    final PsiElement lBrace = getLBrace();
//
//    if( lBrace == null )
//    {
//      throw new IncorrectOperationException( "No left brace" );
//    }
//
//    PsiMember member = getAnyMember( psiElement );
//    PsiElement anchor = member != null ? getDefaultAnchor( member ) : null;
//    if( anchor == null )
//    {
//      anchor = lBrace.getNextSibling();
//    }
//
//    if( anchor != null )
//    {
//      ASTNode node = anchor.getNode();
//      assert node != null;
//      if( GosuElementTypes.mSEMI.equals( node.getElementType() ) )
//      {
//        anchor = anchor.getNextSibling();
//      }
//      psiElement = addBefore( psiElement, anchor );
//    }
//    else
//    {
//      add( psiElement );
//    }
//
//    return psiElement;
  }

  @Nullable
  private static PsiMember getAnyMember( @Nullable PsiElement psiElement )
  {
    if( psiElement instanceof PsiMember )
    {
      return (PsiMember)psiElement;
    }
    if( psiElement instanceof IGosuVariable)
    {
      return (PsiMember)psiElement;
    }
    return null;
  }

  public <T extends GosuMembersDeclaration> T addMemberDeclaration( T decl, PsiElement anchorBefore )
    throws IncorrectOperationException
  {

    if( anchorBefore == null )
    {
      return (T)add( decl );
    }

    decl = (T)addBefore( decl, anchorBefore );
//    node.addLeaf(GosuTokenTypes.mWS, " ", decl.getNode()); //add whitespaces before and after to hack over incorrect auto reformat
//    node.addLeaf(GosuTokenTypes.mWS, " ", anchorNode);
    return decl;
  }

  public IGosuField[] getFields()
  {
    if( myFields == null )
    {
      IGosuVariable[] declarations = findChildrenByClass( IGosuVariable.class );
      if( declarations.length == 0 )
      {
        return IGosuField.EMPTY_ARRAY;
      }
      List<IGosuField> result = new ArrayList<IGosuField>();
      for( IGosuVariable variable : declarations )
      {
        if( variable instanceof IGosuField )
        {
          result.add( (IGosuField)variable );
        }
      }
      myFields = result.toArray( new IGosuField[result.size()] );
    }

    return myFields;
  }

  @Nullable
  public PsiJavaToken getLBrace()
  {
    return null; //(PsiJavaToken)findChildByType( GosuTokenTypes.TT_OP_brace_left );
  }

  @Nullable
  public PsiJavaToken getRBrace()
  {
    return null; //(PsiJavaToken)findChildByType( GosuTokenTypes.TT_OP_brace_right );
  }

  public void removeVariable( IGosuVariable variable )
  {
    throw new UnsupportedOperationException( "Men at work" );
    //## todo:
    // PsiImplUtil.removeVariable( variable );
  }

  public IGosuVariable addVariableBefore( IGosuVariable variable, IGosuStatement anchor ) throws IncorrectOperationException
  {
    PsiElement rBrace = getRBrace();
    if( anchor == null && rBrace == null )
    {
      throw new IncorrectOperationException();
    }

    if( anchor != null && !this.equals( anchor.getParent() ) )
    {
      throw new IncorrectOperationException();
    }

    ASTNode elemNode = variable.getNode();
    final ASTNode anchorNode = anchor != null ? anchor.getNode() : rBrace.getNode();
    getNode().addChild( elemNode, anchorNode );
    return (IGosuVariable)elemNode.getPsi();
  }

  public String toString(){
    return "PsiClass:" + getName();
  }
}
