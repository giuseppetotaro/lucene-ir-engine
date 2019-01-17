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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;



public class TestSearcher {
	private static Logger logger = LogManager.getLogger("TestSearcher");
	
	private final static String OPT_INDEX = "index";
	private final static String OPT_QUERY = "seed";
	
	private static String FIELD_PATH = "fs_pathname";
	private static String FIELD_FILENAME = "fs_filename";
	private static String FIELD_IMAGE_ID = "fs_image_uuid";
	
	private static HashMap<Integer, String> imagesMap;
	
    private static void usage() {
    	System.out.print("Usage:\n\t");
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(TestSearcher.class.getCanonicalName());
    	sb.append(" -").append(OPT_INDEX).append(" [CASE_PATH]");
    	sb.append(" -").append(OPT_QUERY).append(" [QUERY]\n");
    	//String usage = "Usage:\tjava -cp CLASSPATH " + TestSearcher.class.getCanonicalName()
	    //    + " -index INDEX_DIR [INDEX_DIR [INDEX_DIR [...]]] -seed STRING\n" 
		//+ "NOTE: Using quotes to include spaces in parameters";
    	System.out.println(sb.toString());
    }

    public static void main(String[] args) throws IOException, ParseException {
    	/** Command line parser and options */
		CommandLineParser parser = new PosixParser();

		Options options = new Options();
		options.addOption(OPT_INDEX, true, "Index path");
		options.addOption(OPT_QUERY, true, "The query");
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			logger.fatal("Error while parsing command line arguments");
			System.exit(1);
		}
		
		/** Check for mandatory options */
		if (!cmd.hasOption(OPT_INDEX) || !cmd.hasOption(OPT_QUERY)) {
			usage();
			System.exit(0);
		}
		
		/** Read options */
		File casePath = new File(cmd.getOptionValue(OPT_INDEX));
		String query = cmd.getOptionValue(OPT_QUERY);

		/** Check correctness of the path containing an ISODAC case */
		if (!casePath.exists() || !casePath.isDirectory()) {
			logger.fatal("The case directory \"" + casePath.getAbsolutePath() + "\" is not valid");
			System.exit(1);
		}
		
		/** Load all the directories containing an index */
		ArrayList<String> indexesDirs = new ArrayList<String>();
		for (File f : casePath.listFiles()) {
			logger.info("Analyzing: " + f);
			if (f.isDirectory())
				indexesDirs.add(f.getAbsolutePath());
		}
		logger.info(indexesDirs.size() + " directories found!");
		
		/** Set-up the searcher */
		Searcher searcher = null;
		try {
			String[] array = indexesDirs.toArray(new String[indexesDirs.size()]);
			searcher = new Searcher(array);
		    TopDocs results = searcher.search(query, Integer.MAX_VALUE);
		    
		    ScoreDoc[] hits = results.scoreDocs;
		    long numTotalHits = results.totalHits;
	
		    System.out.println(numTotalHits + " total matching documents");
	
		    for (int i = 0; i < numTotalHits; i++) {
				Document doc = searcher.doc(hits[i].doc);
				
				String path = doc.get(FIELD_PATH);
				String filename = doc.get(FIELD_FILENAME);
				String image_uuid = doc.get(FIELD_IMAGE_ID);
				
				if (path != null) {
				    System.out.println((i + 1) + ". " + path + File.separator + filename + " - score: " + hits[i].score);
                                    //System.out.println((i + 1) + ". " + path + File.separator + filename + " - image_file: " + imagesMap.get(Integer.parseInt(image_uuid)));
				} else {
				    System.out.println((i + 1) + ". " + "No path for this document");
				}
		    }
		    
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
		    e.printStackTrace();
		} finally {
	    	if (searcher != null) searcher.close();
		}
    }
}
