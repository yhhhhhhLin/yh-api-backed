# api平台微服务版本

> 语雀日志: [开发日志](https://www.yuque.com/u28887180/qlq65u/gvdo85496tegubcr?singleDoc#)

## 部署
利用docker进行部署前端和后端
部署直接将仓库根路径下的docker-compose.yml和init目录下的所有文件复制到自己电脑
利用以下命令可启动项目所需所有docker
```
sudo docker-compose up
```

如果需要初始化数据，可以在init在init目录下添加对应sql语句
```sql

```

## 主要功能
可以将自己的接口上传到平台上面，路由实现了根据不同接口地址跳转到不同接口的功能

发布者可以管理自己发布的接口和查看接口调用次数，管理员可以管理所有接口

可以在线调用发布的接口，后续会提供sdk文件，可以实现在自己代码里面调用到接口








