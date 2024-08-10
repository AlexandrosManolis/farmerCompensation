FROM openjdk:21-rc-oracle
WORKDIR /app
COPY target/farmerCompensation-0.0.1-SNAPSHOT.jar ./application.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","application.jar"]

# Copy the entrypoint script into the container
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/entrypoint.sh"]