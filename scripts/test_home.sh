#!/usr/bin/env bash
set -e
./gradlew :home:test --no-build-cache --rerun-tasks

