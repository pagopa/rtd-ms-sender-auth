FROM maven:3.9.0-amazoncorretto-17@sha256:0d683f66624265935e836c9d2c3851ce3cf250cb48c9929d979d8d80f62d8590 AS buildtime

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.6-al2@sha256:86ad3a5620d6f7590f59fb6067b98687367e49e632a5ee719fb03bc9ffd1499f AS runtime

VOLUME /tmp
WORKDIR /app

RUN useradd --uid 10000 runner
USER 10000

COPY --from=buildtime /build/target/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
