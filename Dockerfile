FROM openjdk:8-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY target/app-0.0.1-SNAPSHOT.jar /app/target/app.jar
CMD java -jar target/app.jar
