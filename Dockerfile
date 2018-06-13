FROM maven:3.5.3-jdk-8
WORKDIR /usr/src/
COPY . .
ARG URL
ARG USERNAME
ARG PASSWORD
ARG SCHEMA
RUN mvn clean package
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs
RUN npm install serverless
RUN npm install serverless-pseudo-parameters
CMD ["bash"]