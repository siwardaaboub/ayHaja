@echo off
cd /d "%~dp0"
node node_modules\vite\bin\vite --host 0.0.0.0 --port 3000
