FROM openjdk:8
COPY . /usr/src/inframind
WORKDIR /usr/src/inframind
EXPOSE 9090
CMD [ "sh", "target/bin/webapp" ]
