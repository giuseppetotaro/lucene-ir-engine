/*
 * Copyright 2015 Giuseppe Totaro
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package iac.cnr.it;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestIndexer {
    private static Logger logger = LogManager.getLogger("TestIndexer");

    private static void usage() {
	String usage = "Usage: " + TestIndexer.class.getName()
	        + " -dir DATA_DIR -index INDEX_DIR -log LOG_FILE [-update] [-fork] [-ocr]\n\n";
	System.err.println(usage);
	logger.fatal("Error in passing the command line parameters.");
	System.exit(1);
    }

    public static void main(String[] args) throws IOException {
	
	String dataDir = null;
	String indexDir = null;
	boolean create = true;
	boolean fork = false;
	boolean ocr = false;

	for (int i = 0; i < args.length; i++) {
	    if ("-dir".equalsIgnoreCase(args[i])) {
		dataDir = args[++i];
	    } else if ("-index".equalsIgnoreCase(args[i])) {
		indexDir = args[++i];
	    } else if ("-update".equals(args[i])) {
		create = false;
	    } else if ("-fork".equalsIgnoreCase(args[i])) {
		fork = true;
	    } else if ("-ocr".equalsIgnoreCase(args[i])) {
		ocr = true;
	    } else {
		usage();
	    }
	}
	if ((null == dataDir) || (null == indexDir)) {
	    usage();
	}
	
	Indexer indexer = null;

	long start = System.nanoTime();

	try {
	    indexer = new Indexer(indexDir, create, fork, ocr);

	    System.out.println("Indexing " + dataDir + "...");
	    indexer.index(dataDir);

	} catch (IOException ioe) {
	    logger.fatal("Fatal error during indexing: " + ioe.getMessage());
	} finally {
	    indexer.close();
	}

	long end = System.nanoTime();

	logger.info("Indexing completed in " + (double) (end - start) / 1000000000 + " seconds.");
	logger.info("Files processed: " + indexer.getFiles());
	logger.info("Errors: " + indexer.getErrors());
	
	System.out.println("Indexing completed in " + (double) (end - start) / 1000000000 + " seconds.");
	System.out.println("Files processed: " + indexer.getFiles());
	System.out.println("Errors: " + indexer.getErrors());
    }
}