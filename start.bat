@echo off
echo Starting EvenUp on Windows...
docker compose -f docker-compose.yaml -f docker-compose.windows.yaml up --build
pause