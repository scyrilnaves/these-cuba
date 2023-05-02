#!/bin/sh

# DOCKER PART

echo "REMOVE-CONTAINER"
docker rmi --force cyrilthese/dltsim-democraticimplicit:latest

echo "CONTAINER-BUILD"
docker build -f dltdocker -t cyrilthese/dltsim-democraticimplicit:latest --no-cache .

echo "CONTAINER-PUSH"
docker push cyrilthese/dltsim-democraticimplicit:latest
