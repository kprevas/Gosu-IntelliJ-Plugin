package gw.plugin.ij.lang.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.GosuReferenceList;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public interface GosuReferenceListStub extends StubElement<GosuReferenceList> {

  String[] getBaseClasses();
}
