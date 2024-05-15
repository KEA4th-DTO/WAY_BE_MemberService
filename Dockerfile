FROM openjdk:17-alpine
RUN ./gradlew build
COPY build/libs/way.member-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080