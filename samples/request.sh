#!/bin/bash

set -e

curl -H "Content-Type: application/json" -X POST http://localhost:8080/virtual_machines -d @samples/request.json
