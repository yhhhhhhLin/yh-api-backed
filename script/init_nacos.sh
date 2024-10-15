#!/bin/bash

NACOS_HOST="nacos"
NACOS_PORT=8848
NAMESPACE="yhapi-default"

# nacos配置文件名
DEFAULT_CONFIG_PATH="nacos_config.zip"


    CONFIG_PATH="$DEFAULT_CONFIG_PATH"

# 在nacos上增加命名空间配置
curl -X POST "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/console/namespaces" -d "customNamespaceId=$NAMESPACE&namespaceName=$NAMESPACE&namespaceDesc=yhapi default config"

echo $CONFIG_PATH
echo 1234
# 将配置写到nacos
curl --location --request POST "http://$NACOS_HOST:$NACOS_PORT/nacos/v1/cs/configs?import=true&namespace=$NAMESPACE" --form "policy=OVERWRITE" --form "file=@$CONFIG_PATH"