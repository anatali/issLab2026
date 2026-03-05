## ----------------------------------------------------------
## Dockerfile for unibo.robotoutgui25
## ----------------------------------------------------------

FROM eclipse-temurin:17.0.5_8-jre-focal as builder
## EXPOSE 8085
## ADD extracts the tar
ADD ../build/distributions/SistemaSJavalin-1.0.tar /
WORKDIR /SistemaSJavalin-1.0/bin
COPY ./*.pl ./
COPY ./*.json ./
COPY ./*.bin ./
COPY ./*.txt ./

## RUN apt-get update -y
## RUN sudo apt-get install -y python

CMD ["bash", "SistemaSJavalin"]

## 0) docker rmi sistemasjavalin-1.0
## 1) gradlew distTar
## 2) docker build -t sistemasjavalin-1.0 .
 
## docker run -it --rm --name sistemasjavalin-1.0 -p8080:8080/tcp   sistemasjavalin-1.0  /bin/bash
## docker run -it --rm --name sistemasjavalin-1.0 -p8080:8080/tcp   --privileged --env "HOST_IP=192.168.1.132" sistemasjavalin-1.0  /bin/bash

# docker tag sistemasjavalin:1.0 natbodocker/sistemasjavalin:1.0

#type docker_password.txt | docker login --username natbodocker --password-stdin
#docker login --username natbodocker --password xyz
# docker push natbodocker/sistemasjavalin:1.0

# docker-compose -f sistemasjavalin.yaml  up