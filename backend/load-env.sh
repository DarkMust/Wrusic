#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
    echo "Environment variables loaded from .env file"
else
    echo "No .env file found. Please create one with your Spotify API credentials."
    exit 1
fi

# Run the application with the loaded environment variables
./mvnw spring-boot:run 