#!/bin/bash
#
# Script     : indexer.sh
# Usage      : ./indexer.sh -i /path/to/data_dir -o /path/to/index_dir -l /path/to/log_file -p /path/to/jar [-update] [-fork] [-ocr]
# Author     : Giuseppe Totaro
# Date       : 09-01-2015 [MM-DD-YYYY]
# Last Edited: 09-01-2015, Giuseppe Totaro
# Description: This script launches a Java class that leverages Apache Tika and
#              Apache Lucene to extract text from heterogeneous files and build
#              an inverted index. 
# Notes      : 
#

function usage() {
        echo "Usage: ./indexer.sh -i /path/to/data_dir -o /path/to/index_dir -l /path/to/log_file -p /path/to/jar [-update] [-fork] [-ocr]"
        exit 1
}

if [ $# -lt 6 ]
then
	usage
fi

DATA_DIR=""
INDEX_DIR=""
LOG_FILE=""
UPDATE=""
FORK=""
OCR=""
IR_PATH="../../../target"

while [ "$1" != ""  ]   
do
	case $1 in
		-i|--input)
                DATA_DIR="$2"
                shift
                ;;  
                -o|--output)
                INDEX_DIR="$2"
                shift
                ;;  
                -l|--log)
                LOG_FILE=$2
                shift
                ;;  
                -p|--path)
                IR_PATH=$2
                shift
                ;;  
                -u|--update)
                UPDATE="-update"
                ;;  
                -f|--fork)
                FORK="-fork"
                ;;  
                -ocr|--ocr-tesseract)
                OCR="-ocr"
                ;;  
                *)  
                usage
                ;;  
        esac
        shift
done

if [ "$DATA_DIR" == "" ] || [ "$INDEX_DIR" == "" ] || [ "$LOG_FILE" == "" ]
then
        usage
fi

java -Dlogfile.name=${LOG_FILE} -cp ${IR_PATH}/lucene-ir-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar iac.cnr.it.TestIndexer -dir "${DATA_DIR}" -index "${INDEX_DIR}" $UPDATE $FORK $OCR #2>/dev/null
