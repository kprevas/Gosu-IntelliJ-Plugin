package gw.plugin.ij.lang.parser;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import gw.lang.parser.IParseIssue;
import gw.lang.parser.exceptions.ParseResultsException;
import gw.lang.parser.resources.Res;
import gw.lang.reflect.TypeSystem;
import gw.plugin.ij.intentions.GosuAddImportAction;
import gw.plugin.ij.lang.psi.IGosuPsiElement;
import gw.plugin.ij.lang.psi.api.types.GosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.stubs.elements.GosuStubFileElementType;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuParserAnnotator implements Annotator, ExternalAnnotator
{
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
            Annotation annotation = holder.createErrorAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
            maybeRegisterFix( issue, annotation, element );
          }

          for( IParseIssue issue : parseResultsException.getParseWarnings() )
          {
            Annotation annotation = holder.createWarningAnnotation( new TextRange( issue.getTokenStart(), issue.getTokenEnd() ), issue.getUIMessage() );
            maybeRegisterFix( issue, annotation, element );
          }
        }
      }
    }
    finally
    {
      TypeSystem.unlock();
    }
  }

  private void maybeRegisterFix( IParseIssue issue, Annotation annotation, PsiElement element )
  {
    element = element.getContainingFile().findElementAt( issue.getTokenStart() );
    while( element != null && !(element instanceof GosuCodeReferenceElement) )
    {
      element = element.getParent();
    }
    if( issue.getMessageKey() == Res.MSG_INVALID_TYPE && element instanceof GosuCodeReferenceElement )
    {
      annotation.registerFix( new GosuAddImportAction( (GosuCodeReferenceElement)element ) );
    }
  }
}
