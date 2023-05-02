#!/bin/sh

# DOCKER PART

echo "REMOVE-CONTAINER"
docker rmi --force cyrilthese/dltsim-democratic:latest

echo "CONTAINER-BUILD"
docker build -f dltdocker -t cyrilthese/dltsim-democratic:latest --no-cache .

echo "CONTAINER-PUSH"
docker push cyrilthese/dltsim-democratic:latest
