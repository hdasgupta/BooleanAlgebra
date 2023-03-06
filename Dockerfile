#

FROM ubuntu:java

COPY ./target/Digital-0.0.1-SNAPSHOT.jar .

EXPOSE 80

ENTRYPOINT java -jar Digital-0.0.1-SNAPSHOT.jar

