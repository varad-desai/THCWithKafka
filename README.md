# THCWithKafka
Texas Hamburger Company

### Commands to set JAVA HOME path in new terminal 
```
export PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

```

### How to run Kafka
```
# 1. Start Zookeeper in terminal 1==>
bin/zookeeper-server-start.sh config/zookeeper.properties

# 2. Start Kafka Server in terminal 2 ==>
bin/kafka-server-start.sh config/server.properties

# 3. List Kafka topics in terminal 3 ==>
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# 4. Create a topic in Kafka Cluster ==>
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 2 --topic thc-topic
```

### Execute Docker 

```
# To start container services
docker-compose --build
docker-compose up --build

# To start container services
docker-compose up -d

# To list all active containers
docker ps
```

### MongoDB 

```
service mongodb status

service mongodb stop

service mongodb start
```
