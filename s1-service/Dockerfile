FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@exadel.com>

ADD ./build/libs/s1-0.0.1.jar /opt/s1/app/
RUN mkdir -p /opt/s1/log
CMD ["java", "-Xmx200m", "-jar", "/opt/s1/app/s1-0.0.1.jar"]

EXPOSE 8080