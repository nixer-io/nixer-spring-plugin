# official OpenJDK image, see https://hub.docker.com/_/openjdk/
FROM openjdk:8u242-jdk-slim

RUN apt-get update && apt-get install -y git

WORKDIR /nixer

COPY publish.sh .

RUN chmod +x ./publish.sh

ENTRYPOINT ["./publish.sh"]
