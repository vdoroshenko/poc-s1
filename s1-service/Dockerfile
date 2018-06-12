FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@exadel.com>

ADD ./target/dynamailboxes-service.jar /opt/dynamailboxes/app/
RUN mkdir -p /opt/dynamailboxes/log
CMD ["java", "-Xmx200m", "-jar", "/opt/dynamailboxes/app/dynamailboxes-service.jar"]

EXPOSE 8080