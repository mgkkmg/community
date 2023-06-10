FROM openjdk:17
ARG JAR_FILE=module-api/build/libs/app.jar
COPY ${JAR_FILE} ./app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./app.jar"]