FROM public.ecr.aws/docker/library/maven:3.9.6-amazoncorretto-17@sha256:b64f097a87e94f3fb433649f2a49270564fa626494d7d6bfd8955f32da794210 AS buildtime

WORKDIR /build
COPY . .

RUN mvn clean package -DskipTests

FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre@sha256:97077b491447b095b0fe8d6d6863526dec637b3e6f8f34e50787690b529253f3 AS runtime

VOLUME /tmp
WORKDIR /app

RUN useradd --uid 10000 runner
USER 10000

COPY --from=buildtime /build/target/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
