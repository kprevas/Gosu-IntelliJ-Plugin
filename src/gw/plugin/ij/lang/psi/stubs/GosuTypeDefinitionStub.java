package gw.plugin.ij.lang.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuTypeDefinition;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuTypeDefinitionStub extends NamedStub<GosuTypeDefinition>
{
  String[] getSuperClassNames();

  String getQualifiedName();

  String getSourceFileName();

  String[] getAnnotations();

  boolean isAnonymous();

  boolean isAnonymousInQualifiedNew();

  boolean isInterface();

  boolean isEnum();

  boolean isAnnotationType();

  byte getFlags();
}
