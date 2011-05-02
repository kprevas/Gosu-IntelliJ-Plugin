package gw.plugin.ij.lang.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.Modifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import gw.plugin.ij.GosuClassFileType;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public abstract class GosuFileBaseImpl extends PsiFileBase implements GosuFileBase
{
  protected GosuFileBaseImpl( FileViewProvider viewProvider, @NotNull Language language )
  {
    super( viewProvider, language );
  }

  public GosuFileBaseImpl( IFileElementType root, IFileElementType root1, FileViewProvider provider )
  {
    this( provider, root.getLanguage() );
    init( root, root1 );
  }

  @NotNull
  public FileType getFileType()
  {
    return GosuClassFileType.instance();
  }

  public GosuTypeDefinition[] getTypeDefinitions()
  {
    final StubElement<?> stub = getStub();
    if( stub != null )
    {
      return stub.getChildrenByType( GosuElementTypes.TYPE_DEFINITION_TYPES, GosuTypeDefinition.ARRAY_FACTORY );
    }

    return calcTreeElement().getChildrenAsPsiElements( GosuElementTypes.TYPE_DEFINITION_TYPES, GosuTypeDefinition.ARRAY_FACTORY );
  }

  public GosuTypeDefinition getTopLevelDefinition()
  {
    GosuTypeDefinition[] children = findChildrenByClass( GosuTypeDefinition.class );
    if( children != null && children.length > 0 )
    {
      return children[0];
    }
    return null;
  }

  private static boolean hasElementType( PsiElement next, final IElementType type )
  {
    if( next == null )
    {
      return false;
    }
    final ASTNode astNode = next.getNode();
    if( astNode != null && astNode.getElementType() == type )
    {
      return true;
    }
    return false;
  }

  @NotNull
  public PsiClass[] getClasses()
  {
    try {
      return getTypeDefinitions();
    } catch(Throwable t) {
      System.err.println("ERROR: " + this.getClass().getSimpleName() + ": Trapped " + t.getLocalizedMessage());
      return new PsiClass[0];
    }
  }


  @Override
  public PsiModifierList getModifierList()
  {
    return null;
  }

  @Override
  public boolean hasModifierProperty( @Modifier @NonNls @NotNull String name )
  {
    return false;
  }
}
