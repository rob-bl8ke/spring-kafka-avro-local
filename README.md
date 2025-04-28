
## Usage

```bash
docker compose up
```

```bash
mvn clean install
```
Run your application using the `local` profile to avoid external dependencies.

Once `docker compose up` is run, use `curl http://localhost:8091/subjects
` to make sure that your schema registry is reachable. If no schemas are registered, it will return an empty array. Otheriwse it will return something like this: `["context-topic-value"]`.

### Inspect Kafka

```bash
docker inspect kafka
```

### Listen for produced messages

Open up the Kafka bash terminal and start listening for produced messsages.
```bash
docker exec -it kafka bash

# When at the prompt:
# Navigate to Kafka
cd /usr/bin/
ls

# Ensure the topic exists:
kafka-topics --bootstrap-server kafka:29092 --list

# Set up a consumer/listener and start listening....
kafka-console-consumer --bootstrap-server kafka:29092 --topic context-topic --from-beginning
```

Run your application, and hit the endpoint: `curl GET http://localhost:8090/hello`. You should see the message coming through.

## Overview

The goal is to find a way to set up an environment that is for local development only. In order to run Kafka locally and have some support for Avro one has to do the following:

- Eliminate the dependency on an AWS Glue Schema Registry
- Eliminate the dependency on a cloud-based Kafka service

### Docker compose

This can be done using `docker-compose.yml` which spins up two containerized services: one for an Avro compatible schema registry (Confluent Schema Registry + Avro serializer) and one for a local Kafka service instance.

One must configure Kafka to use a serializer that knows how to serialize Avro objects — specifically the `SomethingHappened` generated class.

```yaml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    properties:
      schema-registry-url: http://localhost:8091
```

The KafkaAvroSerializer will:

- Automatically register your SomethingHappened schema at http://localhost:8091/subjects/your-topic-name-value/versions
- Serialize your Avro object into bytes for Kafka
- Embed the schema ID into the Kafka message

You can fully debug your Spring Boot Kafka production locally this way.

### Additional dependency management

Some additional dependencies are required by the the client `app-main`, and these are found in the `pom.xml` file with a group Id of `io.confluent`. Note too, that you have to reach out to the confluent repository to fetch these dependenceis so that repository also needs to be declared in the `pom.xml` file. Without this, Maven will only ever check Maven Central. 

Check your dependencies for conflicts:

```bash
mvn dependency:tree
# or
mvn dependency:tree -Dverbose -Dincludes=commons-logging
```

Unfortunately, at this time the only way to get around dependency conflicts with the kafka-core library, one must make use of the `dependencyManagement` tags in order to skirt around the issue.

### Changes in Config

Unfortunately some changes need to be made to the `KafkaProducerConfig`. Instead of using the following:

```java
@Value("${spring.kafka.producer.keySerializer}")
private String keySerializer;

@Value("${spring.kafka.producer.valueSerializer}")
private String valueSerializer;
```
You will want to use the following:

```java
@Value("${spring.kafka.producer.key-serializer}")
private Class<?> keySerializer;

@Value("${spring.kafka.producer.value-serializer}")
private Class<?> valueSerializer;
```
However, this shouldn't be a problem since the final solution relies on swapping out these configuration files for local and for testing, anyhow, as we do not want all the security and cloud settings for local testing.

The alternative is to keep it String and manually `Class.forName()` in your config code later. A consideration if the current change is not desirable.

