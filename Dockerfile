FROM openjdk:17
VOLUME /tmp
COPY target/*.jar app.jar
COPY src/main/resources/application.properties /config/
COPY src/main/resources/trustStoreFile /src/main/resources/trustStoreFilecc
WORKDIR /
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]