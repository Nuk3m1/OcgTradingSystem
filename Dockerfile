FROM maven:3.8.4-openjdk-8 AS build

WORKDIR /app

# 复制pom.xml和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 确认JAR文件已生成
RUN ls -la /app/target/

# 运行阶段使用较小的基础镜像
FROM openjdk:8-jre-slim

WORKDIR /app

# 从构建阶段复制构建好的jar文件
COPY --from=build /app/target/ocgtradingsystem-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]