uses gw.util.Shell
uses java.lang.StringBuilder
uses java.io.File
uses java.lang.System

var gwVark = file( "/aardvark/alpha/bin/vark.cmd") 

var libDir = file( "lib" )

function updateGosu() {
  var gwVarkFile = file( "../../ant/build.vark") 
  
  Shell.buildProcess( "${gwVark.AbsolutePath} -f ${gwVarkFile.AbsolutePath} build-gosu-with-webservices" )
       .withStdErrHandler( \ s -> print( s ) )
       .withStdOutHandler( \ s -> print( s ) )
       .exec()

  var gosuBuildDir = file( "../../../build/gosu/") 
       
  Ant.copy(
          :filesetList = { gosuBuildDir.file("jars").fileset(), 
                           gosuBuildDir.file("ext").fileset() },
          :todir = libDir,
          :includeemptydirs = false)
}

function updateHeaders() {
  var header = file( "etc/COPY.txt" ).read().chomp()
  file( "src" ).eachFileInTree( \ f -> {
    if( f.Extension == "java" ) {
      var source = f.read()
      var ls = SystemProperties.line.separator
      if(not source.containsIgnoreCase( "copyright" ) ) {
        Shell.exec( "p4 edit ${f.AbsolutePath}" )
        var added = false
        var foundComment = false
        var lines = source.split( SystemProperties.line.separator )
        var sb = new StringBuilder()
        for( l in lines index i) {
          if( not added ) {
            if( not foundComment and l.contains( "class" ) ) {
              sb.append( "/**${ls} * " + header + "${ls} */${ls}" )
              added = true
            }
            if( l.contains( "/*" ) and not l.contains( "*/" ) ) {
              foundComment = true
            }
            if( foundComment and l.contains( "*/" ) ) {
              sb.append( " *${ls} * " + header + ls )
              added = true
            }
          }
          sb.append( l )
          if( i != lines.Count - 1 ) {
            sb.append( ls )
          }
        }
        if( source.endsWith( ls ) ) {
            sb.append( ls )
        }
        f.write( sb.toString() )
      }
    }
  })
}