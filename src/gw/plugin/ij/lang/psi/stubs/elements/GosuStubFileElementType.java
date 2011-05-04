package gw.plugin.ij.lang.psi.stubs.elements;

import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import gw.lang.parser.GosuParserFactory;
import gw.lang.parser.IGosuParser;
import gw.lang.parser.IGosuProgramParser;
import gw.lang.parser.IParseResult;
import gw.lang.parser.ParserOptions;
import gw.lang.parser.ScriptabilityModifiers;
import gw.lang.parser.exceptions.ParseResultsException;
import gw.lang.parser.statements.IClassFileStatement;
import gw.lang.parser.statements.IClassStatement;
import gw.lang.reflect.ITemporaryFileProvider;
import gw.lang.reflect.ITypeRef;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.gs.ClassType;
import gw.lang.reflect.gs.GosuClassTypeLoader;
import gw.lang.reflect.gs.IFileSystemGosuClassRepository;
import gw.lang.reflect.gs.IGosuClass;
import gw.lang.reflect.gs.IGosuProgram;
import gw.lang.reflect.gs.ISourceFileHandle;
import gw.lang.reflect.gs.StringSourceFileHandle;
import gw.lang.reflect.module.IModule;
import gw.plugin.ij.GosuClassFileType;
import gw.plugin.ij.GosuProgramFileType;
import gw.plugin.ij.activator.Activator;
import gw.plugin.ij.lang.GosuTokenTypes;
import gw.plugin.ij.lang.parser.GosuAstTransformer;
import gw.plugin.ij.lang.parser.GosuCompositeElement;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.stubs.GosuFileStub;
import gw.plugin.ij.lang.psi.stubs.GosuFileStubBuilder;
import gw.plugin.ij.lang.psi.stubs.index.GosuClassNameIndex;
import gw.plugin.ij.lang.psi.stubs.index.GosuFullClassNameIndex;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuStubFileElementType extends IStubFileElementType<GosuFileStub>
{
  public static final Key<ParseResultsException> KEY_PARSE_RESULTS = new Key<ParseResultsException>( "_parseResults" );

  public GosuStubFileElementType( Language language )
  {
    super( language );
  }

  public StubBuilder getBuilder()
  {
    return new GosuFileStubBuilder();
  }

  @Override
  public int getStubVersion()
  {
    return super.getStubVersion() + 2;
  }

  public String getExternalId()
  {
    return "gosu.FILE";
  }

  @Override
  public void indexStub( PsiFileStub stub, IndexSink sink )
  {
    super.indexStub( stub, sink );
  }


  public ASTNode parseContents( ASTNode chameleon )
  {
    GosuClassFileImpl psiFile = (GosuClassFileImpl)chameleon.getPsi();
    Module ijMod = getModuleForPsi( psiFile );
    IModule module;
    if( ijMod == null )
    {
      module = Activator.getUberModule();
    }
    else
    {
      if( psiFile.getFileFromPsi() instanceof VirtualFileWindow )
      {
        return parseAsInjectedExpression( chameleon, psiFile, ijMod );
      }
      else
      {
        module = TypeSystem.getExecutionEnvironment().getModule( ijMod.getName() );
      }
    }
    return parseAsType( chameleon, psiFile, module );
  }

  private ASTNode parseAsInjectedExpression( ASTNode chameleon, GosuClassFileImpl psi, Module ijMod )
  {
    IGosuParser parser = GosuParserFactory.createParser( TypeSystem.getCompiledGosuClassSymbolTable(), ScriptabilityModifiers.SCRIPTABLE );
    parser.setThrowParseExceptionForWarnings( true );
    parser.setDontOptimizeStatementLists( true );
    parser.setWarnOnCaseIssue( true );
    parser.setEditorParser( true );

    final String fqn = "gw.lang.__Foo__";
    final String optVarPrefix = "var __xx__=";
    final String fakeClassPlusFunctionPrefix = "package gw.lang\nclass __Foo__ {\n  function __test__() {";
    final String fakeClassPlusFunctionSuffix = "\n  }\n}";
    final String fakeClassPrefix = "package gw.lang\nclass __Foo__ {";
    final String fakeClassSuffix = "\n}";
    ExprType exprType = ExprType.METHOD_CALL;
    IGosuClass gsClass = null;

    IModule module = TypeSystem.getExecutionEnvironment().getModule( ijMod.getName() );
    TypeSystem.getExecutionEnvironment().pushModule( module );

    String text = ((VirtualFileWindow)psi.getFileFromPsi()).getDocumentWindow().getText();
    if( text == null || text.length() == 0 )
    {
      text = psi.getText();
    }
    assert text != null && text.length() > 0;

    int wsIndex = 0;
    while( wsIndex < text.length() && Character.isWhitespace( text.charAt( wsIndex ) ) )
    {
      ++wsIndex;
    }
    String wsPrefix = wsIndex != 0 ? text.substring( 0, wsIndex ) : null;
    wsIndex = text.length() - 1;
    while( wsIndex > 0 && Character.isWhitespace( text.charAt( wsIndex ) ) )
    {
      --wsIndex;
    }
    String wsSuffix = wsIndex < text.length() - 1 ? text.substring( wsIndex + 1 ) : null;

    try
    {
      exprType = computeExpressionType( text );
      String fakeClassText;
      if( exprType == ExprType.VAR )
      {
        fakeClassText = fakeClassPlusFunctionPrefix + optVarPrefix + text + fakeClassPlusFunctionSuffix;
      }
      else if( exprType == ExprType.FUNCTION )
      {
        fakeClassText = fakeClassPrefix + text + fakeClassSuffix;
      }
      else
      {
        fakeClassText = fakeClassPlusFunctionPrefix + text + fakeClassPlusFunctionSuffix;
      }

      StringSourceFileHandle sourceFile = new StringSourceFileHandle( fqn, fakeClassText, null, false, ClassType.Class );
      try
      {
//        parser.setScript(text);
//        parser.parseExp(new TypelessScriptPartId("scriptlet"));
        gsClass = parser.parseClass( fqn, sourceFile, true, true );
      }
      catch( ParseResultsException ex )
      {
        IClassFileStatement classFileStmt = (IClassFileStatement)ex.getParsedElement();
        IClassStatement classStatement = classFileStmt.getClassStatement();
        gsClass = classStatement != null ? classStatement.getGosuClass() : null;
      }
      catch( Throwable t )
      {
        t.printStackTrace();
      }
    }
    finally
    {
      TypeSystem.getExecutionEnvironment().popModule( module );
    }
    try
    {
      ASTNode ast = GosuAstTransformer.instance().transform( chameleon, gsClass );
      TreeElement trimmed = (TreeElement)trimToExpressionTree( ast, exprType );
//    System.out.println("TRIMMED AST = '" + trimmed.getText() + "'");
      if( wsSuffix != null )
      {
        TreeElement wsSuffixNode = ASTFactory.leaf( GosuTokenTypes.TT_WHITESPACE, wsSuffix );
        if( exprType == ExprType.FUNCTION )
        {
          TreeElement node = trimmed;
          while( node.getTreeNext() != null )
          {
            node = node.getTreeNext();
          }
          if( node != null )
          {
            wsSuffixNode.setTreePrev( node );
            node.setTreeNext( wsSuffixNode );
          }
        }
        else
        {
          wsSuffixNode.setTreePrev( trimmed );
          trimmed.setTreeNext( wsSuffixNode );
        }
      }
      if( wsPrefix != null && !(trimmed instanceof PsiWhiteSpace) )
      {
        TreeElement wsPrefixNode = ASTFactory.leaf( GosuTokenTypes.TT_WHITESPACE, wsPrefix );
        wsPrefixNode.setTreeNext( trimmed );
        trimmed.setTreePrev( wsPrefixNode );
        trimmed = wsPrefixNode;
      }
      return trimmed;
    }
    catch( Throwable t )
    {
      t.printStackTrace();
    }
    return null;
  }

  private ASTNode trimToExpressionTree( ASTNode ast, ExprType exprType )
  {
    ASTNode result = null;
    try
    {
      ASTNode exprNode = findNodeByType( ast, exprType );
      // handle VAR nodes correctly.
      if( exprType == ExprType.VAR )
      {
        result = exprNode.getFirstChildNode().getTreeNext().getTreeNext().getTreeNext().getTreeNext();
        ((GosuCompositeElement)result).setTreePrev( null );
        for( TreeElement node = (TreeElement)result; node != null; node = node.getTreeNext() )
        {
          node.setTreeParent( null );
        }
      }
      else if( exprType == ExprType.FUNCTION )
      {
        result = exprNode;
        ASTNode tail = result;
        while( tail != null && !tail.toString().equals( "PsiElement(})" ) )
        {
          tail = tail.getTreeNext();
        }
        if( tail != null )
        {
          tail = tail.getTreePrev();
          if( tail instanceof PsiWhiteSpace )
          {
            tail = tail.getTreePrev();
          }
          ((TreeElement)tail).setTreeNext( null );
        }
        ((GosuCompositeElement)result).setTreePrev( null );
        for( TreeElement node = (TreeElement)result; node != null; node = node.getTreeNext() )
        {
          node.setTreeParent( null );
        }
      }
      else
      {
        result = exprNode;
        ((GosuCompositeElement)result).setTreeNext( null );
        ((GosuCompositeElement)result).setTreePrev( null );
        ((GosuCompositeElement)result).setTreeParent( null );
      }
    }
    catch( Throwable t )
    {
      result = null;
      t.printStackTrace();
    }
    return result;
  }

  private ASTNode findNodeByType( ASTNode ast, ExprType exprType )
  {
    ASTNode result = null;
    for( ASTNode child = ast.getFirstChildNode(); result == null && child != null; child = child.getTreeNext() )
    {
      if( child.toString().contains( exprType.getType() ) )
      {
        result = child;
      }
      else if( child.getFirstChildNode() != null )
      {
        result = findNodeByType( child, exprType );
      }
    }
    return result;
  }

  public static enum ExprType
  {
    VAR( "VarStatement" ),
    IF( "IfStatement" ),
    FOR( "ForStatement" ),
    FUNCTION( "(method definition)" ),
    METHOD_CALL( "MethodCallStatement" );

    private String _type;

    ExprType( String type )
    {
      _type = type;
    }

    public String getType()
    {
      return _type;
    }
  }

  private static Pattern looksLikeMethodCallPattern = Pattern.compile( "\\s*[a-zA-Z][a-zA-Z0-9_]*\\s*\\(" );
  private static Pattern looksLikeIfPattern = Pattern.compile( "\\s*if*\\s*\\(" );
  private static Pattern looksLikeForPattern = Pattern.compile( "\\s*for*\\s*\\(" );
  private static Pattern looksLikeFunctionPattern = Pattern.compile( "\\s*function\\s*[a-zA-Z][a-zA-Z0-9_]*\\(" );

  private ExprType computeExpressionType( String text )
  {
    if( looksLikeFunctionPattern.matcher( text ).find() )
    {
      return ExprType.FUNCTION;
    }
    else if( looksLikeIfPattern.matcher( text ).find() )
    {
      return ExprType.IF;
    }
    else if( looksLikeForPattern.matcher( text ).find() )
    {
      return ExprType.FOR;
    }
    else if( looksLikeMethodCallPattern.matcher( text ).find() )
    {
      return ExprType.METHOD_CALL;
    }
    else
    {
      return ExprType.VAR;
    }
  }

  private ASTNode parseAsType( ASTNode chameleon, GosuClassFileImpl psiFile, IModule module )
  {
    if( psiFile.getFileType() == GosuClassFileType.instance() )
    {
      return parseAsClass( chameleon, psiFile, module );
    }
    else if( psiFile.getFileType() == GosuProgramFileType.instance() )
    {
      return parseAsProgram( chameleon, psiFile, module );
    }
    throw new UnsupportedOperationException( "Unhandled type: " + psiFile.getExtension() );
  }

//  private ASTNode parseAsClass( ASTNode chameleon, GosuClassFileImpl psi, IModule module )
//  {
//    TypeSystem.lock();
//    try
//    {
//      String strClassName = psi.classNameFromFile();
//
//      TypeSystem.getExecutionEnvironment().pushModule( module );
//
//      CharSequence contents = psi.getViewProvider().getContents();
//      boolean bForCompletion = contents.toString().contains( "IntellijIdeaRulezzz" );
//      ITemporaryFileProvider fileProvider = addTemporaryFileProvider( strClassName, psi.getExtension(), contents );
//      try
//      {
//        IGosuClass gsClass = (IGosuClass)TypeSystem.getByFullName( strClassName );
//        TypeSystem.refresh( (ITypeRef)gsClass, true );
//        gsClass.isValid();
//        if( !bForCompletion )
//        {
//          //noinspection ThrowableResultOfMethodCallIgnored
//          psi.getOriginalFile().putUserData( KEY_PARSE_RESULTS, gsClass.getParseResultsException() );
//        }
//        return GosuAstTransformer.instance().transform( chameleon, gsClass ).getFirstChildNode();
//      }
//      catch( Exception e )
//      {
//        throw new RuntimeException( e );
//      }
//      finally
//      {
//        removeTemporaryFileProvider( fileProvider );
//        TypeSystem.getExecutionEnvironment().popModule( module );
//      }
//    }
//    finally
//    {
//      TypeSystem.unlock();
//    }
//  }

  private ASTNode parseAsClass( ASTNode chameleon, GosuClassFileImpl psiFile, IModule module )
  {
    String strClassName = psiFile.classNameFromFile();

    CharSequence contents = psiFile.getViewProvider().getContents();
    boolean bForCompletion = contents.toString().contains( "IntellijIdeaRulezzz" );

    IGosuParser parser = GosuParserFactory.createParser( TypeSystem.getCompiledGosuClassSymbolTable(),
                                                         ScriptabilityModifiers.SCRIPTABLE );
    parser.setThrowParseExceptionForWarnings( true );
    parser.setDontOptimizeStatementLists( true );
    parser.setWarnOnCaseIssue( true );
    parser.setEditorParser( true );

//    if( _typeUsesMap != null )
//    {
//      parser.setTypeUsesMap( _typeUsesMap );
//    }
    TypeSystem.getExecutionEnvironment().pushModule( module );
    try
    {
      if( psiFile.getFileType() == GosuClassFileType.instance() )
      {
        IGosuClass gsClass = null;
        try
        {
          gsClass = parser.parseClass( strClassName, new StringSourceFileHandle( strClassName, contents, false, ClassType.Class ), true, true );
        }
        catch( ParseResultsException e )
        {
          if( e.getParsedElement() instanceof IClassFileStatement )
          {
            IClassFileStatement classFileStmt = (IClassFileStatement)e.getParsedElement();
            if( classFileStmt.getClassStatement() != null )
            {
              gsClass = classFileStmt.getClassStatement().getGosuClass();
            }
          }
        }
        if( !bForCompletion )
        {
          //noinspection ThrowableResultOfMethodCallIgnored
          psiFile.getOriginalFile().putUserData( KEY_PARSE_RESULTS, gsClass.getParseResultsException() );
        }
        return GosuAstTransformer.instance().transform( chameleon, gsClass ).getFirstChildNode();
      }
      else
      {
        throw new UnsupportedOperationException( "" );
      }
    }
    finally
    {
      TypeSystem.getExecutionEnvironment().popModule( module );
    }
  }

  private ASTNode parseAsProgram( ASTNode chameleon, GosuClassFileImpl psiFile, IModule module )
  {
    CharSequence contents = psiFile.getViewProvider().getContents();
    boolean bForCompletion = contents.toString().contains( "IntellijIdeaRulezzz" );

    IGosuParser parser = GosuParserFactory.createParser( TypeSystem.getCompiledGosuClassSymbolTable(),
                                                         ScriptabilityModifiers.SCRIPTABLE );
    parser.setThrowParseExceptionForWarnings( true );
    parser.setDontOptimizeStatementLists( true );
    parser.setWarnOnCaseIssue( true );
    parser.setEditorParser( true );
    parser.setScript( contents );

//    if( _typeUsesMap != null )
//    {
//      parser.setTypeUsesMap( _typeUsesMap );
//    }
    TypeSystem.getExecutionEnvironment().pushModule( module );
    try
    {
      IGosuProgram gsProgram;
      try
      {
        IGosuProgramParser programParser = GosuParserFactory.createProgramParser();
        ParserOptions options = new ParserOptions().withParser( parser );
        IParseResult result = programParser.parseExpressionOrProgram( chameleon.getText(), parser.getSymbolTable(), options );
        gsProgram = result.getProgram();
      }
      catch( ParseResultsException ex )
      {
        IClassFileStatement classFileStmt = (IClassFileStatement)ex.getParsedElement();
        IClassStatement classStatement = classFileStmt.getClassStatement();
        gsProgram = classStatement != null ? (IGosuProgram)classStatement.getGosuClass() : null;
      }
      if( !bForCompletion )
      {
        //noinspection ThrowableResultOfMethodCallIgnored
        psiFile.getOriginalFile().putUserData( KEY_PARSE_RESULTS, gsProgram.getParseResultsException() );
      }
      return GosuAstTransformer.instance().transform( chameleon, gsProgram ).getFirstChildNode();
    }
    finally
    {
      TypeSystem.getExecutionEnvironment().popModule( module );
    }
  }

  private Module getModuleForPsi( GosuClassFileImpl psi )
  {
    Module mod = ModuleUtil.findModuleForPsiElement( psi );
    if( mod == null )
    {
      VirtualFile vfile = psi.getFileFromPsi();
//      System.out.println("vfile for psi " + psi.getClass().getSimpleName() + psi.hashCode() + " is " +
//              vfile.getClass().getSimpleName() + vfile.hashCode());
      if( vfile != null )
      {
        mod = ModuleUtil.findModuleForFile( vfile, psi.getProject() );
      }
    }
    if( mod == null )
    {
      //throw new IllegalStateException( "No module found for: " + psi );
    }
    return mod;
  }

  public static ITemporaryFileProvider addTemporaryFileProvider( final String gosuTypeName, final String extension, final CharSequence contents )
  {
    TemporaryFileProvider fileProvider = new TemporaryFileProvider( extension, gosuTypeName, contents );
    GosuClassTypeLoader gosuClassTypeLoader = TypeSystem.getTypeLoader( GosuClassTypeLoader.class, TypeSystem.getCurrentModule() );
    ((IFileSystemGosuClassRepository)gosuClassTypeLoader.getRepository()).addTemporaryFileProvider( fileProvider );
    return fileProvider;
  }

  public static void removeTemporaryFileProvider( ITemporaryFileProvider fileProvider )
  {
    GosuClassTypeLoader typeLoader = TypeSystem.getTypeLoader( GosuClassTypeLoader.class, TypeSystem.getCurrentModule() );
    ((IFileSystemGosuClassRepository)typeLoader.getRepository()).removeTemporaryFileProvider( fileProvider );
  }

  @Override
  public void serialize( final GosuFileStub stub, final StubOutputStream dataStream ) throws IOException
  {
    dataStream.writeName( stub.getPackageName().toString() );
    dataStream.writeName( stub.getName().toString() );
  }

  @Override
  public GosuFileStub deserialize( final StubInputStream dataStream, final StubElement parentStub ) throws IOException
  {
    StringRef packName = dataStream.readName();
    StringRef name = dataStream.readName();
    return new GosuFileStub( packName, name );
  }

  public void indexStub( GosuFileStub stub, IndexSink sink )
  {
    String name = stub.getName().toString();
    if( name != null )
    {
      sink.occurrence( GosuClassNameIndex.KEY, name );
      final String pName = stub.getPackageName().toString();
      final String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
      sink.occurrence( GosuFullClassNameIndex.KEY, fqn.hashCode() );
    }
  }

  private static class TemporaryFileProvider implements ITemporaryFileProvider
  {
    private final String _extension;
    private final String _gosuTypeName;
    private CharSequence _contents;

    public TemporaryFileProvider( String extension, String gosuTypeName, CharSequence contents )
    {
      _extension = extension;
      _gosuTypeName = gosuTypeName;
      _contents = contents;
    }

    @Override
    public ISourceFileHandle getSourceFileHandle( String fullyQualifiedName, String[] extensions )
    {
      boolean bFound = false;
      for( String strExt : extensions )
      {
        if( strExt.equalsIgnoreCase( _extension ) )
        {
          bFound = true;
          break;
        }
      }
      if( !bFound )
      {
        return null;
      }
      if( fullyQualifiedName.equalsIgnoreCase( _gosuTypeName ) )
      {
        ClassType classType;
        if( GosuClassTypeLoader.GOSU_ENHANCEMENT_FILE_EXT.endsWith( _extension ) )
        {
          classType = ClassType.Enhancement;
        }
        else if( GosuClassTypeLoader.GOSU_CLASS_FILE_EXT.endsWith( _extension ) )
        {
          classType = ClassType.Class;
        }
        else
        {
          classType = null;
        }
        return new StringSourceFileHandle( _gosuTypeName, _contents, false, classType )
        {
          @Override
          protected CharSequence getRawSource()
          {
            return _contents;
          }
        };
      }
      else
      {
        return null;
      }
    }
  }
}
