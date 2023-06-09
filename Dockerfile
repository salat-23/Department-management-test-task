FROM ubuntu:20.04
WORKDIR /var/app
COPY build/libs/*.jar app.jar
RUN apt update && apt upgrade -y && apt install -y openjdk-17-jdk libgbm1 libxkbcommon0 && apt clean && apt autoclean
ENTRYPOINT ["java","-jar","app.jar"]
# ubuntu is used because there are rare instances of libraries
# that need to donwload specific-OS binaries, which wont work without full-fledged os