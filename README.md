## REST API

### Run Postgres container

```shell
git clone https://github.com/NovaServe/fitness-hosting.git
cd path/fitness-hosting
cd postgres
cp .env_template .env
docker compose up -d
```

### Run server

```shell
git clone https://github.com/NovaServe/fitness-backend.git
cd path/to/fitness-backend
./gradlew clean build
java -jar build/libs/backend.jar
```

### Swagger

`localhost:8085/swagger-ui.html`
