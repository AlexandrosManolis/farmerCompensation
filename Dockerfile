FROM openjdk:21-rc-oracle
WORKDIR /app
COPY target/farmerCompensation-0.0.1-SNAPSHOT.jar ./application.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","application.jar"]