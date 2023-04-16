build:
	./gradlew clean build

report:
	./gradlew jacocoTestReport

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

install-boot-dist:
	./gradlew clean installBootDist

.PHONY: build