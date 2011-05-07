package gw.plugin.ij.compiler;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Chunk;
import gw.lang.parser.IParseIssue;
import gw.lang.parser.IParsedElement;
import gw.lang.parser.exceptions.ParseException;
import gw.lang.parser.exceptions.ParseResultsException;
import gw.lang.parser.expressions.ITypeLiteralExpression;
import gw.lang.parser.resources.Res;
import gw.lang.reflect.IType;
import gw.lang.reflect.ITypeRef;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.*;
import gw.lang.reflect.module.IModule;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuCompiler implements TranslatingCompiler {
  public static boolean DEBUG = false;
  public static boolean WAIT_FOR_BUILD = false;
  private static List<String> testParsedResources = new ArrayList<String>();
  private static List<ResourceParseTime> _parseStatistics = new ArrayList<ResourceParseTime>(10000);

  private Project project;
  public String STATISTICS_FILE_NAME = "c:\\build-statistics.txt";
  private Map<String, ResourceBuildInfo> relationships;

  static String[] ignoreList = new String[] {
  };

  static Pattern[] ignorePats = new Pattern[] {
    Pattern.compile(".*Errant.*\\.gs.*"),
  };

  public GosuCompiler(Project project) {
    this.project = project;
  }

  @Override
  public boolean isCompilableFile(VirtualFile file, CompileContext context) {
    if (file.getName().endsWith(".gs")) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {
    List<ResourceProjectPair> filesToCompile = new ArrayList<ResourceProjectPair>();

    Set<Module> nodes = moduleChunk.getNodes();
    if (nodes.size() > 1) {
      throw new RuntimeException("Cyclic dependency.");
    }
    Module ijModule = nodes.iterator().next();
    List<OutputItem> outputs = new ArrayList<OutputItem>();

    for (final VirtualFile file : files) {
      System.out.println("Compile " + file);
      parseResource(context, new ResourceProjectPair(file, ijModule), true);
    }

    sink.add(null, outputs, new VirtualFile[0]);
  }

  static class GosuOutputItem implements OutputItem {

    private VirtualFile srcFile;

    public GosuOutputItem(VirtualFile srcFile) {
      this.srcFile = srcFile;
    }

    public VirtualFile getSourceFile() {
      return srcFile;
    }

    public String getOutputPath() {
      return null;
    }
  }

  private boolean isEnabled() {
    return true;
  }

  public static void startCollectingParsedResoures() {
    DEBUG = true;
    WAIT_FOR_BUILD = true;
    testParsedResources.clear();
  }

  public static void stopCollectingParsedResoures() {
    DEBUG = false;
  }

  public static List<String> getCollectedParsedResources() {
    return testParsedResources;
  }

  public static boolean waitForBuild() {
    return WAIT_FOR_BUILD;
  }

  private void clean() {
    if (relationships == null) {
      relationships = new HashMap<String, ResourceBuildInfo>();
    } else {
      relationships.clear();
    }
  }

  private void saveParseStatistics() {
    Collections.sort(_parseStatistics);
    try {
      PrintWriter writer = new PrintWriter(new File(STATISTICS_FILE_NAME));
      for (ResourceParseTime t : _parseStatistics) {
        t.print(writer);
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    _parseStatistics.clear();
  }
/*
  private void incrementalBuild(final IProgressMonitor monitor)  {
    IProject project = getProject();
    if (DEBUG) {
      System.out.println("===== incremental build - " + project + " - " + Thread.currentThread().getName());
    }

    try {
      maybeLoadRelationships(project);
    } catch (IOException e) {
      // cannot load the relationships table, a full build will recreate it
      GosuEditorPlugin.log("Cannot load the relationships table, a full build will recreate it.", e);
      fullBuild(monitor);
      return;
    }

    IResourceDelta delta = getDelta(project);
    final LinkedHashSet<ResourceProjectPair> adedOrChangedSourceFiles = new LinkedHashSet<ResourceProjectPair>();
    final LinkedHashSet<ResourceProjectPair> removedSourceFiles = new LinkedHashSet<ResourceProjectPair>();
    delta.accept(new IResourceDeltaVisitor() {
      @Override
      public boolean visit(IResourceDelta affectedFile)  {
        VirtualFile resource = affectedFile.getResource();
        int kind = affectedFile.getKind();

        // collect package additions and removals
        if (resource instanceof IFolder && !ProjectUtil.isInOutputFolder(resource)) {
          if (kind == IResourceDelta.ADDED) {
            adedOrChangedSourceFiles.add(new ResourceProjectPair(resource));
          } else if (kind == IResourceDelta.REMOVED) {
            removedSourceFiles.add(new ResourceProjectPair(resource));
          }
        }

        // collect all types of resource changes
        if (shouldParse(resource)) {
          ResourceProjectPair resourceProjectPair = new ResourceProjectPair(resource);
          if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
            adedOrChangedSourceFiles.add(resourceProjectPair);
          } else if (kind == IResourceDelta.REMOVED) {
            removedSourceFiles.add(new ResourceProjectPair(resource));
          }
        }
        return true;
      }

    });
    if (removedSourceFiles.size() > 0) {
      parseRemovedResources(removedSourceFiles, monitor);
    }
    if (adedOrChangedSourceFiles.size() > 0) {
      parseAddedOrChangedResources(adedOrChangedSourceFiles, monitor);
    }
  }

  private void fullBuild(IProgressMonitor monitor) throws JavaModelException, CoreException {
    IJavaProject javaProject = (IJavaProject) getProject().getNature(JavaCore.NATURE_ID);
    if (DEBUG) {
      System.out.println("===== full build - " + javaProject.getProject() + " - " + Thread.currentThread().getName());
    }

    clean();
    IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
    for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
      checkForCancellation(monitor);
      if (packageFragmentRoot.getResource() instanceof IFolder) {
        IJavaElement[] packages = packageFragmentRoot.getChildren();
        for (IJavaElement package_ : packages) {
          checkForCancellation(monitor);
          IJavaElement[] classFiles = ((IPackageFragment) package_).getCompilationUnits();
          for (IJavaElement compilationUnit : classFiles) {
            checkForCancellation(monitor);
            VirtualFile resource = compilationUnit.getResource();
            if (shouldParse(resource)) {
              parseResource(new ResourceProjectPair(resource), true, monitor);
            }
          }
        }
      }
    }
  }
*/

  private void maybeLoadRelationships(Module module) throws IOException {
//    if (relationships == null) {
//      File serializationFile = GosuSaveParticipant.getSerializationFile(module);
//      if (serializationFile != null) {
//        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(serializationFile)));
//        try {
//          loadBuildState(in, module);
//        } finally {
//          in.close();
//        }
//      }
//    }
  }

  private int parseClassOrEnhancement(CompileContext context, ResourceProjectPair resource, boolean updateRelationships) {
    if (shouldIgnore(resource.name)) {
//      GosuEditor.createIssueMarkers((IFile) resource.getResource(), null, GosuEditorPlugin.getIssueMarkerID(resource.name));
      return -1;
    }

    gw.lang.reflect.IType resourceType = resource.getType();
    if(resourceType == null) { // the resource was deleted
      return -1;
    }

    VirtualFile file = resource.getResource();
    if(!(resourceType instanceof IGosuClass)) {
      ParseException ex = new ParseException(null, Res.MSG_DUPLICATE_TYPE_FOUND, resourceType.getName());
      createIssueMarker(file, ex);
      return -1;
    }

    IGosuClass gsClass = (IGosuClass) resourceType;
    TypeSystem.lock();
    TypeSystem.refresh((ITypeRef) gsClass, false);
    gsClass.setCreateEditorParser(true);
    try {
      boolean shouldReportErrors = shouldReportErrors(gsClass);
      gsClass.isValid(); // force compilation

      ParseResultsException parseResultsException = gsClass.getParseResultsException();
      //TODO-put back
//      if (updateRelationships) {
//        boolean isIncremental = shouldReportErrors && parseResultsException != null;
//        updateRelationships(resource, gsClass.getClassStatement().getClassFileStatement(), isIncremental);
//      }
//      getGosuBuilderForProject(resource.ijModule).getRelationships().get(resource.name).updateFingerprint();

      // link the enhanced type to this enhancement
      if (gsClass instanceof IGosuEnhancement) {
        IGosuEnhancement enhancement = (IGosuEnhancement)gsClass;
        gw.lang.reflect.IType enhancedType = enhancement.getEnhancedType();
        enhancedType = TypeSystem.getPureGenericType(enhancedType);
        while (enhancedType.getEnclosingType() != null) {
          enhancedType = enhancedType.getEnclosingType();
        }
        if (enhancedType instanceof IGosuClass) {
          VirtualFile enhancedTypeResource = null;//ProjectUtil.getResource(enhancedType); //TODO
          if(enhancedTypeResource != null) {
            ResourceProjectPair resourceProjectPair = new ResourceProjectPair(enhancedTypeResource, null); //TODO
            ResourceBuildInfo resourceBuildInfo = relationships.get(resourceProjectPair.name);
            if (resourceBuildInfo != null) {
              resourceBuildInfo.addRelatedResource(resource.name);
            }
          }
        }
      }

      if (shouldReportErrors) {
        createIssueMarkers(context, resource, parseResultsException);
      } else {
        createIssueMarkers(context, resource, null);
      }
      //TODO-d ptodos
//      createTaskMarkers(file, gsClass.getSource(), null);
    } catch (Exception e) {
      throw new RuntimeException(e);
//      GosuEditorPlugin.log(e);
    } finally {
      TypeSystem.unlock();
    }
    return countLines(gsClass.getSource());
  }

  private void createIssueMarker(VirtualFile file, ParseException ex) {
//    if (_suspendMarkerCreation == 0) {
//      try {
//        file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
//
//        IMarker marker = file.createMarker(markerID);
//        marker.setAttribute(IMarker.TRANSIENT, false);
//        marker.setAttribute(IMarker.SEVERITY, issue instanceof ParseException ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
//        marker.setAttribute(IMarker.MESSAGE, issue.getUIMessage());
//        marker.setAttribute(IMarker.LINE_NUMBER, 1);
//        marker.setAttribute(IMarker.CHAR_START, 0);
//        marker.setAttribute(IMarker.CHAR_END, 0);
//        marker.setAttribute(ISSUE_FINGERPRINT, issue.getUIMessage());
//      } catch (CoreException e) {
//        throw new RuntimeException(e);
//      }
    }

  public static void createIssueMarkers(CompileContext context, ResourceProjectPair resource, ParseResultsException parseResults) {
//        file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    VirtualFile file = resource.file;
    if (parseResults != null) {
      for (IParseIssue exception : parseResults.getParseExceptions()) {
        int line = exception.getLine();
        int column = exception.getColumn();
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(resource.ijModule.getProject(), file, exception.getTokenStart());
        String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, file.getPath());
        context.addMessage(CompilerMessageCategory.ERROR, exception.getUIMessage(), url, line, column, openFileDescriptor);
      }
      for (IParseIssue warning : parseResults.getParseWarnings()) {
        int line = warning.getLine();
        int column = warning.getColumn();
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(resource.ijModule.getProject(), file, warning.getTokenStart());
        String url = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, file.getPath());
        context.addMessage(CompilerMessageCategory.ERROR, warning.getUIMessage(), url, line, column, openFileDescriptor);
      }
    }
  }

  private int countLines(String source) {
    int n = 0;
    for (int i = 0; i < source.length(); i++) {
      if (source.charAt(i) == '\n') {
        n++;
      }
    }
    return n;
  }

  private boolean shouldReportErrors(IGosuClass gsClass) {
    IGosuClassTypeInfo typeInfo = gsClass.getTypeInfo();
    IType dnvr = TypeSystem.getByFullNameIfValid( "gw.testharness.DoNotVerifyResource" );
    IType dnpiide = TypeSystem.getByFullNameIfValid( "gw.lang.DoNotParseInIDE" );
    return (dnvr == null || !typeInfo.hasAnnotation( dnvr )) && (dnpiide == null || !typeInfo.hasAnnotation( dnpiide ));
  }

  public static boolean shouldIgnore(String name) {
    for (String s : ignoreList) {
      if (name.endsWith(s)) {
        return true;
      }
    }
    for (Pattern pat : ignorePats) {
      if (pat.matcher(name).matches()) {
        return true;
      }
    }
    return false;
  }

//  private int parseProgramAndUpdate(ResourceProjectPair resource, boolean updateRelationships)  {
//    if (shouldIgnore(resource.name)) {
//      GosuEditor.createIssueMarkers((IFile) resource.getResource(), null, GosuEditorPlugin.getIssueMarkerID(resource.name));
//      return -1;
//    }
//
//    VirtualFile file =resource.getResource();
//    String contents = getContents(file);
//    try {
//      String strGsClassName = resource.name.replace('/', '.');
//      strGsClassName = strGsClassName.substring(0, strGsClassName.lastIndexOf('.'));
//      strGsClassName = strGsClassName.substring(strGsClassName.indexOf('.') + 1);
//      Pair<IParsedElement, ParseResultsException> results = parseProgram(contents, strGsClassName);
//      if (updateRelationships) {
//        updateRelationships(resource, results.getFirst(), results.getSecond() != null);
//      }
//      getGosuBuilderForProject(resource.ijModule).getRelationships().get(resource.name).updateFingerprint();
//      GosuEditor.createIssueMarkers((IFile) file, results.getSecond(), GosuEditorPlugin.getIssueMarkerID(resource.name));
//      GosuEditor.createTaskMarkers((IFile) file, contents, null);
//    } catch (Exception e) {
//      GosuEditorPlugin.log(e);
//    }
//    return countLines(contents);
//  }

//  private static Pair<IParsedElement, ParseResultsException> parseProgram(String text, final String fullyQualifiedName) {
//    IGosuParser parser = GosuParserFactory.createParser(TypeSystem.getCompiledGosuClassSymbolTable(), ScriptabilityModifiers.SCRIPTABLE);
//    parser.setThrowParseExceptionForWarnings(true);
//    parser.setDontOptimizeStatementLists(true);
//    parser.setWarnOnCaseIssue(true);
//    parser.setEditorParser(true);
//    IGosuProgramParser programParser = GosuParserFactory.createProgramParser();
//    IFileContext programFileContext = new IFileContext() {
//      public String getClassNameForFile() {
//        return fullyQualifiedName;
//      }
//
//      public String getFileContext() {
//        return null;
//      }
//
//      public String getContextString() {
//        return null;
//      }
//    };
//    ParserOptions options = new ParserOptions().withParser( parser ).withFileContext(programFileContext);
//    IParseResult result;
//    ParseResultsException exception;
//    IParsedElement rootParsedElement;
//    try {
//      result = programParser.parseExpressionOrProgram( text, parser.getSymbolTable(), options );
//      IGosuProgram parsedGosuClass = result.getProgram();
//      rootParsedElement = parsedGosuClass.getClassStatement();
//      exception = parsedGosuClass.getParseResultsException();
//    } catch (ParseResultsException e) {
//      exception = e;
//      rootParsedElement = e.getParsedElement();
//    }
//    return Pair.make(rootParsedElement, exception);
//  }
//
//  private int parseTemplateAndUpdate(ResourceProjectPair resource, boolean updateRelationships)  {
//    if (shouldIgnore(resource.name)) {
//      GosuEditor.createIssueMarkers((IFile) resource.getResource(), null, GosuEditorPlugin.getIssueMarkerID(resource.name));
//      return -1;
//    }
//
//    String contents = getContents(resource.getResource());
//    IGosuParser parser = GosuParserFactory.createParser(contents);
//    try {
//      Pair<IParsedElement, ParseResultsException> results = parseTemplate(parser, GosuEditorPlugin.getFullyQualifiedName(resource.getResource()));
//      if (updateRelationships) {
//        updateRelationships(resource, results.getFirst(), results.getSecond() != null);
//      }
//      getGosuBuilderForProject(resource.ijModule).getRelationships().get(resource.name).updateFingerprint();
//      GosuEditor.createIssueMarkers((IFile) resource.getResource(), results.getSecond(), GosuEditorPlugin.getIssueMarkerID(resource.name));
//      GosuEditor.createTaskMarkers((IFile) resource.getResource(), parser.getTokenizer().getSource(), parser.getTokenizerInstructor());
//    } catch (Exception e) {
//      GosuEditorPlugin.log(e);
//    }
//    return countLines(contents);
//  }
//
//  private static Pair<IParsedElement, ParseResultsException> parseTemplate(IGosuParser parser, String strTemplateName) {
//    Pair<IParsedElement, ParseResultsException> results;
//    parser.setThrowParseExceptionForWarnings(true);
//    parser.setDontOptimizeStatementLists(true);
//    parser.setWarnOnCaseIssue(true);
//    parser.setEditorParser(true);
//    ITemplateGenerator gen = GosuShop.createSimpleTemplateHost().getTemplate(
//        new StringReader(parser.getTokenizer().getSource()), strTemplateName);
//    ParseResultsException exception = null;
//    try {
//      gen.verify(parser);
//    } catch (ParseResultsException e) {
//      exception = e;
//    }
//    IParsedElement rootParsedElement = parser.getLocations().get(0).getParsedElement();
//    results = Pair.make(rootParsedElement, exception);
//    return results;
//  }

//  public static List<? extends IParseIssue> getRootParseIssues(VirtualFile resource) {
//    try {
//      IParsedElement rootParsedElement = getRootParsedElementWithErrors(resource);
//      if(rootParsedElement != null) {
//        return rootParsedElement.getParseIssues();
//      }
//    } catch(ParseException ex) {
//      return Collections.singletonList(ex);
//    }
//    return Collections.emptyList();
//  }

//  public static IParsedElement getRootParsedElement(VirtualFile resource) {
//    try {
//      return getRootParsedElementWithErrors(resource);
//    } catch(ParseException ex) {
//      return null;
//    }
//  }

//  public static IParsedElement getRootParsedElementWithErrors(VirtualFile resource) throws ParseException {
//    String name = resource.getName();
//    IModule module = GosuEditorPlugin.getModule(resource);
//    TypeSystem.getExecutionEnvironment().pushModule(module);
//    String strTypeName = GosuEditorPlugin.getFullyQualifiedName(resource);
//    try {
//      if (GosuEditorPlugin.isGosuClass(name)) {
//        gw.lang.reflect.IType resourceType = TypeSystem.getByFullName(strTypeName);
//        if(resourceType != null) {
//          if(resourceType instanceof IGosuClass) {
//            IGosuClass gsClass = (IGosuClass) resourceType;
//            gsClass.setCreateEditorParser(true);
//            gsClass.isValid();
//            return gsClass.getClassStatement();
//          } else {
//            throw new ParseException(null, Res.MSG_DUPLICATE_TYPE_FOUND, resourceType.getName());
//          }
//        }
//        return null;
//      } else if (GosuEditorPlugin.isGosuProgram(name)) {
//        return parseProgram(getContents(resource), strTypeName).getFirst();
//      } else if (GosuEditorPlugin.isGosuTemplate(name)) {
//        gw.lang.reflect.IType resourceType = TypeSystem.getByFullName(strTypeName);
//        if(resourceType != null) {
//          if(resourceType instanceof ITemplateType) {
//            ITemplateType template = (ITemplateType) resourceType;
////            gsClass.setCreateEditorParser(true);
//            template.isValid();
//            ITemplateGenerator templateGenerator = template.getTemplateGenerator();
//            return templateGenerator.getProgram();
//          } else {
//            throw new ParseException(null, Res.MSG_DUPLICATE_TYPE_FOUND, resourceType.getName());
//          }
//        }
//        return null;
////        String contents = getContents(resource);
////        IGosuParser parser = GosuParserFactory.createParser(contents);
////        return parseTemplate(parser, GosuEditorPlugin.getFullyQualifiedName(resource)).getFirst();
//      }
//    } catch (VerifyError e) {
//      GosuEditorPlugin.log(e);
//      return null;
//    } catch (RuntimeException e) {
//      if(e.getCause() instanceof ClassNotFoundException) {
//        return null;
//      } else {
//        throw e;
//      }
//    } catch (ParseException ex) {
//      throw ex;
//    } catch (Exception e) {
//      //GosuEditorPlugin.log(e);
//      return null;
//    } finally {
//      TypeSystem.getExecutionEnvironment().popModule(module);
//    }
//    return null;
//  }

//  public static IParsedElement getCoreResourceRootParsedElement(IPath path) {
//    String extension = "." + path.getFileExtension();
//    IModule module = TypeSystem.getExecutionEnvironment().getJreModule();
//    TypeSystem.getExecutionEnvironment().pushModule(module);
//    try {
//      if (GosuEditorPlugin.isGosuClass(extension)) {
//        String strGsClassName = path.toString().replace(IPath.SEPARATOR, '.').substring(0, path.toString().length() - extension.length());
//        IGosuClass gsClass = (IGosuClass) TypeSystem.getByFullName(strGsClassName);
//        gsClass.isValid();
//        return gsClass.getClassStatement();
//      }
//    } finally {
//      TypeSystem.getExecutionEnvironment().popModule(module);
//    }
//    return null;
//  }

//  private void updateRelationships(ResourceProjectPair resource, IParsedElement rootParsedElement, boolean incremental) {
//    if (resource == null) {
//      throw new IllegalArgumentException("resource is null");
//    }
//    Set<String> namespaces = new HashSet<String>();
//    Set<String> relatedResourceNames = new HashSet<String>();
//    Set<String> unresolvedTypes = new HashSet<String>();
//
//    // deal with all expressions
//    List<IExpression> expressions = new ArrayList<IExpression>();
//    rootParsedElement.getContainedParsedElementsByType(IExpression.class, expressions);
//    Set<gw.lang.reflect.IType> types = new HashSet<gw.lang.reflect.IType>();
//    for (IExpression expression : expressions) {
//      gw.lang.reflect.IType type = expression.getType();
//      if (type != null & type instanceof IMetaType) {
//          type = ((IMetaType) type).getType();
//      }
//      if (type != null && !(type instanceof IJavaType)) {
//        if( type instanceof IErrorType && expression instanceof ITypeLiteralExpression) {
//          IExpression packageExpression = ((ITypeLiteralExpression)expression).getPackageExpression();
//          if (packageExpression != null) {
//            type = TypeSystem.getErrorType( packageExpression.toString() );
//          }
//        }
//        if( type != TypeSystem.getErrorType()
////            && !(expression instanceof ITypeLiteralExpression && isTypeLiteralInAncestry(expression.getParent()))
//            ) {
//          types.add(type);
//        }
//      }
//
//      if (expression instanceof IMethodCallExpression) {
//        IMethodCallExpression methodCall = ((IMethodCallExpression) expression);
//        IFunctionSymbol functionSymbol = methodCall.getFunctionSymbol();
//        if (functionSymbol != null && !(functionSymbol.getType() instanceof IBlockType)
//            && !functionSymbol.getName().equals("print")) {
//          IFunctionType functionType = methodCall.getFunctionType();
//          if (functionType != null) {
//            IMethodInfo methodInfo = functionType.getMethodInfo();
//            if (methodInfo != null) {
//              gw.lang.reflect.IType ownersType = methodInfo.getOwnersType();
//              if (ownersType instanceof IGosuEnhancement) {
//                types.add(ownersType);
//              }
//            }
//          }
//        }
//      } else if (expression instanceof IBeanMethodCallExpression) {
//        IBeanMethodCallExpression methodCall = ((IBeanMethodCallExpression) expression);
//        IMethodInfo methodInfo = methodCall.getMethodDescriptor();
//        if (methodInfo != null) {
//          gw.lang.reflect.IType ownersType = methodInfo.getOwnersType();
//          if (ownersType instanceof IGosuEnhancement) {
//            types.add(ownersType);
//          }
//        }
//      } else if (expression instanceof IFieldAccessExpression) {
//        IFieldAccessExpression memberAccess = ((IFieldAccessExpression) expression);
//        try {
//          IPropertyInfo propertyInfo = memberAccess.getPropertyInfo();
//          if (propertyInfo != null) {
//            gw.lang.reflect.IType ownersType = propertyInfo.getOwnersType();
//            if (ownersType instanceof IGosuEnhancement) {
//              types.add(ownersType);
//            }
//          }
//        } catch (RuntimeException e) {
//          continue;
//        }
//      }
//    }
//
//    // deal with the package statement
//    if (rootParsedElement instanceof IClassFileStatement) {
//      IGosuClass gosuClass = ((IClassFileStatement)rootParsedElement).getGosuClass();
//      if(gosuClass != null) {
//        namespaces.add(gosuClass.getNamespace());
//      }
//    }
//
//    // deal with uses statements
//    List<IUsesStatement> usesStatements = new ArrayList<IUsesStatement>();
//    rootParsedElement.getContainedParsedElementsByType(IUsesStatement.class, usesStatements);
//    for (IUsesStatement usesStatement : usesStatements) {
//      String typeName = usesStatement.getTypeName();
//      if (typeName.endsWith("*")) {
//        namespaces.add(typeName.substring(0, typeName.lastIndexOf('.')));
//      } else {
//        gw.lang.reflect.IType type = TypeSystem.getByFullNameIfValid(typeName);
//        if (type != null && !(type instanceof INamespaceType)) {
//          types.add(type);
//
//          int i = typeName.lastIndexOf('.');
//          while (i >= 0) {
//            typeName = typeName.substring(0, i);
//            type = TypeSystem.getByFullNameIfValid(typeName);
//            if (type != null && !(type instanceof INamespaceType)) {
//              types.add(type);
//            }
//            i = typeName.lastIndexOf('.');
//          }
//        } else {
//          unresolvedTypes.add(typeName);
//        }
//      }
//    }
//
//    // deal with enhancements of the type
//    if (rootParsedElement instanceof IClassFileStatement) {
//      IGosuClass gosuClass = ((IClassFileStatement)rootParsedElement).getGosuClass();
//      if(gosuClass != null) {
//        IEnhancementIndex enhancementIndex = gosuClass.getTypeLoader().getEnhancementIndex();
//        List<? extends IGosuEnhancement> enhancements = enhancementIndex.getEnhancementsForGenericType(gosuClass);
//        types.addAll(enhancements);
//      }
//    }
//
//    IGosuClass self = ParseTreeUtil.getContainingGosuClass(rootParsedElement);
//    for (gw.lang.reflect.IType type : types) {
//      if (!type.equals(self) && !(type instanceof IJavaType)) {
//        // de-generify the type
//        gw.lang.reflect.IType genericType = ProjectUtil.getPureGenericType(type);
//        if (genericType != null) {
//          type = genericType;
//        }
//
//        // get the outer-most enclosing type
//        while (type.getEnclosingType() != null) {
//          type = type.getEnclosingType();
//        }
//
//        if (type instanceof IErrorType) {
//          String errantTypeName = ((IErrorType)type).getErrantTypeName();
//          unresolvedTypes.add(errantTypeName);
//        } else {
//          VirtualFile theResource = ProjectUtil.getResource(type);
//          if (theResource != null) {
//            relatedResourceNames.add(theResource.getProjectRelativePath().toPortableString());
//          }
//        }
//      }
//    }
//
//    GosuBuilder gosuBuilderForProject = getGosuBuilderForProject(resource.ijModule);
//    Map<String, ResourceBuildInfo> relationshipsMap = gosuBuilderForProject.getRelationships();
//    ResourceBuildInfo buildInfo = relationshipsMap.get(resource.name);
//    if (buildInfo == null) {
//      buildInfo = new ResourceBuildInfo(resource.name, resource.getModule());
//      relationshipsMap.put(resource.name, buildInfo);
//    }
//    if (incremental) {
//      buildInfo.addRelationships(namespaces, relatedResourceNames, unresolvedTypes);
//    } else {
//      buildInfo.setRelationships(namespaces, relatedResourceNames, unresolvedTypes);
//    }
//  }

  private boolean isTypeLiteralInAncestry(IParsedElement expression) {
    if( expression == null ) {
      return false;
    }
    if( expression instanceof ITypeLiteralExpression ) {
      return true;
    }
    return isTypeLiteralInAncestry( expression.getParent() );
  }

//  private Map<String, ResourceBuildInfo> getRelationships()  {
//    try {
//      maybeLoadRelationships(getProject());
//    } catch (IOException e) {
//      GosuEditorPlugin.log("Could not load relationships table", e);
//    }
//    return relationships;
//  }

//  private static String getContents(VirtualFile resource) {
//    String contents = null;
//    GosuEditor activeEditor = GosuEditorPlugin.getActiveEditor(resource);
//    if (activeEditor != null) {
//      contents = activeEditor.getViewer().getDocument().get();
//    }
//    if (contents == null) {
//      InputStream stream = ((IFile) resource).getContents(true);
//      try {
//        contents = StreamUtil.getContent(new InputStreamReader(stream));
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      } finally {
//        if (stream != null) {
//          try {
//            stream.close();
//          } catch (IOException e) {
//            // whatever
//          }
//        }
//      }
//    }
//    return contents;
//  }

  private boolean shouldParse(VirtualFile resource)  {
    return isGosu(resource.getName());
  }

  public boolean isGosu(String fileName) {
    return fileName.endsWith(GosuClassTypeLoader.GOSU_CLASS_FILE_EXT) || fileName.endsWith(GosuClassTypeLoader.GOSU_ENHANCEMENT_FILE_EXT)
        || fileName.endsWith(".gsp") || fileName.endsWith(GosuTemplateTypeLoader.GOSU_TEMPLATE_FILE_EXT);
  }

//  private void parseAddedOrChangedResources(LinkedHashSet<ResourceProjectPair> affectedSourceFiles, IProgressMonitor monitor)  {
//    // remove resources with changes that cannot affect other resources (non-signature changes)
//    Set<ResourceProjectPair> toBeRemovedUpFront = new HashSet<ResourceProjectPair>();
//    Set<ResourceProjectPair> toNotBeParsedAgain = new HashSet<ResourceProjectPair>();
//    for (ResourceProjectPair resource : affectedSourceFiles) {
//      ResourceBuildInfo buildInfo = relationships.get(resource.name);
//      if (buildInfo != null) {
//        long oldFingerprint = buildInfo.getRawFingerprint();
//        parseResource(resource, false, monitor);
//        long newFingerprint = buildInfo.getRawFingerprint();
//        if (newFingerprint == oldFingerprint) {
//          toBeRemovedUpFront.add(resource);
//        }
//        toNotBeParsedAgain.add(resource);
//      }
//    }
//    affectedSourceFiles.removeAll(toBeRemovedUpFront);
//
//    // recursively gather the set of all possibly affected files starting at the given roots
//    LinkedHashSet<ResourceProjectPair> originalAffectedSourceFiles = new LinkedHashSet<ResourceProjectPair>(affectedSourceFiles);
//    Set<String> visitedFiles = new HashSet<String>();
//    for (ResourceProjectPair affectedSourceFile : originalAffectedSourceFiles) {
//      gatherAffectedResources(affectedSourceFile, affectedSourceFiles, visitedFiles);
//    }
//    affectedSourceFiles.removeAll(toNotBeParsedAgain);
//
//    // update relationships for the already-parsed resources
//    for (ResourceProjectPair resource : toNotBeParsedAgain) {
//      parseResource(resource, true, monitor);
//    }
//
//    // parse all the files that could be affected
//    for (ResourceProjectPair affectedSourceFile : affectedSourceFiles) {
//      checkForCancellation(monitor);
//      parseResource(affectedSourceFile, true, monitor);
//    }
//  }
//
//  private void parseRemovedResources(LinkedHashSet<ResourceProjectPair> removedResources, IProgressMonitor monitor)  {
//    // recursively gather the set of all possibly affected files starting at the given roots
//    LinkedHashSet<ResourceProjectPair> affectedResources = new LinkedHashSet<ResourceProjectPair>();
//
//    Set<String> visitedFiles = new HashSet<String>();
//    for (ResourceProjectPair removedResource : removedResources) {
//      gatherAffectedResources(removedResource, affectedResources, visitedFiles);
//    }
//
//    // parse all the files that could be affected, except the removed ones
//    affectedResources.removeAll(removedResources);
//    for (ResourceProjectPair affectedSourceFile : affectedResources) {
//      checkForCancellation(monitor);
//      parseResource(affectedSourceFile, true, monitor);
//    }
//
//    // delete the entries from the relationship map
//    for (ResourceProjectPair removedResource : removedResources) {
//      relationships.remove(removedResource.name);
//    }
//  }
//
//  private void gatherAffectedResources(ResourceProjectPair affectedResource,
//      LinkedHashSet<ResourceProjectPair> affectedSourceFiles, Set<String> visitedFiles)  {
//
//    visitedFiles.add(affectedResource.name);
//
//    // look through our own relationships map
//    for (String resourceName : relationships.keySet()) {
//      ResourceBuildInfo buildInfo = relationships.get(resourceName);
//      if (buildInfo.references(affectedResource.name) && !visitedFiles.contains(resourceName)) {
//        ResourceProjectPair resource = new ResourceProjectPair(resourceName, affectedResource.ijModule);
//        affectedSourceFiles.add(resource);
//        gatherAffectedResources(resource, affectedSourceFiles, visitedFiles);
//      }
//    }
//
//    // look through dependent projects
//    LinkedHashSet<IProject> referencingProjects = getAllReferencingProjects(affectedResource.ijModule, new LinkedHashSet<IProject>());
//    for (IProject project : referencingProjects) {
//      GosuBuilder builder = getGosuBuilderForProject(project.getProject());
//      if (builder != null) {
//        for (String resourceName : builder.getRelationships().keySet()) {
//          ResourceBuildInfo buildInfo = builder.getRelationships().get(resourceName);
//          if (buildInfo.references(affectedResource.name) && !visitedFiles.contains(resourceName)) {
//            ResourceProjectPair resource = new ResourceProjectPair(resourceName, project);
//            affectedSourceFiles.add(resource);
//            builder.gatherAffectedResources(resource, affectedSourceFiles, visitedFiles);
//          }
//        }
//      }
//    }
//  }
//
//  private LinkedHashSet<IProject> getAllReferencingProjects(IProject project, LinkedHashSet<IProject> projects) {
//    IProject[] referencingProjects = project.getReferencingProjects();
//    for (IProject referencingProject : referencingProjects) {
//      projects.add(referencingProject);
//      getAllReferencingProjects(referencingProject, projects);
//    }
//    return projects;
//  }
//
//  public static GosuBuilder getGosuBuilderForProject(IProject project)
//       {
//    ICommand[] buildSpec = ((Project) project).internalGetDescription().getBuildSpec(false);
//    for (ICommand command : buildSpec) {
//      IncrementalProjectBuilder builder = ((BuildCommand) command).getBuilder();
//      if (builder instanceof GosuBuilder) {
//        return (GosuBuilder) builder;
//      }
//    }
//    return null;
//  }

  private void parseResource(CompileContext context, ResourceProjectPair resource, boolean updateRelationships)  {
    if (!isEnabled()) return;
    long t1 = 0, t2;
    if (DEBUG && updateRelationships) {
//      monitor.subTask("Gosu Builder: " + resource.name);
      System.out.println("Parsing " + resource.name);
      testParsedResources.add(resource.name.substring(resource.name.lastIndexOf('/') + 1));
      t1 = System.nanoTime();
    }

    IModule module = TypeSystem.getExecutionEnvironment().getModule(resource.ijModule.getName());
    TypeSystem.getExecutionEnvironment().pushModule(module);
    int nLines = -1;
    try {
      if (resource.name.endsWith(".gs") || resource.name.endsWith(".gsx")) {
        nLines = parseClassOrEnhancement(context, resource, updateRelationships);
      } else if (resource.name.endsWith(".gst")) {
//        nLines = parseTemplateAndUpdate(resource, updateRelationships);
      } else if (resource.name.endsWith(".gsp")) {
//        nLines = parseProgramAndUpdate(resource, updateRelationships);
      }
    } finally {
      TypeSystem.getExecutionEnvironment().popModule(module);
    }

    if (DEBUG) {
      t2 = System.nanoTime();
      if (nLines != -1) {
        recordParseStatistics(module.getName() + "/" + resource.name, (t2 - t1)/1e6/nLines);
      }
    }

//    EclipseMemoryCleaner.getInstance().maybeRefresh();

    // allow other threads to do something!
    Thread.yield();
  }

  private void recordParseStatistics(String name, double time) {
    _parseStatistics.add(new ResourceParseTime(name, time));
  }

  public void saveBuildState(File serializationFile) throws IOException  {
    if (relationships == null) {
      return;
    }

    DataOutputStream out = null;
    try {
      out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(serializationFile)));
      out.writeInt(relationships.size());
      for (String resourceName : relationships.keySet()) {
        ResourceBuildInfo buildInfo = relationships.get(resourceName);
        buildInfo.write(out);
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

//  public void loadBuildState(DataInputStream in, IProject project) throws IOException, CoreException {
//    int mapSize = in.readInt();
//    relationships = new HashMap<String, ResourceBuildInfo>(mapSize);
//    IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
//    for (int i = 0; i < mapSize; i++) {
//      ResourceBuildInfo buildInfo = ResourceBuildInfo.read(in, javaProject);
//      relationships.put(buildInfo.getResourceName(), buildInfo);
//    }
//  }

  @NotNull
  @Override
  public String getDescription() {
    return "Gosu Builder";
  }

  @Override
  public boolean validateConfiguration(CompileScope scope) {
    return true;
  }
}
