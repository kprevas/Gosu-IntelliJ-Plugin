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
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCompletionContributor extends CompletionContributor {
  public GosuCompletionContributor() {

//    // Skeleton symbol completion
//    extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
//      @Override
//      protected void addCompletions(@NotNull CompletionParameters completionParameters,
//                                    ProcessingContext processingContext,
//                                    @NotNull CompletionResultSet completionResultSet) {
//        for (int i = 0; i < 10; i++) {
//          LookupElementBuilder completion = LookupElementBuilder.create("Test" + i);
//          completion.setPrefixMatcher(new PlainPrefixMatcher(""));
//          completionResultSet.addElement(completion);
//        }
//      }
//    });


  }
}
