/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gw.plugin.ij.lang.psi.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.DirectClassInheritorsSearch;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuAnonymousClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import gw.plugin.ij.lang.psi.stubs.index.GosuAnonymousClassIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuDirectInheritorsIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ven
 */
public class GosuDirectInheritorSearcher implements QueryExecutor<PsiClass, DirectClassInheritorsSearch.SearchParameters> {
  public GosuDirectInheritorSearcher() {
  }

  @NotNull
  private static PsiClass[] getDeriverCandidates(PsiClass clazz, GlobalSearchScope scope) {
    final String name = clazz.getName();
    if (name == null) return new PsiClass[0];
    final ArrayList<PsiClass> inheritors = new ArrayList<PsiClass>();
    Collection<GosuReferenceList> gosuReferenceLists = StubIndex.getInstance().get( GosuDirectInheritorsIndex.KEY, name, clazz.getProject(), scope );
    for (GosuReferenceList list : gosuReferenceLists) {
      final PsiElement parent = list.getParent();
      if (parent instanceof GosuTypeDefinition )
      {
        inheritors.add( (GosuTypeDefinition)parent );
      }
    }
    Collection<GosuAnonymousClassDefinition> classes = StubIndex.getInstance().get( GosuAnonymousClassIndex.KEY, name, clazz.getProject(), scope );
    for (GosuAnonymousClassDefinition aClass : classes) {
      inheritors.add(aClass);
    }
    return inheritors.toArray(new PsiClass[inheritors.size()]);
  }

  public boolean execute(@NotNull DirectClassInheritorsSearch.SearchParameters queryParameters, @NotNull final Processor<PsiClass> consumer) {
    final PsiClass clazz = queryParameters.getClassToProcess();
    final SearchScope scope = queryParameters.getScope();
    if (scope instanceof GlobalSearchScope) {
      final PsiClass[] candidates = ApplicationManager.getApplication().runReadAction(new Computable<PsiClass[]>() {
        public PsiClass[] compute() {
          if (!clazz.isValid()) return PsiClass.EMPTY_ARRAY;
          return getDeriverCandidates(clazz, (GlobalSearchScope)scope);
        }
      });
      for (final PsiClass candidate : candidates) {
        final boolean isInheritor = ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
          public Boolean compute() {
            return candidate.isInheritor(clazz, false);
          }
        });
        if (isInheritor) {
          if (!consumer.process(candidate)) {
            return false;
          }
        }
      }
      return true;
    }
    return true;
  }
}
