#!/bin/bash
#
# Script     : searcher.sh
# Usage      : ./searcher.sh -i /path/to/index_dir -s seed
# Author     : Giuseppe Totaro
# Date       : 09-01-2015 [MM-DD-YYYY]
# Last Edited: 09-01-2015, Giuseppe Totaro
# Description: This scripts lauches a Java class that leverages Apache Lucene 
#              to submit a query against one or multiple indexes.
# Notes      : The package lucene-backward-codecs-5.3.0.jar is necessary to 
#              provide support for older versions.
#

function usage() {
        echo "Usage: ./searcher.sh -i /path/to/index_dir -s seed"
        exit 1
}

if [ $# -lt 4 ]
then
	usage
fi

INDEX_DIR=""
SEED=""
IR_PATH="../../../target"
LUCENE_BACKWARD="../../../lib/lucene-backward-codecs-5.3.0.jar"

while [ "$1" != ""  ]   
do
	case $1 in
		-i|--index)
                INDEX_DIR="$2"
                shift
                ;;  
                -s|--seed)
                SEED="$2"
                shift
                ;;  
                *)  
                usage
                ;;  
        esac
        shift
done

if [ "$INDEX_DIR" == "" ] || [ "$SEED" == "" ]
then
        usage
fi

java -Xmx2g -cp ${LUCENE_BACKWARD}:${IR_PATH}/lucene-ir-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar iac.cnr.it.TestSearcher -index "$INDEX_DIR" -seed $SEED
