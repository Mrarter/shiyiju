# Java 17 运行环境
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]
