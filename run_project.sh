#!/bin/bash

# Codevia Project Runner Script
# This script will help you run the project without manually installing Maven

echo "🚀 Codevia Project Setup & Run Script"
echo "======================================"

# Check if Maven is installed
if command -v mvn &> /dev/null; then
    echo "✅ Maven is already installed"
else
    echo "❌ Maven is not installed"
    echo "Please install Maven first:"
    echo "  - Using Homebrew: brew install maven"
    echo "  - Or download from: https://maven.apache.org/download.cgi"
    echo ""
    echo "After installing Maven, run this script again."
    exit 1
fi

# Check if Firebase service account key exists
if [ ! -f "./Codevia Firebase Admin SDK.json" ]; then
    echo "❌ Firebase service account key not found!"
    echo "Please make sure 'Codevia Firebase Admin SDK.json' is in the project root directory"
    exit 1
fi

echo "✅ Firebase service account key found"

# Navigate to project directory
cd "$(dirname "$0")"

echo "📦 Installing dependencies..."
mvn clean install -q

if [ $? -eq 0 ]; then
    echo "✅ Dependencies installed successfully"
else
    echo "❌ Failed to install dependencies"
    exit 1
fi

echo "🔨 Compiling project..."
mvn compile -q

if [ $? -eq 0 ]; then
    echo "✅ Project compiled successfully"
else
    echo "❌ Failed to compile project"
    exit 1
fi

echo "🏃 Running Codevia application..."
echo "======================================"
mvn exec:java -Dexec.mainClass="app.Main" -q

echo ""
echo "👋 Thank you for using Codevia!"
