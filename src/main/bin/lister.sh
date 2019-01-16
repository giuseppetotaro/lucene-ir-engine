#!/bin/bash
#
# Script     : lister.sh
# Usage      : ./lister.sh -i /path/to/index -o /path/to/output_file -l /path/to/log_file -p /path/to/jar
# Author     : Giuseppe Totaro
# Date       : 01-16-2019 [MM-DD-YYYY]
# Last Edited: 09-16-2019, Giuseppe Totaro
# Description: This script launches a Java class that leverages Apache Lucene to 
#              extract the keywords in the given index.
# Notes      : 
#

function usage() {
        echo "Usage: ./lister.sh -i /path/to/index -o /path/to/output_file -l /path/to/log_file -p /path/to/jar"
        exit 1
}

if [ $# -lt 6 ]
then
	usage
fi

INDEX_DIR=""
OUTPUT_FILE=""
LOG_FILE=""
IR_PATH="../../../target"

while [ "$1" != ""  ]   
do
	case $1 in
		-i|--input)
                INDEX_DIR="$2"
                shift
                ;;  
                -o|--output)
                OUTPUT_FILE="$2"
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
                *)  
                usage
                ;;  
        esac
        shift
done

if [ "$INDEX_DIR" == "" ] || [ "$OUTPUT_FILE" == "" ] || [ "$LOG_FILE" == "" ]
then
        usage
fi

java -Dlogfile.name=${LOG_FILE} -cp ${IR_PATH}/lucene-ir-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar iac.cnr.it.TestLister -index "${INDEX_DIR}" -output "${OUTPUT_FILE}" #2>/dev/null
