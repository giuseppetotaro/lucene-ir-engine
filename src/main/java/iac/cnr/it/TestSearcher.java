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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TestSearcher {
    private static void usage() {
	String usage = "Usage:\tjava -cp CLASSPATH " + TestSearcher.class.getCanonicalName()
	        + " -index INDEX_DIR [INDEX_DIR [INDEX_DIR [...]]] -seed STRING\n" 
		+ "NOTE: Using quotes to include spaces in parameters";
	System.err.println(usage);
	System.exit(1);
    }

    public static void main(String[] args) throws IOException, ParseException {
	ArrayList<String> subIndexesList = new ArrayList<String>();
	String seed = null;

	if (args.length < 4) {
	    usage();
	}
	for (int i = 0; i < args.length; i++) {
	    if ("-index".equals(args[i])) {
		while ((++i < args.length) && !("-seed".equals(args[i]))) {
		    File indexDir = new File(args[i]);
		    File directories[] = indexDir.listFiles(new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		            return new File(dir, name).isDirectory();
		        }
		    });
		    if (directories.length > 0) {
			String directoryPaths[] = new String[directories.length];
			for (int j = 0; j < directories.length; j++) {
			    directoryPaths[j] = directories[j].getCanonicalPath();
			}
			subIndexesList.addAll(Arrays.asList(directoryPaths));
		    }
		    else {
			subIndexesList.add(args[i]);
		    }
		}
		i--;
	    } else if ("-seed".equals(args[i])) {
		seed = args[++i];
	    } else {
		usage();
	    }
	}

	Searcher searcher = null;
	try {
	    searcher = new Searcher(subIndexesList.toArray(new String[subIndexesList.size()]));
	    
	    TopDocs results = searcher.search(seed, Integer.MAX_VALUE);

	    ScoreDoc[] hits = results.scoreDocs;
	    int numTotalHits = results.totalHits;

	    System.out.println(numTotalHits + " total matching documents");

	    for (int i = 0; i < numTotalHits; i++) {
		Document doc = searcher.doc(hits[i].doc);
		
		String path = doc.get("fs_pathname");
		String filename = doc.get("fs_filename");
		if (path != null) {
		    System.out.println((i + 1) + ". " + path + File.separator + filename + " - score: " + hits[i].score);
		} else {
		    System.out.println((i + 1) + ". " + "No path for this document");
		}
	    }

	} catch (Exception e) {
	    System.err.println("An error occurred: " + e.getMessage());
	    e.printStackTrace();
	} finally {
	    searcher.close();
	}
    }
}