docker compose down
docker compose up -d postgres redis auth frontend
docker compose down home
./gradlew :home:bootRun