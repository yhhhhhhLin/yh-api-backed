
# 调用接口sdk
通过这个sdk调用，可以生成密钥，密钥可以用到接口调用的时候做权限校验

## 配置和使用

### 配置
在项目中引入这个sdk模块，在项目yml文件配置
```
yhapi:
  client:
    access-key: 
    secret-key: 
    base-url: 
```
其中access-key和secret-key在平台登录后可以获取到 base-url也可以在平台获取。

### 使用
根据平台上面需要的参数，创建一个 InterfaceParams()类，在里面传递对应参数



