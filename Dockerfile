FROM openjdk:17
ARG JAR_FILE=modual-api/build/libs/app.jar
COPY ${JAR_FILE} ./app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "java ${JAVA_OPTS} -jar", "./app.jar"]