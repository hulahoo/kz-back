ECHO OFF
SET USERNAME=tal
SET PGPASSWORD=tal
SET DBNAME=tsadv_dev
SET FILENAME=tsadv_dev.bin
SET HOST=localhost
SET PORT=5432
SET PATH=%PATH%;C:\Program Files\PostgreSQL\9.5\bin

IF EXIST "%FILENAME%" (
ECHO Restoring %FILENAME% to %HOST%...
pg_restore --username=%USERNAME% --host=%HOST% --port=%PORT% --dbname=%DBNAME% --format=c %FILENAME% > %FILENAME%.restore.log
ECHO Restoring %FILENAME% to %HOST% completed!
) ELSE (
ECHO Can't restore because file %FILENAME% not exist. 
)
pause