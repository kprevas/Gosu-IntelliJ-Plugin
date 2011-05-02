package gw.plugin.ij.typesystem;

import com.intellij.openapi.module.Module;
import gw.lang.reflect.module.INativeModule;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class IJNativeModule implements INativeModule {

  private Module nativeModule;

  public IJNativeModule(Module module) {
    nativeModule = module;
  }

  @Override
  public boolean isGosuModule() {
    return true;
  }

  @Override
  public Object getNativeModule() {
    return nativeModule;
  }
}
