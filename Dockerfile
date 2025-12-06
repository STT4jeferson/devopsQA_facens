# ====== build ======
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# ====== runtime ======
FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
COPY --from=build /build/target/*-SNAPSHOT.jar /app/app.jar
USER nonroot
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]