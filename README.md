# Tool Rental Point-Of-sale

This exercise follows the Point of Sale requirements. 

This application is a Spring Boot MVC application which listens on `http://localhost:8080` for POST requets to the `/api/checkout` RESTful endpoint.

It is secured using HTTP Basic Auth, with the "user:user" credentials.

## Building
This application uses Gradle to build.  To do so, simply run:

```bash
gradle clean build
```

## Running
To connect to the endpoint you can run this command:

```bash
curl -H "Authorization: Basic dXNlcjp1c2Vy" -H "Content-Type: application/json" -d "{ \"code\": \"JAKR\", \"daysCount\":\"4\", \"discount\":10,\"checkoutDate\":\"2015-09-03\" }" http://localhost:8080/api/checkout
```

## Testing
You can run the JUnit tests from Gradle.  The tests are written as a simgle parameterized test method that takes multiple inputs from the `src/test/resources/data.csv` file.

```bash
gradle test
```
