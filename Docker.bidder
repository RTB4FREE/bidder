FROM alpine:3.6
MAINTAINER Ben Faul, https://github.com/benmfaul
RUN apk --update add openjdk8-jre
RUN apk add --no-cache bash
RUN apk --update add curl

RUN mkdir shell
RUN mkdir www
RUN mkdir www/js
RUN mkdir www/css
RUN mkdir www/SSI
RUN mkdir www/jsoneditor
RUN mkdir www/images
RUN mkdir www/assets
RUN mkdir web
RUN mkdir js
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
COPY shell/ /shell

COPY www/* /www/
COPY www/assets/ /www/assets
COPY www/js/* /www/js/
COPY www/css/* /www/css/
COPY www/SSI /www/SSI/
COPY www/jsoneditor/ /www/jsoneditor
COPY www/images/320* /www/images/
copy www/images/alien* www/images/

COPY web/* /web/
COPY log4j.properties /
COPY SampleBids /SampleBids

COPY database.json /

COPY Campaigns/docker.json Campaigns/payday.json

COPY sendbid /

EXPOSE 8080 7379 7000

CMD ./rtb4free
