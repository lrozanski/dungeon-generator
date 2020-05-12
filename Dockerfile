FROM openjdk:11-jre-slim
EXPOSE 8080:8080

COPY ./build/libs/dungeon-generator-0.0.1-SNAPSHOT.jar /app/dungeon-generator.jar
WORKDIR /app

CMD ["java", "-server", "-jar", "dungeon-generator.jar", "-Xms512M"]
