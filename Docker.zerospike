FROM alpine:3.6
MAINTAINER Ben Faul, https://github.com/benmfaul
RUN apk --update add openjdk8-jre
RUN apk add --no-cache bash

RUN mkdir shell
RUN mkdir www
RUN mkdir web
RUN mkdir target
RUN mkdir data
RUN mkdir logs
RUN mkdir SampleBids
RUN mkdir Campaigns

COPY stub.json /stub.json
COPY target/*with-dependencies.jar /target

COPY wait-for-it.sh /
COPY tools/* /
COPY data/ /data
COPY www /www
COPY log4j.properties /

EXPOSE 6000 6001 6002 7001

CMD ./zerospike
