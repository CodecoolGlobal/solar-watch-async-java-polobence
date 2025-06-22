# Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /tmp

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]

# docker run -p 8080:8080 -e GEO_API_KEY=5b754714c82d1d721797e7dea3b82604 codecool/solarwatch-api