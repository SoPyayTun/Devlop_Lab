FROM openjdk:24
COPY ./target/Devlop_Lab-0.1.0.5-SNAPSHOT-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "Devlop_Lab-0.1.0.5-SNAPSHOT-jar-with-dependencies.jar"]