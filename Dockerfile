FROM maven:3.9.0-amazoncorretto-17@sha256:034d0c622da47b6afbb03c847da69cfb5ceb32e7e75d480832af6aac133bf875 as buildtime

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.6-al2@sha256:f92d1ea267909b4d0a431d5b48eded6cf58427455ab85e2fdb36f73542e8f663 as runtime

VOLUME /tmp
WORKDIR /app

COPY --from=buildtime /build/target/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.7/applicationinsights-agent-3.4.7.jar /app/applicationinsights-agent.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
