package gw.plugin.ij.lang.psi.stubs.index;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuAnnotatedMemberIndex extends StringStubIndexExtension<PsiElement>
{
  public static final StubIndexKey<String, PsiElement> KEY = StubIndexKey.createIndexKey( "gosu.annot.members" );

  public StubIndexKey<String, PsiElement> getKey()
  {
    return KEY;
  }
}