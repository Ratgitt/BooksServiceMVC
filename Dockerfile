FROM openjdk:17-jdk

WORKDIR /app

COPY ./target/project2_SpringBoot-0.0.1-SNAPSHOT.jar /app/project2.jar

EXPOSE 8080

CMD ["java", "-jar", "project2.jar"]