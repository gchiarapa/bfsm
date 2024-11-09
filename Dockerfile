FROM openjdk:17
WORKDIR /bfsm
COPY ./target/bfsm-1.0.jar ./bfsm-1.0.jar
EXPOSE 8080
ENV TZ=America/Sao_Paulo
ENTRYPOINT [ "java", "-jar","bfsm-1.0.jar" ]