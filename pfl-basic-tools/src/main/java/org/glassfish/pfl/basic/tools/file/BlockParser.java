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

import org.glassfish.pfl.basic.func.BinaryFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Utility class used to convert FileWrappers into (lists of) Blocks.
 */
public class BlockParser {
    private BlockParser() {}

    /** Return the contents of the text file as a Block.
     */
    public static Block getBlock( final FileWrapper fw ) throws IOException {
	fw.open( FileWrapper.OpenMode.READ ) ;

	try {
	    final List<String> data = new ArrayList<String>() ;

	    String line = fw.readLine() ;
	    while (line != null) {
		data.add( line ) ;
		line = fw.readLine() ;
	    }
	    
	    return new Block( data ) ;
	} finally {
	    fw.close() ;
	}
    }

    public static final String COMMENT_BLOCK_TAG = "CommentBlock" ;

    /** Transform fw into a list of blocks.  There are two types of blocks in this
     * list, and they always alternate:
     * <ul>
     * <li>Blocks in which every line starts with prefix, 
     * Such blocks are given the tag COMMENT_BLOCK_TAG.  
     * <li>Blocks in which no line starts with prefix.
     * Such blocks are not tagged.
     * </ul>
     */
    public static List<Block> parseBlocks( final FileWrapper fw, 
	final String prefix ) throws IOException {

	boolean inComment = false ;
	final List<Block> result = new ArrayList<Block>() ;
	fw.open( FileWrapper.OpenMode.READ ) ;

	try {
	    List<String> data = new ArrayList<String>() ;

	    BinaryFunction<List<String>,String,List<String>> newBlock = 
		new BinaryFunction<List<String>,String,List<String>>() {
                    @Override
		    public List<String> evaluate( List<String> data, 
                        String tag ) {

			if (data.isEmpty()) {
                            return data;
                        }

			final Block bl = new Block( data ) ;
			if (tag != null) {
                            bl.addTag(tag);
                        }
			result.add( bl ) ;
			return new ArrayList<String>() ;
		    }
		} ;

	    String line = fw.readLine() ;
	    while (line != null) {
		if (inComment) {
		    if (!line.startsWith( prefix )) {
			inComment = false ;
			data = newBlock.evaluate( data, COMMENT_BLOCK_TAG ) ;
		    }
		} else {
		    if (line.startsWith( prefix )) {
			inComment = true ;
			data = newBlock.evaluate( data, null ) ;
		    }
		}
		data.add( line ) ;

		line = fw.readLine() ;
	    }

	    // Create last block!
	    Block bl = new Block( data ) ;
	    if (inComment) {
                bl.addTag(COMMENT_BLOCK_TAG);
            }
	    result.add( bl ) ;
	    
	    return result ;
	} finally {
	    fw.close() ;
	}
    }

    /** Transform fw into a list of blocks.  There are two types of blocks in this
     * list, and they always alternate:
     * <ul>
     * <li>Blocks that start with a String containing start, 
     * and end with a String containing end.  Such blocks are given the
     * tag COMMENT_BLOCK_TAG.  
     * <li>Blocks that do not contain start or end anywhere
     * </ul>
     */
    public static List<Block> parseBlocks( final FileWrapper fw, 
	final String start, final String end ) throws IOException {

	boolean inComment = false ;
	final List<Block> result = new ArrayList<Block>() ;
	fw.open( FileWrapper.OpenMode.READ ) ;

	try {
	    List<String> data = new ArrayList<String>() ;

	    BinaryFunction<List<String>,String,List<String>> newBlock = 
		new BinaryFunction<List<String>,String,List<String>>() {
                    @Override
		    public List<String> evaluate( List<String> data, String tag ) {
			if (data.isEmpty()) {
                            return data;
                        }

			final Block bl = new Block( data ) ;
			if (tag != null) {
                            bl.addTag(tag);
                        }
			result.add( bl ) ;
			return new ArrayList<String>() ;
		    }
		} ;

	    String line = fw.readLine() ;
	    while (line != null) {
		if (inComment) {
		    data.add( line ) ;

		    if (line.contains( end )) {
			inComment = false ;
			data = newBlock.evaluate( data, COMMENT_BLOCK_TAG ) ;
		    }
		} else {
		    if (line.contains( start )) {
			inComment = true ;
			data = newBlock.evaluate( data, null ) ;
		    }

		    data.add( line ) ;

		    if (line.contains( end)) {
			// Comment was a single line!
			inComment = false ;
			data = newBlock.evaluate( data, COMMENT_BLOCK_TAG ) ;
		    }
		}

		line = fw.readLine() ;
	    }

	    // Create last block!
	    Block bl = new Block( data ) ;
	    if (inComment) {
                bl.addTag(COMMENT_BLOCK_TAG);
            }
	    result.add( bl ) ;
	    
	    return result ;
	} finally {
	    fw.close() ;
	}
    }
}
