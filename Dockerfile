FROM openjdk:17
ARG JAR_FILE=modual-api/build/libs/app.jar
COPY ${JAR_FILE} ./app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./app.jar", "-Dspring.profiles.active=prod"]