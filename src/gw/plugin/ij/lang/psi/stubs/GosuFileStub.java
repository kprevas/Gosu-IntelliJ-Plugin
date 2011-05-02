package gw.plugin.ij.lang.psi.stubs;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import gw.plugin.ij.lang.parser.GosuParserDefinition;
import gw.plugin.ij.lang.psi.GosuFile;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuFileStub extends PsiFileStubImpl<GosuFile>
{
  private StringRef _packageName;
  private StringRef _name;

  public GosuFileStub( GosuFile file )
  {
    super( file );
  }

  public GosuFileStub( StringRef packName, StringRef name )
  {
    super( null );
    _packageName = packName;
    _name = name;
  }

  public IStubFileElementType getType()
  {
    return GosuParserDefinition.GOSU_FILE;
  }

  public StringRef getPackageName()
  {
    if( _packageName == null )
    {
      _packageName = StringRef.fromString( getPsi().getPackageName() );
    }
    return _packageName;
  }

  public StringRef getName()
  {
    if( _name == null )
    {
      _name = StringRef.fromString( StringUtil.trimEnd( getPsi().getName(), ".gs" ) );
    }
    return _name;
  }

  public String getTypeName()
  {
    return getPackageName().getString() + '.' + getName().getString();
  }
}
