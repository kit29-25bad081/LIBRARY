$ErrorActionPreference = 'Stop'
$msi = 'C:\Temp\mysql-8.4.9-winx64.msi'
$installDir = 'C:\Program Files\MySQL\MySQL Server 8.4'
$log = 'C:\Temp\mysql_install.log'

if (-not (Test-Path $msi)) {
    curl.exe -L -k -o $msi 'https://cdn.mysql.com/Downloads/MySQL-8.4/mysql-8.4.9-winx64.msi'
}

& msiexec.exe /i $msi /L*v $log /qn INSTALLDIR=$installDir
exit $LASTEXITCODE
