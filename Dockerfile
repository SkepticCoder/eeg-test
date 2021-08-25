ARG JAVA_VERSION=11
ARG DIST="openjdk"
ARG REPOSITORY=""
ARG VERSION=${JAVA_VERSION}-jdk-buster
FROM ${REPOSITORY}${DIST}:${VERSION} AS builder

WORKDIR src
COPY ./ .
RUN ./mvnw install

FROM ${REPOSITORY}${DIST}:${VERSION}
RUN adduser --no-create-home stream-app
ARG APP=assesment-1.0-SNAPSHOT.jar
ARG JVM_ARG=""
ENV APP=$APP
ENV JVM_ARG=$JVM_ARG
USER stream-app
WORKDIR /app
COPY --from=builder /src/target/$APP .
ENTRYPOINT java $JVM_ARG -jar $APP