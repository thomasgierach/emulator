#!/usr/bin/env bash

set -u

AUTH_URL="${AUTH_URL:-http://localhost:8081}"
USERNAME="${USERNAME:-smoke-test-$(date +%s)}"
PASSWORD="${PASSWORD:-password123456789}"
EMAIL="${EMAIL:-${USERNAME}@example.com}"

echo "Auth service: ${AUTH_URL}"
echo "Test user: ${USERNAME}"
echo

echo "========================================"
echo "1. Login should fail"
echo "========================================"

curl --silent \
  --show-error \
  --write-out "\nHTTP status: %{http_code}\n" \
  --request POST \
  "${AUTH_URL}/auth/login" \
  --header "Content-Type: application/json" \
  --data "{
    \"username\": \"${USERNAME}\",
    \"password\": \"${PASSWORD}\"
  }"

echo
echo "========================================"
echo "2. Create user"
echo "========================================"

curl --silent \
  --show-error \
  --write-out "\nHTTP status: %{http_code}\n" \
  --request POST \
  "${AUTH_URL}/auth/users" \
  --header "Content-Type: application/json" \
  --data "{
    \"username\": \"${USERNAME}\",
    \"password\": \"${PASSWORD}\",
    \"email\": \"${EMAIL}\"
  }"

echo
echo "========================================"
echo "3. Login should succeed"
echo "========================================"

curl --silent \
  --show-error \
  --write-out "\nHTTP status: %{http_code}\n" \
  --request POST \
  "${AUTH_URL}/auth/login" \
  --header "Content-Type: application/json" \
  --data "{
    \"username\": \"${USERNAME}\",
    \"password\": \"${PASSWORD}\"
  }"

echo