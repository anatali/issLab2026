## ----------------------------------------------------------
## Dockerfile for  SistemaSJavalin
## ----------------------------------------------------------

FROM eclipse-temurin:17.0.5_8-jre-focal AS builder
## ADD extracts the tar
ADD ../build/distributions/conway26GuiAlone-1.0.tar /
WORKDIR /conway26GuiAlone-1.0/bin

## RUN apt-get update -y


CMD ["bash", "conway26GuiAlone"]

## 0) docker rmi conway26guialone:1.0
## 1) gradlew distTar
## 2) docker build -t conway26guialone:1.0 .   ##lowercase
 
## docker run -it --rm --name conway26guialone -p8080:8080/tcp   conway26guialone:1.0  /bin/bash
 
# docker tag conway26guialone:1.0 natbodocker/conway26guialone:1.0
#type docker_password.txt | docker login --username natbodocker --password-stdin
#docker login --username natbodocker --password xyz
# docker push natbodocker/sistemasjavalin:1.0

# docker-compose -f conway26GuiAlone.yaml  up down