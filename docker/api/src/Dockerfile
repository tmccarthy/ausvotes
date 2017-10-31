FROM openjdk:8u131-jre-alpine

COPY "ausvotes-api*.jar" "/opt/ausvotes-api/ausvotes-api.jar"

ENV JAVA_OPTS="-Xmx256m"

WORKDIR "/opt/ausvotes-api/"

CMD java $JAVA_OPTS -jar ausvotes-api.jar
