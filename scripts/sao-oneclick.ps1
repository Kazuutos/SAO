param(
    [switch]$SkipBuild,
    [switch]$SkipBackup,
    [switch]$ResetWorld,
    [switch]$NoStart,
    [switch]$CopyExtrasToClient
)

$ErrorActionPreference = "Stop"

$modDir = "C:\Users\allon\MinecraftDev\mods\forge\sao-world"
$serverDir = "C:\Users\allon\MinecraftDev\servers\forge\sao-test"
$clientMods = "C:\Users\allon\AppData\Roaming\\.minecraft\mods"
$javaExe = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot\bin\java.exe"
$argUser = "$serverDir\user_jvm_args.txt"
$argWin = "$serverDir\libraries\net\minecraftforge\forge\1.20.1-47.3.22\win_args.txt"
$jarName = "saoworld-0.1.0.jar"
$buildJar = "$modDir\build\libs\$jarName"
$backupRoot = "$serverDir\backups"
$worldDir = "$serverDir\world"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$logFile = "$serverDir\oneclick.log"
$extraJars = @("C:\Users\allon\Downloads\nexo-1.15.jar")

function Log($msg) {
    $line = "[$(Get-Date -Format "HH:mm:ss")] $msg"
    Write-Host $line
    Add-Content -Path $logFile -Value $line
}

Log "=== SAO one-click (SkipBuild=$SkipBuild, SkipBackup=$SkipBackup, ResetWorld=$ResetWorld, NoStart=$NoStart) ==="

if (-not $SkipBuild) {
    Push-Location $modDir
    . ..\..\..\use-java17.ps1
    ./gradlew.bat clean build -x test
    Pop-Location
    Log "Build finished."
} else {
    Log "Build skipped (reuse existing jar)."
}

if (-not (Test-Path $buildJar)) {
    throw "Jar missing after build: $buildJar"
}

$targets = @("$serverDir\mods", $clientMods)
foreach ($t in $targets) {
    New-Item -ItemType Directory -Force -Path $t | Out-Null
}

Copy-Item $buildJar "$serverDir\mods\$jarName" -Force
Copy-Item $buildJar "$clientMods\$jarName" -Force
Log "Copied mod jar to server+client."

foreach ($extra in $extraJars) {
    if (Test-Path $extra) {
        Copy-Item $extra "$serverDir\mods\" -Force
        if ($CopyExtrasToClient) {
            Copy-Item $extra "$clientMods\" -Force
        }
        Log "Extra jar copied: $extra"
    }
}

if (-not $SkipBackup -and (Test-Path $worldDir)) {
    New-Item -ItemType Directory -Force -Path $backupRoot | Out-Null
    $backupFile = "$backupRoot\world-$timestamp.zip"
    Add-Type -AssemblyName 'System.IO.Compression.FileSystem'
    [IO.Compression.ZipFile]::CreateFromDirectory($worldDir, $backupFile)
    Log "World backup: $backupFile"
} elseif (-not (Test-Path $worldDir)) {
    Log "No world folder found, skip backup/reset."
}

if ($ResetWorld -and (Test-Path $worldDir)) {
    Remove-Item -Recurse -Force $worldDir
    Log "World reset performed (world folder removed)."
}

if (-not $NoStart) {
    if (-not (Test-Path $javaExe)) { throw "Java missing at $javaExe" }
    if (-not (Test-Path $argUser)) { throw "Missing user_jvm_args.txt" }
    if (-not (Test-Path $argWin)) { throw "Missing win_args.txt" }
    Push-Location $serverDir
    Start-Process -FilePath $javaExe -ArgumentList "@`"$argUser`" @`"$argWin`" --nogui" -NoNewWindow
    Log "Server start triggered (detached)."
    Pop-Location
} else {
    Log "Server start skipped."
}
