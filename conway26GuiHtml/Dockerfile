## ----------------------------------------------------------
## Dockerfile for  SistemaSJavalin
## ----------------------------------------------------------

FROM eclipse-temurin:17.0.5_8-jre-focal AS builder
## ADD extracts the tar
ADD ../build/distributions/conway26GuiHtml-1.0.tar /
WORKDIR /conway26GuiHtml-1.0/bin

## RUN apt-get update -y


CMD ["bash", "conway26GuiHtml"]

## 0) docker rmi gui26html:1.0
## 1) gradlew distTar
## 2) docker build -t gui26html:1.0 .   ##lowercase
 
## docker run -it --rm --name gui26html -p8080:8080/tcp   gui26html:1.0  /bin/bash
 
# docker tag gui26html:1.0 natbodocker/gui26html:1.0
#type docker_password.txt | docker login --username natbodocker --password-stdin
#docker login --username natbodocker --password xyz
# docker push natbodocker/sistemasjavalin:1.0

# docker-compose -f conway26Guihtml.yaml  up down