# Postgres database with Docker
```bash
docker run --name ds-lab-pg /
--rm /
-e POSTGRES_PASSWORD=pass123 /
-e POSTGRES_USER=postgres /
-e POSTGRES_DB=farmers /
-d /
--net=host -v ds-lab-vol:/var/lib/postgresql/data postgres:14
```

# Build and run without test

You can build the project without running the tests by executing:
```bash
./mvnw package -Dmaven.test.skip
```
Then, to run the application (when a postgres database is active):
```bash
java -jar target/farmerCompensation-0.0.1-SNAPSHOT.jar
```


# Build and run test
To run the tests along with the build use:
```bash
./mvnw test
```


# Dockerize
Skip the tests during the build process:
```bash
./mvnw package -Dmaven.test.skip
```

After the build to start the application with Docker Compose:
```bash
docker-compose up
```



### **Notice**



If you use git protocol in frontend build then run: 
```bash
export DOCKER_BUILDKIT=0
export COMPOSE_DOCKER_CLI_BUILD=0
docker-compose up --build
```


# Jenkins

In oreder to execute Jenkinksfiles create these 3 secret text credentials into the machine you running jenkins service

* my-email: github email
* docker-push-secret: github token
* docker-username: github username (lowercase letters)



# Useful Links


* [Postgres Docker Hub](https://hub.docker.com/_/postgres)

* [JPA EntityManager example in Spring Boot](https://www.bezkoder.com/jpa-entitymanager-spring-boot/)

* [JPA/Hibernate Persistence Context](https://www.baeldung.com/jpa-hibernate-persistence-context)