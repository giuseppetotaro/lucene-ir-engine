@echo off

if [%3]==[] (
	echo %0 C:\path\to\data_dir C:\path\to\index_dir C:\path\to\log_file [update] [fork] [ocr]
	echo.
	echo 	Example: %0 ..\..\data\sample ..\..\tmp\index ..\..\tmp\indexer.log
	exit /b 1
)

set DATA_DIR=%1
set INDEX_DIR=%2
set LOG_FILE=%3
set UPDATE=""
set FORK=""
set OCR=""

for %%x in (%*) do (
	if [%%x]==[update] set UPDATE="-update" & echo Trying to update the existing index...
	if [%%x]==[fork] set FORK="-fork" & echo Using fork parser...
	if [%%x]==[ocr] set OCR="-ocr" & echo Using Tesseract OCR, if available...
)

java -Dlogfile.name=%LOG_FILE% -cp %IR_PATH%/lucene-ir-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar iac.cnr.it.TestIndexer -dir %DATA_DIR% -index %INDEX_DIR% %UPDATE% %FORK% %OCR%
