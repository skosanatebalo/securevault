.PHONY: build test up run down clean

build:
	mvn clean package -DskipTests

test:
	mvn test

up:
	docker compose up -d db

run:
	docker compose run --rm app

down:
	docker compose down

clean:
	mvn clean
	docker compose down -v
