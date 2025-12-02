# ====== Stage 1: Build JAR ======
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy file cần thiết
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src ./src

# cho phép thực thi mvnw (fix lỗi Permission denied)
RUN chmod +x mvnw

# build ứng dụng
RUN ./mvnw -DskipTests clean package


# ====== Stage 2: Run JAR ======
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy jar từ stage build sang
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
