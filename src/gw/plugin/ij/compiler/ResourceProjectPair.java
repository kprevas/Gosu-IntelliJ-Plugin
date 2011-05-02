package gw.plugin.ij.compiler;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.module.IModule;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
class ResourceProjectPair {
  VirtualFile file;
  String name;
  Module ijModule;

  public ResourceProjectPair(VirtualFile file, Module module) {
    this.name = getRelativeFileName(file, module);
    this.ijModule = module;
    this.file = file;
  }

  private String getRelativeFileName(VirtualFile file, Module module) {
    String moduleDir = module.getModuleFile().getParent().getPath();
    String relativeName = file.getPath();
    if (relativeName.startsWith(moduleDir)) {
      return relativeName.substring(moduleDir.length() + 1);
    } else {
      throw new RuntimeException("File is not under the module root.");
    }
  }

  public ResourceProjectPair(String name, Module module) {
    this.name = name;
    this.ijModule = module;
  }

  public Module getModule() {
    return ijModule;
  }

  public VirtualFile getResource() {
    return file;
  }

  public gw.lang.reflect.IType getType() {
    IModule gosuModule = TypeSystem.getExecutionEnvironment().getModule(ijModule.getName());
    TypeSystem.getExecutionEnvironment().pushModule(gosuModule);
    try {
      String typeName = name.substring(name.indexOf('/') + 1, name.lastIndexOf('.')).replace('/', '.');
      return TypeSystem.getByFullNameIfValid(typeName);
    } finally {
      TypeSystem.getExecutionEnvironment().popModule(gosuModule);
    }
  }

  public String toString() {
    return ijModule.getName() + "/" + name;
  }

  public boolean equals(Object o) {
    if (o instanceof ResourceProjectPair) {
      ResourceProjectPair pair = (ResourceProjectPair) o;
      return this.name.equals(pair.name) && this.ijModule.getName().equals(pair.ijModule.getName());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return name.hashCode() ^ ijModule.getName().hashCode();
  }
}
