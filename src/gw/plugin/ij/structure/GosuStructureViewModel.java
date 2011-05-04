package gw.plugin.ij.structure;

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

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.structureView.impl.java.*;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import gw.plugin.ij.lang.psi.GosuFileBase;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuMethod;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GosuStructureViewModel extends TextEditorBasedStructureViewModel {
  private final GosuFileBase rootElement;

  private static final Class[] SUITABLE_CLASSES =
      new Class[]{GosuFileBase.class, GosuTypeDefinition.class, GosuMethod.class, IGosuVariable.class};

  public GosuStructureViewModel(GosuFileBase rootElement) {
    super(rootElement);
    this.rootElement = rootElement;
  }

  protected PsiFile getPsiFile() {
    return rootElement;
  }

  @NotNull
  public StructureViewTreeElement getRoot() {
    try {
        return new JavaClassTreeElement(rootElement.getPsiClass(), false);
    } catch(ClassCastException ex) {
        ex.printStackTrace();
        return null;
    }
//    return new GosuFileStructureViewElement(rootElement);
  }

  @NotNull
  public Filter[] getFilters() {
    return new Filter[]{new InheritedMembersFilter(),
                        new FieldsFilter(),
                        new PublicElementsFilter()};
  }

  @NotNull
  public Grouper[] getGroupers() {
    return new Grouper[]{new SuperTypesGrouper(), new PropertiesGrouper()};
  }


  @Override
  public boolean shouldEnterElement(Object element) {
    return element instanceof GosuTypeDefinition;
  }

  @NotNull
  public Sorter[] getSorters() {
    return new Sorter[]{KindSorter.INSTANCE, VisibilitySorter.INSTANCE, Sorter.ALPHA_SORTER};
  }

  @NotNull
  protected Class[] getSuitableClasses() {
    return SUITABLE_CLASSES;
  }

  @Nullable
  protected Object findAcceptableElement(PsiElement element) {
    while (element != null && !(element instanceof PsiDirectory)) {
      if (isSuitable(element)) {
        if (element instanceof GosuFileBase) {
          return ((GosuFileBase) element).getPsiClass();
        }
        return element;
      }
      element = element.getParent();
    }
    return null;
  }

  @Override
  protected boolean isSuitable(final PsiElement element) {
    if (super.isSuitable(element)) {
      if (element instanceof GosuMethod) {
        GosuMethod method = (GosuMethod) element;
        PsiElement parent = method.getParent().getParent();
        if (parent instanceof GosuTypeDefinition) {
          return ((GosuTypeDefinition) parent).getQualifiedName() != null;
        }
      } else if (element instanceof IGosuVariable ) {
        IGosuVariable field = (IGosuVariable) element;
        PsiElement parent = field.getParent().getParent().getParent();
        if (parent instanceof GosuTypeDefinition) {
          return ((GosuTypeDefinition) parent).getQualifiedName() != null;
        }
      } else if (element instanceof GosuTypeDefinition) {
        return ((GosuTypeDefinition) element).getQualifiedName() != null;
      } else if (element instanceof GosuFileBase) {
        return true;
      }
    }
    return false;
  }
}
