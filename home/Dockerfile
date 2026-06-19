FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY gradlew /workspace/
COPY gradle /workspace/gradle
COPY settings.gradle build.gradle /workspace/

COPY home/build.gradle /workspace/home/
COPY home/src /workspace/home/src

COPY proto/build.gradle /workspace/proto/
COPY proto/src /workspace/proto/src

COPY auth/build.gradle /workspace/auth/

RUN chmod +x ./gradlew
RUN ./gradlew :home:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/home/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]