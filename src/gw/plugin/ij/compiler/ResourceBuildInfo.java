package gw.plugin.ij.compiler;

import com.intellij.openapi.module.Module;
import gw.lang.reflect.*;
import gw.lang.reflect.gs.IGenericTypeVariable;
import gw.lang.reflect.gs.IGosuClass;
import gw.lang.reflect.gs.IGosuEnhancement;
import gw.lang.reflect.module.IModule;
import gw.util.fingerprint.FP64;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class ResourceBuildInfo {
  private String _resourceName;
  private IType _type;
  private FP64 _fingerprint;
  private LinkedHashSet<String> _namespaces;
  private LinkedHashSet<String> _fullyQualifiedTypes;
  private LinkedHashSet<String> _unresolvedTypes;

  public ResourceBuildInfo(String resourceName, Module ijModule) {
    _resourceName = resourceName;
    IModule module = TypeSystem.getExecutionEnvironment().getModule(ijModule.getName());
    TypeSystem.getExecutionEnvironment().pushModule(module);
    try {
      _type = TypeSystem.getByFullNameIfValid(getTypeName(_resourceName));
    } finally {
      TypeSystem.getExecutionEnvironment().popModule(module);
    }
    _namespaces = new LinkedHashSet<String>();
    _fullyQualifiedTypes = new LinkedHashSet<String>();
    _unresolvedTypes = new LinkedHashSet<String>();
  }

  public void addRelationships(Set<String> namespaces, Set<String> relatedResourceNames, Set<String> unresolvedTypes) {
    _namespaces.addAll(namespaces);
    _fullyQualifiedTypes.addAll(relatedResourceNames);
    _unresolvedTypes.addAll(unresolvedTypes);
  }

  public void addRelatedResource(String relatedResourceName) {
    _fullyQualifiedTypes.add(relatedResourceName);
  }

  public void setRelationships(Set<String> namespaces, Set<String> relatedResourceNames, Set<String> unresolvedTypes) {
    _namespaces.clear();
    _fullyQualifiedTypes.clear();
    _unresolvedTypes.clear();
    addRelationships(namespaces, relatedResourceNames, unresolvedTypes);
  }

  public boolean references(String resourceName) {
    if (_fullyQualifiedTypes.contains(resourceName)) {
      return true;
    }

    String typeName = getTypeName(resourceName);

    for (String unresolvedType : _unresolvedTypes) {
      // Ex: "unresolvedType" is a reference to an inner class in a non-existent class
      if (unresolvedType.startsWith(typeName)) {
        return true;
      }
      for (String namespace : _namespaces) {
        String possibleType = namespace + "." + unresolvedType;
        // Ex: unresolved type found in one of the packages we use
        if (typeName.equals(possibleType)) {
          return true;
        }
      }
    }

    for (String namespace : _namespaces) {
      // Ex: we are creating or deleting a package which was used by this class
      if (typeName.equals(namespace)) {
        return true;
      }
    }

    return false;
  }

  private String getTypeName(String resourceName) {
    String name;
    if (resourceName.contains(".")) {
      name = resourceName.substring(resourceName.indexOf('/') + 1, resourceName.lastIndexOf('.'));
    } else {
      name = resourceName.substring(resourceName.indexOf('/') + 1, resourceName.length());
    }
    name = name.replace('/', '.');
    return name;
  }

  public void write(DataOutputStream out) throws IOException {
    out.writeUTF(_resourceName);
    out.writeLong(_fingerprint != null ? _fingerprint.getRawFingerprint() : 0);

    // write namespaces
    out.writeInt(_namespaces.size());
    for (String namespace : _namespaces) {
      out.writeUTF(namespace);
    }

    // write fully qualified names
    out.writeInt(_fullyQualifiedTypes.size());
    for (String relatedResourceName : _fullyQualifiedTypes) {
      out.writeUTF(relatedResourceName);
    }

    // write unresolved types
    out.writeInt(_unresolvedTypes.size());
    for (String unresolvedType : _unresolvedTypes) {
      out.writeUTF(unresolvedType);
    }
  }

  public static ResourceBuildInfo read(DataInputStream in, Module project) throws IOException {
    String resourceName = in.readUTF();
    long rawFingerprint = in.readLong();

    // read namespaces
    int namespacesSetSize = in.readInt();
    Set<String> namespacesSet = new LinkedHashSet<String>(namespacesSetSize);
    for (int j = 0; j < namespacesSetSize; j++) {
      String namespace = in.readUTF();
      namespacesSet.add(namespace);
    }

    // read fully qualified types
    int fullyQualifiedTypesSetSize = in.readInt();
    Set<String> fullyQualifiedTypesSet = new LinkedHashSet<String>(fullyQualifiedTypesSetSize);
    for (int j = 0; j < fullyQualifiedTypesSetSize; j++) {
      String fullyQualifiedType = in.readUTF();
      fullyQualifiedTypesSet.add(fullyQualifiedType);
    }

    // read unresolved types
    int unresolvedTypesSetSize = in.readInt();
    Set<String> unresolvedTypesSet = new LinkedHashSet<String>(unresolvedTypesSetSize);
    for (int j = 0; j < unresolvedTypesSetSize; j++) {
      String unresolvedType = in.readUTF();
      fullyQualifiedTypesSet.add(unresolvedType);
    }

    ResourceBuildInfo buildInfo = new ResourceBuildInfo(resourceName, project);
    buildInfo.setRelationships(namespacesSet, fullyQualifiedTypesSet, unresolvedTypesSet);
    buildInfo._fingerprint = new FP64();
    buildInfo._fingerprint.extend(rawFingerprint);
    return buildInfo;
  }

  public void updateFingerprint() {
    if (_type != null) {
      _fingerprint = calculateFingerprint(_type);
    }
  }

  public static FP64 calculateFingerprint(IType type) {
    // the package
    FP64 fingerprint = new FP64(type.getNamespace());

    // the type name
    fingerprint.extend(type.getName());

    // the class modifiers
    int modifiers = type.getModifiers();
    modifiers = Modifier.setPublic(modifiers, false);
    fingerprint.extend(modifiers);

    // the superclass declaration, if any
    if (type.getSupertype() != null) {
      fingerprint.extend(type.getSupertype().getName());
    }

    // the list of interfaces
    List<? extends IType> interfaces = new ArrayList<IType>(type.getInterfaces());
    Collections.sort(interfaces, new Comparator<IType>() {
      public int compare(IType type1, IType type2) {
        return type1.getName().compareTo(type2.getName());
      }
    });
    for (IType interfaceType : interfaces) {
      fingerprint.extend(interfaceType.getName());
    }

    // generic type variables
    IGenericTypeVariable[] genericTypeVariables = type.getGenericTypeVariables();
    for (IGenericTypeVariable genericTypeVariable : genericTypeVariables) {
      // the bounding type of the generic type variable
      fingerprint.extend(genericTypeVariable.getBoundingType().getName());
    }

    // the enhanced type for enhancements
    if (type instanceof IGosuEnhancement) {
      IType enhancedType = ((IGosuEnhancement)type).getEnhancedType();
      if (enhancedType != null) {
        fingerprint.extend(enhancedType.getName());
      }
    }

    // non-private methods
    ITypeInfo typeInfo = type.getTypeInfo();
    List<? extends IMethodInfo> methods = new ArrayList<IMethodInfo>(
        typeInfo instanceof IRelativeTypeInfo ? ((IRelativeTypeInfo)typeInfo).getMethods(type) : typeInfo.getMethods());
    Collections.sort(methods, methodComparator);
    for (IMethodInfo method : methods) {
      if (!method.isPrivate()) {
        // the method name
        fingerprint.extend(method.getName());
        // the return type
        fingerprint.extend(method.getReturnType().getName());

        IParameterInfo[] parameters = method.getParameters();
        for (IParameterInfo parameter : parameters) {
          // method parameter type
          fingerprint.extend(parameter.getFeatureType().getName());
        }

        if (method instanceof IGenericMethodInfo) {
          IGenericTypeVariable[] typeVariables = ((IGenericMethodInfo)method).getTypeVariables();
          for (IGenericTypeVariable typeVariable : typeVariables) {
            // the bounding type of the generic type variable
            fingerprint.extend(typeVariable.getBoundingType().getName());
          }
        }

        // modifiers
        fingerprint.extend(Boolean.toString(method.isPublic()));
        fingerprint.extend(Boolean.toString(method.isPrivate()));
        fingerprint.extend(Boolean.toString(method.isProtected()));
        fingerprint.extend(Boolean.toString(method.isInternal()));
        fingerprint.extend(Boolean.toString(method.isFinal()));
        fingerprint.extend(Boolean.toString(method.isStatic()));
        fingerprint.extend(Boolean.toString(method.isHidden()));
      }
    }

    // non-private constructors
    List<? extends IConstructorInfo> constructors = new ArrayList<IConstructorInfo>(
        typeInfo instanceof IRelativeTypeInfo ? ((IRelativeTypeInfo)typeInfo).getConstructors(type) : typeInfo.getConstructors());
    Collections.sort(constructors, constructorComparator);
    for (IConstructorInfo constructor : constructors) {
      if (!constructor.isPrivate()) {
        IParameterInfo[] parameters = constructor.getParameters();
        for (IParameterInfo parameter : parameters) {
          // constructor parameter type
          fingerprint.extend(parameter.getFeatureType().getName());
        }

        // modifiers
        fingerprint.extend(Boolean.toString(constructor.isPublic()));
        fingerprint.extend(Boolean.toString(constructor.isPrivate()));
        fingerprint.extend(Boolean.toString(constructor.isProtected()));
        fingerprint.extend(Boolean.toString(constructor.isInternal()));
        fingerprint.extend(Boolean.toString(constructor.isFinal()));
        fingerprint.extend(Boolean.toString(constructor.isStatic()));
//        fingerprint.extend(Boolean.toString(constructor.isHidden()));
      }
    }

    // non-private properties
    List<? extends IPropertyInfo> properties = new ArrayList<IPropertyInfo>(
        typeInfo instanceof IRelativeTypeInfo ? ((IRelativeTypeInfo)typeInfo).getProperties(type) : typeInfo.getProperties());
    Collections.sort(properties, propertyComparator);
    for (IPropertyInfo property : properties) {
      if (!property.isPrivate()) {
        // the property name
        fingerprint.extend(property.getName());
        // the property type
        fingerprint.extend(property.getFeatureType().getName());
        // readability
        fingerprint.extend(Boolean.toString(property.isReadable()));
        fingerprint.extend(Boolean.toString(property.isWritable()));
        // modifiers
        fingerprint.extend(Boolean.toString(property.isPublic()));
        fingerprint.extend(Boolean.toString(property.isPrivate()));
        fingerprint.extend(Boolean.toString(property.isProtected()));
        fingerprint.extend(Boolean.toString(property.isInternal()));
        fingerprint.extend(Boolean.toString(property.isFinal()));
        fingerprint.extend(Boolean.toString(property.isStatic()));
        fingerprint.extend(Boolean.toString(property.isHidden()));
      }
    }

    // non-private inner classes
    if (type instanceof IGosuClass) {
      ArrayList<IGosuClass> innerClasses = new ArrayList<IGosuClass>(((IGosuClass)type).getInnerClasses().values());
      Collections.sort(innerClasses, new Comparator<IGosuClass>() {
        public int compare(IGosuClass class1, IGosuClass class2) {
          return class1.getName().compareTo(class2.getName());
        }
      });
      for (IGosuClass innerClass : innerClasses) {
        if (!innerClass.isAnonymous() && !Modifier.isPrivate(innerClass.getModifiers())) {
          fingerprint.extend(calculateFingerprint(innerClass).getRawFingerprint());
        }
      }
    }

    return fingerprint;
  }

  static Comparator<IMethodInfo> methodComparator = new Comparator<IMethodInfo>() {
    public int compare(IMethodInfo method1, IMethodInfo method2) {
      return getSignature(method1).compareTo(getSignature(method2));
    }

    private String getSignature(IMethodInfo method) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(method.getName());
      buffer.append("(");
      IParameterInfo[] parameters = method.getParameters();
      for (IParameterInfo parameter : parameters) {
        buffer.append(parameter.getName());
        buffer.append(":");
        buffer.append(parameter.getFeatureType().getName());
        buffer.append(",");
      }
      buffer.append("):");
      buffer.append(method.getReturnType().getName());
      return buffer.toString();
    }
  };

  static Comparator<IConstructorInfo> constructorComparator = new Comparator<IConstructorInfo>() {
    public int compare(IConstructorInfo constructor1, IConstructorInfo constructor2) {
      return getSignature(constructor1).compareTo(getSignature(constructor2));
    }

    private String getSignature(IConstructorInfo constructor) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(constructor.getName());
      buffer.append("(");
      IParameterInfo[] parameters = constructor.getParameters();
      for (IParameterInfo parameter : parameters) {
        buffer.append(parameter.getName());
        buffer.append(":");
        buffer.append(parameter.getFeatureType().getName());
        buffer.append(",");
      }
      buffer.append(")");
      return buffer.toString();
    }
  };

  static Comparator<IPropertyInfo> propertyComparator = new Comparator<IPropertyInfo>() {
    public int compare(IPropertyInfo constructor1, IPropertyInfo constructor2) {
      return getSignature(constructor1).compareTo(getSignature(constructor2));
    }

    private String getSignature(IPropertyInfo property) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(property.getName());
      buffer.append(":");
      buffer.append(property.getFeatureType().getName());
      return buffer.toString();
    }
  };

  public String toString() {
    StringBuffer s = new StringBuffer(_resourceName);
    s.append(" -> [");
    for (Iterator<String> i = _namespaces.iterator(); i.hasNext();) {
      s.append(i.next());
      if (i.hasNext()) {
        s.append(", ");
      }
    }
    s.append("], [");
    for (Iterator<String> i = _fullyQualifiedTypes.iterator(); i.hasNext();) {
      s.append(i.next());
      if (i.hasNext()) {
        s.append(", ");
      }
    }
    s.append("], [");
    for (Iterator<String> i = _unresolvedTypes.iterator(); i.hasNext();) {
      s.append(i.next());
      if (i.hasNext()) {
        s.append(", ");
      }
    }
    return s.append("]").toString();
  }

  public String getResourceName() {
    return _resourceName;
  }

  public long getRawFingerprint() {
    return _fingerprint != null ? _fingerprint.getRawFingerprint() : 0;
  }

  public IType getType() {
    return _type;
  }
}
