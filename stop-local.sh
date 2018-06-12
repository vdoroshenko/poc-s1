#!/bin/sh
. ./setup-env.sh
docker-compose -f docker-compose.yml -f docker-compose.dev.yml down