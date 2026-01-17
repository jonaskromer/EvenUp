@echo off
echo Starting EvenUp on Windows...
docker compose -f docker-compose.yml -f docker-compose.windows.yml up --build
pause