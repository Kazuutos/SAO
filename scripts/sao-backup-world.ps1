param([switch]$StopFirst)
$ErrorActionPreference = "Stop"
$serverDir = "C:\Users\allon\MinecraftDev\servers\forge\sao-test"
$world = "$serverDir\world"
$backupRoot = "$serverDir\backups"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"

if ($StopFirst) {
  Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*sao-test*" } | Stop-Process -Force
}

if (-not (Test-Path $world)) { throw "World folder missing: $world" }
New-Item -ItemType Directory -Force -Path $backupRoot | Out-Null
$backupFile = "$backupRoot\world-$timestamp.zip"
Add-Type -AssemblyName 'System.IO.Compression.FileSystem'
[IO.Compression.ZipFile]::CreateFromDirectory($world, $backupFile)
Write-Host "Backup created: $backupFile"
