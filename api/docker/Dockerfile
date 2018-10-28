FROM openjdk:8-alpine

WORKDIR /opt/app
ADD --chown=daemon:daemon opt /opt
USER daemon

EXPOSE 8080

CMD java -jar api.jar
