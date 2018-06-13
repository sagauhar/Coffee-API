# Coffee API

This project provides a simple implementation of a Coffee ordering API.

## Technologies

When selecting technical stack, the idea was to pick such tools and technologies which can provide base for a stable, reliable, scalable and highly available architecture.

For this, I chose [API Gateway](https://aws.amazon.com/api-gateway/), together with [Lambdas](https://aws.amazon.com/lambda/).

I also picked [Java 8](https://www.oracle.com/au/java/index.html) to be the language of choice simply because of the solution requirements.

To store the data, an [RDS](https://aws.amazon.com/rds/) instance of MySQL is used, which is managed database by AWS.

The reason to choose a relational database, was to utilize the generation of auto incremented IDs. Otherwise, it could have been easily replaced by a document oriented data store such as [DynamoDB](https://aws.amazon.com/dynamodb).

Instead of writing SQL queries in the code, I have used [jOOQ](https://www.jooq.org/) as a SQL Query Builder library. It makes the interaction with database very convenient, easy to maintain and readable at the same time.

To decouple the classes, I have used [Guice](https://github.com/google/guice) as a library to provide Dependency Injection. It is a very light weight Google backed library compared to Spring.

[Maven](https://maven.apache.org/) is used for source project management and build tool. This is also used to build the artifact which is uploaded to AWS environment using [Serverless](https://serverless.com/).

Serverless is a scripting language based on YAML. It is used to generate the deployment script for AWS environment, called [CloudFormation](https://aws.amazon.com/cloudformation) template. It not only generates the script but also creates the whole stack except the RDS instance.

To secure the API, an API key is generated which is used by AWS to authenticate before forwarding the request to one of the Lambdas.

For unit tests, I have used a local version of [MySQL](https://github.com/vorburger/MariaDB4j) which starts as part of tests and then at the end shuts down itself.

## Configuration

To run the project, you need to have Maven installed on the machine. I have tested the project with 3.5 version of it.

You also need Java 8 and [Docker](https://www.docker.com/) to run the Serverless script in a controlled environment.

Since, the application interacts with database, there are four environment variables that are needed for the Application to build and run successfully.

```
URL - Endpoint of MySQL database in the form of jdbc:mysql://hostname:port/name_of_database.
USERNAME - Username to use for authentication
PASSWORD - Password to use for authentication
SCHEMA - Schema to connect to
```

Create the database and the two tables. The scripts for tables are in **resources/sql** folder of the code.

Together with the above variables, you will also need AWS Access and Secret Keys. Those can be generated for a user from [IAM](https://aws.amazon.com/iam/) console.

## Build

In order to build the code, you need to download or clone the [code](https://github.com/sagauhar/Coffee-API).

To clone the code, you will require Git on your machine. Once installed, you can follow the GitHub instructions on the repository page for further instructions.

Once, the code is downloaded and available on a machine, you need to install Java [8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

To confirm, the installation, you should run **java** on terminal or command prompt depending on your operating system. Make sure that you also add [JAVA_HOME](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html) as part of your environment variables.

You will also require Apache Maven to be installed. The version that was tested with this project, can be downloaded from this [link](https://maven.apache.org/download.cgi).

Set the [M2_HOME](https://stackoverflow.com/questions/26609922/maven-home-mvn-home-or-m2-home) and then run **mvn** command to see if it is working or not.

Once, the above two are setup, you can go to the project directory, where the code was copied, and run the following command:

```
mvn clean package
```

## Running the Project locally

The project can be run locally by using [AWS Cloud9](https://aws.amazon.com/cloud9/). Otherwise, if you don't have access to it, then you can use unit tests to debug the code.

## Deployment

To deploy the project to AWS, you will require [Docker](https://docs.docker.com/install/) to be installed.

You need to run the following command to deploy the project to AWS:

```
docker-compose build --build-arg URL=jdbc:mysql://hostname:port/name_of_database --build-arg USERNAME=username --build-arg PASSWORD=password --build-arg SCHEMA=name_of_database coffee-service
docker-compose run coffee-service bash -c "./node_modules/.bin/serverless deploy --stage dev"
```

If everything goes well, then you should see a successful message with the endpoint url and a key on console.

The url and the key, can be used to invoke the APIs. The key needs to be part of the header in the form of:

```
x-api-key: value_of_key
```

## Testing

There are unit tests written for all of the endpoints. Local MySQL instance is used instead of the external one.

To inject a local version of MySQL, a mock is used instead of the external MySQL which is used by the actual Lambda.

**BaseEndpointTest** is used to run the suite of tests because of the fact that the database instance is shared across tests.

## FAQ

**Q**: The unit tests fail, complaining about some openssl library not found. What is the fix for it?
**A**: This happens when the test code tries to start the local MariaDB4j database. There are two ways to avoid it:
       1. Add a -DskipTests=true when running the mvn command. You can do the same in Dockerfile.
       2. Follow the instructions mentioned in this [link](https://github.com/vorburger/MariaDB4j/issues/48).

## Things to Improve

The unit tests can be done in a far better way. There are no tests for the Dataaccess class. This was mainly because of shortage of time.

Currently, the database and table creation is done manually. Although, the scripts for those, do exist in resources/sql folder, but perhaps a CloudFormation script could have been a better way.

Java wouldn't have been my choice for creating Lambdas. I would have chosen Node since, Node is native to AWS Lambda environment but Java requires additional JVM libraries for AWS to run the code.

API documentation is also missing from the code. This has become a standard and was missed because of deadline.