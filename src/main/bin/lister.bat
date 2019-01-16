@echo off

if [%3]==[] (
	echo %0 C:\path\to\index_dir C:\path\to\output_file C:\path\to\log_file
	echo.
	echo 	Example: %0 ..\..\tmp\index ..\..\output\keywords.txt ..\..\tmp\lister.log
	exit /b 1
)

set INDEX_DIR=%2
set OUTPUT_FILE=%2
set LOG_FILE=%3

set IR_PATH="..\..\..\target"

java -Dlogfile.name=%LOG_FILE% -cp %IR_PATH%/lucene-ir-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar iac.cnr.it.TestLister -index %INDEX_DIR% -output %OUTPUT_FILE%
