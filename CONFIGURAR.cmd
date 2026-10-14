@echo off
cd /d "%~dp0"
powershell -NoProfile -Command "& ([scriptblock]::Create([IO.File]::ReadAllText((Join-Path (Get-Location) 'configurar-mysql.ps1'))))"
