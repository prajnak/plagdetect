# A sample app to use the copyleaks API to check user submitted text for plagiarism.
[Copyleaks Api](https://api.copyleaks.com/Guides/HowToUse)
App Framework obtained from [Spring boot starter web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)

App has been deployed to heroku here:
[https://safe-fjord-76241.herokuapp.com/submit](https://safe-fjord-76241.herokuapp.com/submit)

## Build Instructions
Get all the dependencies and build from scratch
```
./mvnw -DskipTests clean dependency:list install
```

## Run locally
```
./mvnw spring-boot:run
```