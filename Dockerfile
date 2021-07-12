FROM openjdk:11.0.11-jdk-oracle
VOLUME /tmp
ADD /target/marketdata-0.0.1-SNAPSHOT.jar marketdata.jar
ENTRYPOINT ["java","-jar","/marketdata.jar"]
