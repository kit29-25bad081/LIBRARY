$client = 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe'
if (Test-Path $client) {
    Write-Host 'CLIENT_FOUND'
} else {
    Write-Host 'CLIENT_MISSING'
}

$service = Get-Service -Name MySQL80 -ErrorAction SilentlyContinue
if ($service) {
    $service | Select-Object Name, Status | Format-List
} else {
    Write-Host 'MYSQL_SERVICE_NOT_FOUND'
}
