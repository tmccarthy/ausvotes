FROM openjdk:8u131-jre-alpine

COPY "ausvotes-backend*.jar" "/opt/ausvotes-api/ausvotes-backend.jar"

ENV JAVA_OPTS="-Xmx1024m"
# TODO volume for raw data?

WORKDIR "/opt/ausvotes-api/"

CMD java $JAVA_OPTS -jar ausvotes-backend.jar
