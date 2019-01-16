package iac.cnr.it;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

public class Lister {
	private MultiReader multiReader;
	
	public Lister(String[] subIndexes) throws IOException {
		IndexReader[] subReaders = new IndexReader[subIndexes.length];
		for (int i = 0; i<subIndexes.length; i++) {
		    subReaders[i] = DirectoryReader.open(FSDirectory.open(Paths.get(subIndexes[i])));
		}
		
		multiReader = new MultiReader(subReaders, true);
	}
	
	public void close() {
		try {
		    multiReader.close();
		} catch (IOException ioe) {
		    System.err.println("Can't close IndexReader...");
		}
	}
	
	public String[] list() throws IOException {
		ArrayList<String> termsList = new ArrayList<String>();
		
		LuceneDictionary ld = new LuceneDictionary(multiReader, "contents" );
		BytesRefIterator iterator = ld.getEntryIterator();
		BytesRef byteRef = null;

		while ( ( byteRef = iterator.next() ) != null )
		{
		    String term = byteRef.utf8ToString();
		    termsList.add(term);
		}
		
		String[] terms = new String[termsList.size()];
		return termsList.toArray(terms);
	}
}
