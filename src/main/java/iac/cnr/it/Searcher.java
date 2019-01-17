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
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    private MultiReader   multiReader;
    private IndexSearcher searcher;
    private QueryParser   parser;
    private String        field;

    public Searcher(String[] subIndexes) throws IOException {
	
	IndexReader[] subReaders = new IndexReader[subIndexes.length];
	for (int i = 0; i<subIndexes.length; i++) {
	    subReaders[i] = DirectoryReader.open(FSDirectory.open(Paths.get(subIndexes[i])));
	}
	
	multiReader = new MultiReader(subReaders, true);
	
	searcher = new IndexSearcher(multiReader);
	field = "contents";

	Analyzer analyzer = new StandardAnalyzer();

	parser = new QueryParser(field, analyzer);
    }

    public void close() {
	try {
	    multiReader.close();
	} catch (IOException ioe) {
	    System.err.println("Can't close IndexReader...");
	}
    }

    public TopDocs search(String line, int n) throws IOException, ParseException {
	Query query = parser.parse(line);
	System.out.println("Searching for: " + query.toString(field));

	TopDocs results = searcher.search(query, n);

	return results;
    }

    public Document doc(int docID) throws IOException {
	return searcher.doc(docID);
    }
}
