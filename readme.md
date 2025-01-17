# Kafka Streams with Quarkus [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

This repository is related with the article: [Kafka Streams with Quarkus](https://piotrminkowski.com/2021/11/24/kafka-streams-with-quarkus/).

It shows how to build a simple stock market application that consumes Kafka Streams.

The following picture illustrates our architecture:

<img src="https://i1.wp.com/piotrminkowski.com/wp-content/uploads/2021/11/Screenshot-2021-11-23-at-09.52.58.png?ssl=1" title="Architecture"><br/>

## Run locally

### Usage
You need to have Maven, JDK11+ and Docker installed on your local machine.

First, run the `order-service`:
```shell
cd order-service
mvn quarkus:dev
```

It will automatically start Kafka using Redpanda (Kafka API compatible platform). \
Then, run the `stock-service`:
```shell
cd stock-service
mvn quarkus:dev
```

Alternatively you can use workflow based stock service so instead of `stock-service` use
`workflow-stock-service` module.
Then, run the `workflow-stock-service`:
```shell
cd workfow-stock-service
mvn clean quarkus:dev
```

Observe the logs. \
You can access Quarkus Dev UI console: `http://localhost:8080/q/dev`. \
You can also call some REST endpoints with analytical data:
```shell
$ curl http://localhost:8080/transactions/products
$ curl http://localhost:8080/transactions/products/3
$ curl http://localhost:8080/transactions/products/5
```
