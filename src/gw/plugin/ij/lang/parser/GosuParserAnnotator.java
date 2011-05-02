package gw.plugin.ij.lang.parser;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import gw.lang.parser.IParseIssue;
import gw.lang.parser.IParsedElement;
import gw.lang.parser.exceptions.ParseResultsException;
import gw.lang.parser.expressions.IVarStatement;
import gw.lang.parser.statements.IClassStatement;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.IGosuClass;
import gw.plugin.ij.lang.psi.GosuFile;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuPsiElementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuVariableBaseImpl;
import gw.plugin.ij.lang.psi.stubs.elements.GosuStubFileElementType;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuParserAnnotator implements Annotator, ExternalAnnotator
{
  private static Key<Set<IParseIssue>> ISSUES_KEY = new Key<Set<IParseIssue>>( "_gosu_issues_complete_" );

//  public void annotate( PsiElement element, AnnotationHolder holder )
//  {
//    TypeSystem.lock();
//    try
//    {
//      while( !(element instanceof GosuClassFileImpl) )
//      {
//        element = element.getParent();
//      }
//
//      handleIssues( element, holder );
//    }
//    finally
//    {
//      TypeSystem.unlock();
//    }
//  }
//
//  private void handleIssues( PsiElement element, AnnotationHolder holder )
//  {
//    if( element.getNode() instanceof GosuCompositeElement )
//    {
//      IParsedElement pe = ((GosuCompositeElement)element.getNode()).getParsedElement();
//      for( IParseIssue issue : pe.getParseExceptions() )
//      {
//        Set<IParseIssue> issuesDone = getIssuesDone( holder );
//        if( !issuesDone.contains( issue ) )
//        {
//          holder.createErrorAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
//          issuesDone.add( issue );
//        }
//      }
//
//      for( IParseIssue issue : pe.getParseWarnings() )
//      {
//        Set<IParseIssue> issuesDone = getIssuesDone( holder );
//        if( !issuesDone.contains( issue ) )
//        {
//          holder.createWarningAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
//          issuesDone.add( issue );
//        }
//      }
//    }
//    for( PsiElement child : element.getChildren() )
//    {
//      handleIssues( child, holder );
//    }
//  }

  public void annotate( PsiFile element, AnnotationHolder holder )
  {
    annotate( (PsiElement)element, holder );
  }
  public void annotate( PsiElement element, AnnotationHolder holder )
  {
    TypeSystem.lock();
    try
    {
      if( element instanceof LeafPsiElement )
      {
        element = element.getParent();
      }
      GosuClassFileImpl psiFile = (GosuClassFileImpl)element;
      if( element instanceof GosuClassFileImpl )
      {
        element = element.getFirstChild();
        while( !(element instanceof IGosuPsiElement) )
        {
          element = element.getNextSibling();
        }
      }
      if( element instanceof IGosuPsiElement && element.getNode() instanceof GosuCompositeElement )
      {
        ParseResultsException parseResultsException = psiFile.getUserData( GosuStubFileElementType.KEY_PARSE_RESULTS );//pe.getGosuClass().getParseResultsException();
        if( parseResultsException != null )
        {
  //        Document doc = element.getContainingFile().getViewProvider().getDocument();
  //        UpdateHighlightersUtil.setHighlightersToEditor( element.getProject(), doc, 0, doc.getTextLength(), Collections.<HighlightInfo>emptyList(), Pass.WOLF );
          for( IParseIssue issue : parseResultsException.getParseExceptions() )
          {
            Set<IParseIssue> issuesDone = getIssuesDone( holder );
            if( !issuesDone.contains( issue ) )
            {
              holder.createErrorAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
              issuesDone.add( issue );
            }
          }

          for( IParseIssue issue : parseResultsException.getParseWarnings() )
          {
            Set<IParseIssue> issuesDone = getIssuesDone( holder );
            if( !issuesDone.contains( issue ) )
            {
              holder.createWarningAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
              issuesDone.add( issue );
            }
          }
        }
      }
    }
    finally
    {
      TypeSystem.unlock();
    }
  }

  private Set<IParseIssue> getIssuesDone( AnnotationHolder holder )
  {
    if( holder.getCurrentAnnotationSession() == null )
    {
      return new HashSet<IParseIssue>();
    }
    Set<IParseIssue> issuesDone = holder.getCurrentAnnotationSession().getUserData( ISSUES_KEY );
    if( issuesDone == null )
    {
      issuesDone = new HashSet<IParseIssue>();
      holder.getCurrentAnnotationSession().putUserData( ISSUES_KEY, issuesDone );
    }
    return issuesDone;
  }
}
