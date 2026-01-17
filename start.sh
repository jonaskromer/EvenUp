#!/bin/bash

OS_TYPE="$(uname -s)"

if [[ "$OS_TYPE" == "Darwin" ]]; then
    echo "Starting EvenUp on macOS..."
    docker compose -f docker-compose.yml -f docker-compose.macos.yml up --build
elif [[ "$OS_TYPE" == "Linux" ]]; then
    echo "Starting EvenUp on Linux..."
    docker compose -f docker-compose.yml -f docker-compose.linux.yml up --build
else
    echo "Unsupported OS: $OS_TYPE"
    exit 1
fi