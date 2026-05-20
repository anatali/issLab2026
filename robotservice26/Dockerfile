## ----------------------------------------------------------
## Dockerfile for unibo.robotservice26
## ----------------------------------------------------------

FROM eclipse-temurin:17.0.5_8-jre-focal as builder
## ADD extracts the tar
ADD ./build/distributions/robotservice26-1.0.tar / 
WORKDIR /robotservice26-1.0/bin
COPY ./*.pl ./
COPY ./*.json ./
COPY ./*.bin ./
COPY ./*.txt ./

## RUN apt-get update -y
## RUN apt-get install -y wiringpi
## RUN sudo apt-get install -y python

CMD ["bash", "robotservice26"]

## 0) docker rmi robotservice26:1.0
## 1) gradlew distTar
## 2) docker build -t robotservice26:1.0 .
## OCCORRE APRIRE UDP PER PERMETTERE COAP
## docker run -it --rm --name robotservice26 -p8125:8125/tcp -p8125:8125/udp  --privileged robotservice26:1.0  /bin/bash
## MODIFICA DEL FILE DI CONFIGURAZIONE
# docker cp basicrobotConfig.json 89193dba02a2:/robotservice26-1.0/bin/basicrobotConfig.json
# docker cp stepTimeConfig.json cab3aa29f8eb:/robotservice26-1.0/bin/stepTimeConfig.json

# docker tag robotservice26:1.0 natbodocker/robotservice26:1.0

#type docker_password.txt | docker login --username natbodocker --password-stdin
#docker login --username natbodocker --password xyz
# docker push natbodocker/robotservice26:1.0

 
# docker-compose -f robotservice26.yaml  up