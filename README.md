# =denial=
a simple REST API with rate limiting capabilities (see [specifications](ASSIGNMENT.md)).

## Features
* Kotlin, gradle
* Server leverages SpringBoot 2
* Reactive runtime (Netty)
* [Unit tests](server/src/test/java/io/maslick/denial/server/IntegrationTest.kt)

## Installation
```
./gradlew clean build
```

## Usage
* Start the Server
```
java -jar server/build/libs/server-1.0.jar
java -jar -Dbucket.implementation=bucket4j server/build/libs/server-1.0.jar
```

* Start the CLI client:
```
java -jar cli/build/libs/cli-1.0.jar 5
Number of clients: 5
Press ENTER to exit...
```

## Docker
```
./gradlew build -x test
./gradlew createDockerfile
docker build -t denial-server:1.0 server/build/libs

docker run -d -p 8080:8080 denial-server:1.0
docker run -d -p 8080:8080 --env ALGORITHM=bucket4j denial-server:1.0
```

```
open "http://`docker-machine ip`:8080?clientId=test"
java -Dserver=http://`docker-machine ip`:8080 -jar cli/build/libs/cli-1.0.jar 2
```

## Load test
```
brew install vegeta

echo "GET http://localhost:8080/?clientId=loadtest" | vegeta attack -duration=10s -rate=500 | vegeta report
Requests      [total, rate]            5000, 500.10
Duration      [total, attack, wait]    9.998655209s, 9.998077s, 578.209µs
Latencies     [mean, 50, 95, 99, max]  659.709µs, 611.152µs, 849.142µs, 974.497µs, 4.721495ms
Bytes In      [total, mean]            20, 0.00
Bytes Out     [total, mean]            0, 0.00
Success       [ratio]                  0.20%
Status Codes  [code:count]             200:10  503:4990
Error Set:
503 Service Unavailable
```