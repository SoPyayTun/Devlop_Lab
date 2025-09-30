FROM openjdk:24
COPY ./target/Devlop_Lab-1.0-SNAPSHOT-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "Devlop_Lab-1.0-SNAPSHOT-jar-with-dependencies.jar"]