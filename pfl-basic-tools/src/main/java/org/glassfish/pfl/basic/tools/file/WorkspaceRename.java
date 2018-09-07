/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.tools.file ;

import java.util.List ;
import java.util.ArrayList ;

import java.io.File ;
import java.io.IOException ;
import org.glassfish.pfl.basic.tools.argparser.ArgParser;
import org.glassfish.pfl.basic.tools.argparser.DefaultValue;
import org.glassfish.pfl.basic.tools.argparser.Help;

public class WorkspaceRename {
    private static final String[] SUBSTITUTE_SUFFIXES = {
	"c", "h", "java", "sjava", "idl", "htm", "html", "xml", "dtd",
	"tdesc", "policy", "secure", "vthought", "jmk",
	"ksh", "sh", "classlist", "config", "properties", "prp", 
	"set", "settings", "data", "txt", "text", "javaref", "idlref" } ;

    private static final String[] SUBSTITUTE_NAMES = {
	"Makefile.corba", "Makefile.example", "ExampleMakefile", "Makefile",
        "manifest", "README", "README.SUN", "COPYING", "COPYRIGHT",
        "ChangeLog"
    } ;

    private static final String[] COPY_SUFFIXES = {
	"sxc", "sxi", "sxw", "odp", "odt", "gif", "png", "jar", "zip", "jpg", "pom",
	"pdf", "doc", "mif", "fm", "book", "zargo", "zuml", "cvsignore", 
	"hgignore", "list", "old", "orig", "rej", "hgtags", "xsl", "bat", "css",
        "icns", "bin", "ico", "init", "ss", "pp", "el", "mail", "lisp", "sch",
        "tst", "xcf", "war"
    } ;

    private static final String[] IGNORE_SUFFIXES = {
	"swm", "swn", "swo", "swp", "class", "o", "gz"
    } ;

    private static final String[] IGNORE_NAMES = {
	"NORENAME", "errorfile", "sed_pattern_file.version", "package-list",
        ".hgtags"
    } ;

    private static final String[] IGNORE_DIRS = {
	".hg", ".snprj", ".cvs", "SCCS", "obj", "obj_g", "Codemgr_wsdata", 
	"deleted_files", "build", "rename", "freezepoint", "test-output",
	"webrev", "javadoc", "felix-cache", "vpproject"
    } ;

    public static void main(String[] strs) {
	(new WorkspaceRename( strs )).run() ;
    }

    private interface Arguments {
	@Help( "Set to >0 to get information about actions taken for every file."
	    + "  Larger values give more detail." )
	@DefaultValue( "0" ) 
	int verbose() ;

	@Help( "Set to true to avoid modifying any files" ) 
	@DefaultValue( "false" ) 
	boolean dryrun() ;

	@Help( "Source directory for rename" ) 
	@DefaultValue( "" ) 
	File source() ;

	@Help( "Destination directory for rename" ) 
	@DefaultValue( "" ) 
	File destination() ;

	@Help( "The renamed package" ) 
	@DefaultValue( "ee" ) 
	String version() ;

	@Help( "If true, copy all files without renaming anything" )
	@DefaultValue( "false" )
	boolean copyonly() ;

        @Help( "If true, expand all tabs into spaces on files that are renamed"
	    + " (all text file)" )
        @DefaultValue ( "true" ) 
        boolean expandtabs() ;

        @Help( "List of patterns given as <source java package name>:<renamed java package name>."
            + " Also handes the /-separated version of the pattern.  If the string VERSION occurs" 
            + " in the renamed java package name, it will be replaced with the value of the"
            + " version() argument." ) 
        @DefaultValue( "" )
        List<ArgParser.StringPair> patterns() ;
    }

    // Extract these from args so that the methods in Arguments are not
    // repeatedly evaluated during run().  
    private final int verbose ;
    private final boolean dryrun ;
    private final File source ;
    private final File destination ;
    private final String version ;
    private final boolean copyonly ;
    private final boolean expandtabs ;
    private final List<ArgParser.StringPair> patterns ;
    private final List<String> noActionFileNames = new ArrayList<String>() ;

    private void trace( String msg ) {
	System.out.println( msg ) ;
    }

    // Get the FileWrapper representing the destination for the renamed or
    // copied source.  Note that the relative file name must also be renamed!
    private FileWrapper makeTargetFileWrapper( FileWrapper arg ) {
	String rootName = source.getAbsolutePath() ;
	String sourceName = arg.getAbsoluteName() ;
	if (verbose > 1) {
	    trace( "makeTargetFileWrapper: rootName = " + rootName ) ;
	    trace( "makeTargetFileWrapper: sourceName = " + sourceName ) ;
	}

	if (sourceName.startsWith( rootName)) {
            String targetName = sourceName.substring( 
                rootName.length() ) ;

            for (ArgParser.StringPair astr : patterns) {
                final String key = astr.first() ;
                final String replacement = astr.second() ;

                if (sourceName.indexOf( key ) >= 0) {
                    targetName = targetName.replace( key, replacement ) ;
                }
            }

	    File result = new File( destination, targetName ) ;
	    File resultDir = result.getParentFile() ;
	    resultDir.mkdirs() ;
	    FileWrapper fwres = new FileWrapper( result ) ;
	    if (verbose > 1) {
		trace( "makeTargetFileWrapper: arg = " + arg ) ;
		trace( "makeTargetFileWrapper: fwres = " + fwres ) ;
	    }
	    return fwres ;
	} else {
	    throw new RuntimeException( "makeTargetFileWrapper: arg file " 
		+ sourceName + " does not start with root name " 
		+ rootName ) ;
	}
    }

    public WorkspaceRename(String[] strs) {
	ArgParser ap = new ArgParser( Arguments.class ) ;
	Arguments args = ap.parse( strs, Arguments.class ) ;
	version = args.version() ;
	source = args.source() ;
	destination = args.destination() ;
	verbose = args.verbose() ;
	dryrun = args.dryrun() ;
	copyonly = args.copyonly() ;

        patterns = new ArrayList<ArgParser.StringPair>() ;
        for (ArgParser.StringPair sp : args.patterns()) {
            final String key = sp.first() ;
            final String value = sp.second().replace( "VERSION", version ) ;
            final ArgParser.StringPair newSp =
                new ArgParser.StringPair( key, value ) ;
            patterns.add( newSp ) ;

            final String slashKey = key.replace( ".", "/" ) ;
            final String slashValue = value.replace( ".", "/" ) ;
            final ArgParser.StringPair slashSp =
                new ArgParser.StringPair( slashKey, slashValue ) ;
            patterns.add( slashSp ) ;
        }

        expandtabs = args.expandtabs() ;

	if (verbose > 1) {
	    trace( "Main: args:\n" + args ) ;
            trace( "Main: patterns: " + patterns ) ;
	}
    }

    private void run() {
	try {
	    final byte[] copyBuffer = new byte[ 256*1024 ] ;

	    final Scanner.Action copyAction = new Scanner.Action() {
		@Override
		public String toString() {
		    return "copyAction" ;
		}

		@Override
		public boolean evaluate( FileWrapper fw ) {
		    try {
			FileWrapper target = makeTargetFileWrapper( fw ) ;
			if (target.isYoungerThan( fw )) {
			    if (verbose > 0) {
				trace( "copyAction: copying " + fw
				    + " to " + target ) ;
			    }
			    fw.copyTo( target, copyBuffer ) ;
			} else {
			    if (verbose > 1) {
				trace( "copyAction: NOT copying " + fw
				    + " to " + target ) ;
			    }
			}
			return true ;
		    } catch (IOException exc ) {
			System.out.println( "Exception while processing file " 
                            + fw + ": " + exc ) ;
			exc.printStackTrace() ;
			return false ;
		    } 
		}
	    } ;

	    final Scanner.Action renameAction = new Scanner.Action() {
		@Override
		public String toString() {
		    return "renameAction" ;
		}

		@Override
		public boolean evaluate( FileWrapper fw ) {
		    FileWrapper target = makeTargetFileWrapper( fw ) ;

		    try {
			if (target.isYoungerThan( fw )) {
			    if (verbose > 0) {
				trace( "renameAction: renaming " + fw
				    + " to " + target ) ;
			    }

			    Block sourceBlock = BlockParser.getBlock( fw ) ;
			    Block targetBlock = sourceBlock.substitute( 
                                patterns ) ;

                            if (expandtabs) {
                                targetBlock = targetBlock.expandTabs() ;
                            }

			    target.delete() ;
			    target.open( FileWrapper.OpenMode.WRITE ) ;
			    targetBlock.write( target ) ;
			} else {
			    if (verbose > 1) {
				trace( "renameAction: NOT renaming " + fw
				    + " to " + target ) ;
			    }
			}

			return true ;
		    } catch (IOException exc ) {
			System.out.println( "Exception while processing file " + fw 
			    + ": " + exc ) ;
			exc.printStackTrace() ;
			return false ;
		    } finally {
			target.close() ;
		    }
		}
	    } ;

	    final ActionFactory af = new ActionFactory( verbose, dryrun ) ;

	    // Create the actions we need
	    final Recognizer recognizer = af.getRecognizerAction() ; 

            recognizer.setDefaultAction(
                new Scanner.Action() {
		    @Override
                    public String toString() { 
			return "WorkspaceRename default action" ;
		    }

		    @Override
                    public boolean evaluate( FileWrapper fw ) {
                        // Ignore irritating tmp* files that show up in Hudson
			// job workspaces for some unknown reason.
                        if (!fw.getName().startsWith( "tmp" )) {
                            noActionFileNames.add( fw.getAbsoluteName() ) ;
                        }
                        return true ;
                    }
                } ) ;

	    final Scanner.Action skipAction = af.getSkipAction() ;

	    final Scanner.Action action = copyonly ?
		copyAction : renameAction ;

	    for (String str : SUBSTITUTE_SUFFIXES) {
		recognizer.addKnownSuffix( str, action ) ;
	    }

	    for (String str : SUBSTITUTE_NAMES) {
		recognizer.addKnownName( str, action ) ;
	    }
	    recognizer.setShellScriptAction( action ) ;

	    for (String str : COPY_SUFFIXES) {
		recognizer.addKnownSuffix( str, copyAction ) ;
	    }

	    for (String str : IGNORE_SUFFIXES) {
		recognizer.addKnownSuffix( str, skipAction ) ;
	    }

	    for (String str : IGNORE_NAMES) {
		recognizer.addKnownName( str, skipAction ) ;
	    }

	    if (verbose > 1) {
		trace( "Main: contents of recognizer:" ) ;
		recognizer.dump() ;
	    }

	    final Scanner scanner = new Scanner( verbose, source ) ;
	    for (String str : IGNORE_DIRS )
		scanner.addDirectoryToSkip( str ) ;

	    scanner.scan( recognizer ) ;

            int rc = noActionFileNames.size() ;

            if (rc > 0) {
                System.out.println( "Rename FAILED: no action defined for files:" ) ;
                for (String str : noActionFileNames) {
                    System.out.println( "\t" + str ) ;
                }
                System.exit( rc ) ;
            }
	} catch (IOException exc) {
	    System.out.println( "Exception while processing: " + exc ) ;
	    exc.printStackTrace() ;
	}
    }
}
