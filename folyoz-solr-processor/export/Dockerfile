# The line below states we will base our new image on the Latest Official Ubuntu 
FROM java:8


# Identify the maintainer of an image
LABEL maintainer="mkthakral@gmail.com"

WORKDIR /

ADD ../folyoz-properties.properties folyoz-properties.properties
ADD Processor.jar Processor.jar 

EXPOSE 8080

CMD java -jar Processor.jar
