# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy toàn bộ source
COPY . .

# build jar (dùng maven wrapper)
RUN ./mvnw -DskipTests clean package

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy file jar từ stage build sang
COPY --from=build /app/target/*.jar app.jar

# port trong container
EXPOSE 8080
ENV PORT=8080

# run spring boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
