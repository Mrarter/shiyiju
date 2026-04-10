# 使用 Maven 构建
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
# 复制 server 目录下的所有文件
COPY server/ .
# 在 server 目录下执行 Maven 构建
RUN mvn package -DskipTests

# 运行
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
