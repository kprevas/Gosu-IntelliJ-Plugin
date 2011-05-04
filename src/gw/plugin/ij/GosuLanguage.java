package gw.plugin.ij;

import com.intellij.lang.InjectableLanguage;
import com.intellij.lang.Language;

/**
 *
 * Copyright 2010 Guidewire Software, Inc.
 */
public class GosuLanguage extends Language implements InjectableLanguage
{
  private static final GosuLanguage INSTANCE = new GosuLanguage();

  public static GosuLanguage instance()
  {
    return INSTANCE;
  }

  private GosuLanguage()
  {
    super( "Gosu", "text/gosu", "text/x-gosu" );
  }

//  @Override
//  public Language getBaseLanguage()
//  {
//    //## todo: remove this method when we do our own code completion
//    // Trick intellij into thinking it can do code completion on Gosu code (see completion contributers in plugin.xml)
//    return JavaFileType.INSTANCE.getLanguage();
//  }
//
  @Override
  public String getDisplayName()
  {
    return "Gosu";
  }
}
