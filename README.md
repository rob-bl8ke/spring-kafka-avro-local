
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

### Mockoon

```bash
docker compose up -d
curl http://localhost:3001/hello
# → {"message":"Hello from Mockoon!"}
```

#### Handle multiple files

Using the following approach to create multiple files to better organise your mockoon endpoints:
```
.
├─ docker-compose.yml
└─ mockoon/
   ├─ customers.json      # port 3001
   ├─ inventory.json      # port 3002
   └─ payments.json       # port 3003

```

If each file has a different port, one can expose those ports. Also, simply using `/data` as the data folder will load all the `.json` files in that folder.

```yaml
services:
  mockoon:
    image: mockoon/cli:latest
    container_name: mockoon
    volumes:
      # mount the whole folder read-only
      - ./mockoon:/data:ro
    command:
      # load *all* JSON files found in /data
      - "--data" "/data"
      # optional: watch for changes and auto-reload
      - "--watch"
      # optional: make sure old files are auto-upgraded
      - "--repair"
    ports:
      # expose each environment’s own port
      - "3001:3001"
      - "3002:3002"
      - "3003:3003"

```
Now, your Spring-Boot app (or Postman, etc.) calls:
- http://localhost:3001/... for Customers
- http://localhost:3002/... for Inventory
- http://localhost:3003/... for Payments

#### Seperate container approach (same or overlapping ports)

**Advantages:** Each environment can run on the same port number inside its own container (if you ever need that). You can turn individual mocks on/off with docker compose up mockoon-inventory.

**Downside:** a bit more YAML to maintain.

```yaml
services:
  mockoon-customers:
    image: mockoon/cli:latest
    volumes:
      - ./mockoon/customers.json:/data/env.json:ro
    command: ["--data", "/data/env.json", "--port", "8080"]
    ports:
      - "8080:8080"

  mockoon-inventory:
    image: mockoon/cli:latest
    volumes:
      - ./mockoon/inventory.json:/data/env.json:ro
    command: ["--data", "/data/env.json", "--port", "8081"]
    ports:
      - "8081:8081"

```

#### Handy CLI flags

| Flag                 | Purpose                                                                       |
| -------------------- | ----------------------------------------------------------------------------- |
| `--watch`            | Auto-reload when you edit the JSON on disk. Great for live tweaking.          |
| `--repair`           | Quietly migrate any “too old” env file on startup (avoids the yes/no prompt). |
| `--log-transaction`  | Prints every incoming request/response to the container logs.                 |
| `--data base folder` | If you point to a folder, **all `*.json` files** inside are loaded.           |

#### Organising inside a single env file (fallback)

If you must stick to one file per service but still want order, the Mockoon GUI lets you create folders to group routes, give them colours, collapse/expand, etc. Those folders carry over to the CLI automatically.

#### Auto-repair

If your JSON is in Mockoon ≤ v1.x format (it still has the legacy "type": "environment" / "version": "3.4.0" duo and no lastMigration field), add the `--repair` to the command. This should attempt to make a fix without the yes/no prompt that will break your `docker compose` command. However, a re-export is recommended.

```yaml
command:
  - "--data" "/data/mockoon-env.json"
  - "--port" "3001"
  - "--repair"          # migrate without the yes/no prompt
```

