# Build frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json* ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi
COPY frontend/ ./
RUN npm run build

# Build backend
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./backend/pom.xml
RUN mvn -f backend/pom.xml -q -DskipTests dependency:go-offline
COPY backend/ ./backend/
COPY --from=frontend-build /app/frontend/dist ./backend/src/main/resources/static
RUN mvn -f backend/pom.xml -DskipTests package

# Runtime
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
ENV APP_JWT_SECRET=change_me_in_prod_change_me_in_prod_32
ENV SPRING_PROFILES_ACTIVE=prod
VOLUME ["/app/data"]
COPY --from=backend-build /app/backend/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
