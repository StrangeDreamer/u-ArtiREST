# micro-artirest
每个文件夹均为一个独立服务

本地运行方法：

数据库：MongoDB3.4及以下版本

kafka：2.11-1.0.0版本，于根目录下依次运行
1. bin/zookeeper-server-start.sh config/zookeeper.properties
2. bin/kafka-server-start.sh config/server.properties

服务启动：于各服务根目录下运行./mvwn（console请直接运行docker-compose up）

务必按照jhipster-registry -> uaa -> 其它服务的顺序启动
