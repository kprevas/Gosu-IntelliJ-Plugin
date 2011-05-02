package gw.plugin.ij.lang.psi.api.statements;

import com.intellij.psi.PsiCodeBlock;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMember;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuClassInitializer extends IGosuMember
{
  public static final GosuClassInitializer[] EMPTY_ARRAY = new GosuClassInitializer[0];

  PsiCodeBlock getBlock();

  boolean isStatic();
}
