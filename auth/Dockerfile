FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew /workspace/
COPY gradle /workspace/gradle
COPY settings.gradle build.gradle /workspace/

COPY auth/build.gradle /workspace/auth/
COPY auth/src /workspace/auth/src

COPY home/build.gradle /workspace/home/

COPY proto/build.gradle /workspace/proto/
COPY proto/src /workspace/proto/src

RUN chmod +x ./gradlew
RUN ./gradlew :auth:build

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/auth/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]