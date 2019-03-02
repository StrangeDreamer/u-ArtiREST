# micro-artirest
每个文件夹均为一个独立服务

本地运行方法：

数据库：MongoDB3.4及以下版本
- [安装教程](https://blog.csdn.net/u013066244/article/details/53838721)

kafka：2.11-1.0.0版本，于根目录下依次运行
1. bin/zookeeper-server-start.sh config/zookeeper.properties
2. bin/kafka-server-start.sh config/server.properties
- [安装教程](https://blog.csdn.net/qq_35921007/article/details/80637666)
*注：[kafka_2.11-1.0.0下载](https://archive.apache.org/dist/kafka/1.0.0/kafka_2.11-1.0.0.tgz)*

服务启动：于各服务根目录下运行./mvwn（console请直接运行docker-compose up）

务必按照jhipster-registry -> uaa -> 其它服务的顺序启动
