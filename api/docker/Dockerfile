FROM openjdk:8-alpine

WORKDIR /opt/app
ADD --chown=daemon:daemon opt /opt
USER daemon

CMD java -jar api.jar
