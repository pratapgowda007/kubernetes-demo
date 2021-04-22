FROM openjdk:8-jdk-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} pratap-file-upload-test.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/pratap-file-upload-test.jar"]
