FROM docker.io/library/openjdk:8@sha256:97698df4caa8ee68ab7e4c42f4b85820524307f29dd280453a9481dcbb40643b

WORKDIR /root

COPY target/unitconverter.jar /root/unitconverter.jar
COPY src /root/src

CMD ["java", "-jar", "unitconverter.jar"]
