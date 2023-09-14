FROM openjdk@sha256:9c484cfbe3cda24c78838da9ad333be25c1d3bcf4c9788b4f5cf34911c07c1cf AS build

WORKDIR /app

COPY .mvn .mvn
COPY src src
COPY pom.xml .
COPY mvnw .

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests

FROM openjdk@sha256:9c484cfbe3cda24c78838da9ad333be25c1d3bcf4c9788b4f5cf34911c07c1cf

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN apt update
RUN mkdir "/etc/prometheus"
RUN mkdir "/var/lib/prometheus"
RUN apt-get install -y wget
RUN wget https://github.com/prometheus/prometheus/releases/download/v2.43.0/prometheus-2.43.0.linux-amd64.tar.gz
RUN tar vxf prometheus*.tar.gz
WORKDIR /app/prometheus-2.43.0.linux-amd64
RUN mv prometheus /usr/local/bin
RUN mv promtool /usr/local/bin
RUN mv consoles /etc/prometheus
RUN mv console_libraries /etc/prometheus

RUN rm prometheus.yml

COPY prometheus.yml .

RUN mv prometheus.yml /etc/prometheus

COPY prometheus.service .

RUN mv prometheus.service /etc/systemd/system/

ENV PORT=9090
EXPOSE 9090

WORKDIR /app
COPY prometheus.yml .
COPY start.sh .
RUN chmod +x start.sh

ENTRYPOINT ["./start.sh"]