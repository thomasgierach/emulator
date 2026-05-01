#!/usr/bin/env bash
set -e
./gradlew :auth:test --no-build-cache --rerun-tasks

