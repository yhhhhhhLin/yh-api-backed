# api平台微服务版本

> 开发日志1: [开发日志](https://www.yuque.com/u28887180/qlq65u/gvdo85496tegubcr?singleDoc#)，
> 开发日志2： [开发日志](https://iakbbhkfqkz.feishu.cn/wiki/U9fuw3ATXimiy8kQqK6cC1gonDh?from=from_copylink)
> 接口文档： [接口文档](https://apifox.com/apidoc/shared-7f313213-c284-43ca-afe4-3f5e5fed92a5/api-152526145)

## 部署
利用docker进行部署前端和后端
部署直接将仓库根路径下的docker-compose.yml和init目录下的所有文件复制到自己电脑
利用以下命令可启动项目所需所有docker
```
sudo docker-compose up
```

如果需要初始化数据，可以在init目录下添加对应sql语句
```sql
INSERT INTO api_pay.creditproducts (id, description, price, integral, picture, discountPrice, createTime, updateTime, isDelete) VALUES (1, '获得100积分', 1, 100, '无', 1, '2023-12-26 15:49:52', '2023-12-26 15:49:52', 0);
```

## 主要功能
可以将自己的接口上传到平台上面，路由实现了根据不同接口地址跳转到不同接口的功能

发布者可以管理自己发布的接口和查看接口调用次数，管理员可以管理所有接口

可以在线调用发布的接口，后续会提供sdk文件，可以实现在自己代码里面调用到接口

实现简单支付宝沙箱，实现支付功能

## todo









