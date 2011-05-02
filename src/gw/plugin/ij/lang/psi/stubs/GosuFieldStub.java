package gw.plugin.ij.lang.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuFieldStub extends NamedStub<IGosuField>
{
  String[] getAnnotations();

  boolean isEnumConstant();

  String[] getNamedParameters();

  boolean isDeprecatedByDocTag();

  byte getFlags();
}
