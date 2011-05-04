package gw.plugin.ij.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCompletionContributor extends CompletionContributor {
  public GosuCompletionContributor() {

    // Skeleton symbol completion
    extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                    ProcessingContext processingContext,
                                    @NotNull CompletionResultSet completionResultSet) {
        if( enclosingStatementIsAClass( completionParameters ) )
        {
          LookupElementBuilder completion = LookupElementBuilder.create("function");
          completion.setPrefixMatcher(new PlainPrefixMatcher(getCurrentPrefix(completionParameters)));
          completionResultSet.addElement(completion);
        }
      }

    });
  }

  private String getCurrentPrefix( CompletionParameters completionParameters )
  {
    if( completionParameters.getOriginalPosition() instanceof PsiWhiteSpace )
    {
      return "";
    }
    else
    {
      return completionParameters.getOriginalPosition().getText();
    }
  }

  private boolean enclosingStatementIsAClass( CompletionParameters completionParameters )
  {
    if( completionParameters.getPosition().getParent().getParent() instanceof GosuClassDefinition )
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
