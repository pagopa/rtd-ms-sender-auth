FROM maven:3.9.6-amazoncorretto-17-al2023@sha256:21dc2759ee325a59ee1c4721f3964884c9082d8f3f47e9537b68d6ec9f077e35 AS buildtime

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.10-al2023-headless@sha256:7a028a2e62640aec9e3c1e284539f5ff47f5b32140f9ad5ae29a2f92b937468a AS runtime

VOLUME /tmp
WORKDIR /app

RUN useradd --uid 10000 runner
USER 10000

COPY --from=buildtime /build/target/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
