# lucene-ir-engine
_lucene-ir-engine_ is an extremely simple Java application based on [Apache Tika](http://tika.apache.org/) and [Apache Lucene](http://lucene.apache.org/).
It provides the following features: 

  * Parsing and extraction of metadata and text content from various 
    documents.
  * Indexing of plain text and metadata in order to create an inverted index 
    related to parsed documents.
  * Performing a simple search by term within a previously created inverted 
    index.

To perform the tasks above, ir-engine uses two Java libraries:

  * Apache Tika (1.10) provides Java APIs to detect and extract metadata and data
    from heterogenous file formats using existing parser libraries. 
  * Apache Lucene (5.3.0) is a powerful Java library for indexing and searching 
    of text.

lucene-ir-engine is a [Maven](https://maven.apache.org/) project organized as follows:

  * `lib`:
    This directory includes all the JAR files required at runtime. Currently, it contains only the package lucene-backward-codecs-5.3.0.jar for backwards compatibility.

  * `pom.xml`:
    It is an XML file that contains information about the project and configuration details used by Maven to build the project.
    
  * `src`:
    This directory includes source files. It contains also the shell scripts to easily execute the utilities provided by lucene-ir-engine.
    These scripts are located into `src/main/bin`.

  * `README.txt`:
    This README plain-text file.

## Getting Started

To build the project, you can type the following command:

> mvn clean install

To run the utilities of lucene-ir-engine, you can launch the following scripts (in `/src/main/bin`):

* `indexer.sh` aims at indexing metadata and text extracted from heterogeneous documents:
> ./indexer.sh -i /path/to/data_dir -o /path/to/index_dir -l /path/to/log_file -p /path/to/jar [-update] [-fork] [-ocr]

* `searcher.sh` aims at performing search queries against previously built Lucene indexes:
> ./searcher.sh -i /path/to/index_dir -s seed

Furthermore, the scripts for Microsoft Windows systems are provided into the same directory.

A suitable dataset for testing lucene-ir-engine is *govdocs1* provided by (Digital Corpora](http://digitalcorpora.org/corpora/files)

## Backwards Compatibility

The last release of lucene-ir-engine relies on Apache Lucene 5.3.0.
Lucene 5.x still supports the numerous 4.x index formats, whereas support for 3.x indexes has been removed.
Therefore, lucene-ir-engine is able to perform queries against 4.x indexes if the package `lucene-backward-codecs-5.3.0.jar` is provided in the classpath.
Currently, the script `searcher.sh` requires that package, that is located into the `lib` directory.

## License

[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
