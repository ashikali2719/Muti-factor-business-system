FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY Backend/pom.xml .
COPY Backend/src ./src

RUN mvn clean package -DskipTests

FROM jetty:11-jre17

COPY --from=build /app/target/business-decision-system-1.0-SNAPSHOT.war /var/lib/jetty/webapps/ROOT.war

ENV JAVA_OPTIONS="-Djetty.http.port=8080"

EXPOSE 8080

CMD ["java","-jar","/usr/local/jetty/start.jar"]