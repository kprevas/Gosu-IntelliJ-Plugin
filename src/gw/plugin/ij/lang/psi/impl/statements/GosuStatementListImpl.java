package gw.plugin.ij.lang.psi.impl.statements;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.Modifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.ElementClassHint;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtil;
import gnu.trove.THashSet;
import gw.lang.parser.statements.IStatementList;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatement;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatementList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */                                                                                                             // added to make codeInspections work
public class GosuStatementListImpl extends GosuBaseStatementImpl<IStatementList> implements IGosuStatementList, PsiModifierListOwner
{
  public GosuStatementListImpl( GosuCompositeElement<IStatementList> node )
  {
    super( node );
  }

  @NotNull
  @Override
  public PsiStatement[] getStatements()
  {
    return findChildrenByClass( IGosuStatement.class );
  }

  @Override
  public PsiElement getFirstBodyElement()
  {
    PsiStatement[] stmts = getStatements();
    if( stmts.length > 0 )
    {
      return stmts[0];
    }
    return null;
  }

  @Override
  public PsiElement getLastBodyElement()
  {
    PsiStatement[] stmts = getStatements();
    if( stmts.length > 0 )
    {
      return stmts[stmts.length - 1];
    }
    return null;
  }

  @Override
  public PsiJavaToken getLBrace()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public PsiJavaToken getRBrace()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean processDeclarations( @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place )
  {
    processor.handleEvent( PsiScopeProcessor.Event.SET_DECLARATION_HOLDER, this );
    if( lastParent == null )
    {
      // Parent element should not see our vars
      return true;
    }
    Pair<Set<String>, Set<String>> pair = buildMaps();
    boolean conflict = pair == null;
    final Set<String> classesSet = conflict ? null : pair.getFirst();
    final Set<String> variablesSet = conflict ? null : pair.getSecond();
    final NameHint hint = processor.getHint( NameHint.KEY );
    if( hint != null && !conflict )
    {
      final ElementClassHint elementClassHint = processor.getHint( ElementClassHint.KEY );
      final String name = hint.getName( state );
      if( (elementClassHint == null || elementClassHint.shouldProcess( ElementClassHint.DeclaractionKind.CLASS )) && classesSet.contains( name ) )
      {
        return PsiScopesUtil.walkChildrenScopes( this, processor, state, lastParent, place );
      }
      if( (elementClassHint == null || elementClassHint.shouldProcess( ElementClassHint.DeclaractionKind.VARIABLE )) && variablesSet.contains( name ) )
      {
        return PsiScopesUtil.walkChildrenScopes( this, processor, state, lastParent, place );
      }
    }
    else
    {
      return PsiScopesUtil.walkChildrenScopes( this, processor, state, lastParent, place );
    }
    return true;
  }

  // return Pair(classesset, localsSet) or null if there was conflict
  private Pair<Set<String>, Set<String>> buildMaps()
  {
    final Set<String> localsSet = new THashSet<String>();
    final Set<String> classesSet = new THashSet<String>();
    final Ref<Boolean> conflict = new Ref<Boolean>( Boolean.FALSE );
    PsiScopesUtil.walkChildrenScopes( this,
      new BaseScopeProcessor()
      {
        public boolean execute( PsiElement element, ResolveState state )
        {
          if( element instanceof PsiLocalVariable )
          {
            final PsiLocalVariable variable = (PsiLocalVariable)element;
            final String name = variable.getName();
            if( !localsSet.add( name ) )
            {
              conflict.set( Boolean.TRUE );
              localsSet.clear();
              classesSet.clear();
            }
          }
          else if( element instanceof PsiClass )
          {
            final PsiClass psiClass = (PsiClass)element;
            final String name = psiClass.getName();
            if( !classesSet.add( name ) )
            {
              conflict.set( Boolean.TRUE );
              localsSet.clear();
              classesSet.clear();
            }
          }
          return !conflict.get();
        }
      }, ResolveState.initial(), this, this );

    Set<String> set1 = (classesSet.isEmpty() ? Collections.<String>emptySet() : classesSet);
    Set<String> set2 = (localsSet.isEmpty() ? Collections.<String>emptySet() : localsSet);
    return conflict.get() ? null : Pair.create( set1, set2 );
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
