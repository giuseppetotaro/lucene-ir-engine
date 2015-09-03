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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.fork.ForkParser;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Indexer {

    private IndexWriter   writer;

    private int           numErrors;

    private int           numFiles;

    private Parser        parser;

    private ParseContext  context;
    
    private boolean fork;

    /**
     * Logger object used to log messages.
     */
    private static Logger logger = LogManager.getLogger("Indexer");

    public Indexer(String indexDir, boolean create, boolean fork, boolean ocr) throws IOException {
	logger.entry();
	
	this.fork = fork;

	numErrors = 0;
	numFiles = 0;

	Directory dir = FSDirectory.open(Paths.get(indexDir));
	Analyzer analyzer = new StandardAnalyzer();
	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

	if (create) {
	    iwc.setOpenMode(OpenMode.CREATE);
	    logger.info("Configuration specified to create a new index or overwrites an existing one.");
	} else {
	    iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    logger.info("Configuration specified to create a new index if one does not exist, otherwise the index will be opened and documents will be appended.");
	}

	writer = new IndexWriter(dir, iwc);

	Parser autoDetectParser = new AutoDetectParser();
	context = new ParseContext();
	
	if (ocr) {
	    TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
	    PDFParserConfig pdfConfig = new PDFParserConfig();
	    pdfConfig.setExtractInlineImages(true);
	    pdfConfig.setExtractUniqueInlineImagesOnly(false);
	    context.set(Parser.class, autoDetectParser);
	    context.set(TesseractOCRConfig.class, ocrConfig);
	    context.set(PDFParserConfig.class, pdfConfig);
	}

	if (fork) {
	    parser = new ForkParser(ForkParser.class.getClassLoader(), autoDetectParser);
	} else {
	    parser = autoDetectParser;
	}

	logger.exit();
    }

    public void close() {
	try {
	    writer.close();
	} catch (IOException ioe) {
	    logger.catching(ioe);
	} finally {
	    if (this.fork) {
		((ForkParser)parser).close();
	    }
	}
    }

    public int index(String dataDir) throws IOException {
	int numFiles = 0;

	File dir = new File(dataDir);

	if (!dir.isDirectory() || !dir.canRead()) {
	    IOException ioe = new IOException(dataDir + " cannot be read or is not a directory.");
	    throw logger.throwing(ioe);
	} else {
	    File[] files = dir.listFiles();

	    for (File file : files) {
		indexFile(file);
	    }
	}

	return numFiles;
    }

    public void indexFile(File file) {
	if (file.canRead()) {
	    if (file.isDirectory()) {
		String[] files = file.list();
		if (files != null) {
		    for (int i = 0; i < files.length; i++) {
			indexFile(new File(file, files[i]));
		    }
		}
	    } else {
		try {
		    numFiles++;

		    // System.out.println(file.getName());

		    Document doc = getDocument(file);
		    if (doc == null)
			throw new IOException("Error during building index document...");

		    writer.addDocument(doc);

		} catch (IOException ioe) {
		    logger.error("Error during adding document " + file.getName() + " to the index: "
			    + ioe.getMessage());
		}
	    }
	}
    }

    public int getErrors() {
	return numErrors;
    }

    public int getFiles() {
	return numFiles;
    }

    private Document getDocument(File file) {
	Metadata metadata = new Metadata();

	InputStream inputStream = null;
	Document doc = null;

	try {
	    inputStream = TikaInputStream.get(file);

	    ContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
	    parser.parse(inputStream, handler, metadata, context);

	    doc = buildDocument(file, handler.toString(), metadata);
	} catch (Exception e) {
	    logger.error("Parsing exception on " + file.getName() + " - " + e.getClass().getName() + ": "
		    + e.getMessage());
	    numErrors++;
	} finally {
	    try {
		inputStream.close();
	    } catch (IOException ioe) {
		logger.catching(ioe);
	    }
	}

	return doc;
    }

    private Document buildDocument(File file, String text, Metadata metadata) {
	Document doc = new Document();
	doc.add(new TextField("contents", text, Field.Store.NO));

	for (String name : metadata.names()) {
	    String value = metadata.get(name);
	    doc.add(new StringField(name, value, Field.Store.YES));
	}

	doc.add(new StringField("fs_pathname", file.getPath(), Field.Store.YES));
	doc.add(new StringField("fs_filename", file.getName(), Field.Store.YES));

	return doc;
    }
}