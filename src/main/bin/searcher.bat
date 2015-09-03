@echo off

if [%2]==[] (
	echo %0 C:\path\to\index_dir seed C:\path\to\lucene-backward-codecs-5.3.0.jar
	echo.
	echo 	Example: %0 ..\..\tmp\index 10
	exit /b 1
)

set INDEX_DIR=%1
set SEED=%2
set IR_PATH="..\..\..\target"
set LUCENE_BACKWARD="..\..\..\lib\lucene-backward-codecs-5.3.0.jar"

java -cp %LUCENE_BACKWARD%;%IR_PATH% iac.cnr.it.TestSearcher -index %INDEX_DIR% -seed %SEED% || echo ERROR: java command not found!
