FROM openjdk:8-jre-slim as builder
RUN apt-get update && \
    apt-get install -y --no-install-recommends apt-transport-https apt-utils bc dirmngr gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends sbt
COPY . /cardano-explorer-backend-scala
WORKDIR /cardano-explorer-backend-scala
RUN sbt api/assembly
RUN mv `find . -name explorer-api-assembly-*.jar` /explorer-api.jar
CMD ["/usr/bin/java", "-jar", "/explorer-api.jar"]

FROM openjdk:8-jre-slim
COPY --from=builder /explorer-api.jar /explorer-api.jar
COPY --from=builder /cardano-explorer-backend-scala/config/explorer-api.conf /etc/explorer-api.conf
ENTRYPOINT java -jar /explorer-api.jar /etc/explorer-api.conf