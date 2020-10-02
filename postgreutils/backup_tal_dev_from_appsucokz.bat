ECHO OFF
SET USERNAME=tal
SET PGPASSWORD=tal
SET DATABASE=tsadv_dev
SET HOST=apps.uco.kz
SET PORT=5432
SET PATH=%PATH%;C:\Program Files\PostgreSQL\9.5\bin

IF EXIST "%DATABASE%.bin" (
ECHO Removing old %DATABASE%.bin...
rm %DATABASE%.bin
) ELSE (
ECHO Old %DATABASE%.bin not exist.
)
ECHO Dumping %DATABASE% from %HOST%...
pg_dump --username=%USERNAME% --format=c --file=%DATABASE%.bin --host=%HOST% --port=%PORT% %DATABASE%
ECHO Dumping %DATABASE% from %HOST% successfuly completed!
pause