#!/usr/bin/env bash
set -e
# compile into out/
mkdir -p out
javac -d out src/*.java
# start server in background
nohup java -cp out CalculatorServer > server.log 2>&1 &
echo "Calculator server started (port 8080); logs: server.log"
