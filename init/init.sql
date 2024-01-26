create database if not exists yhapi;
use yhapi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    credits       int          default 0                 null comment '余额',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    accessKey    varchar(512)                           null comment 'accessKey',
    secretKey    varchar(512)                           null comment 'secretKey',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户';


create table yhapi.interfaceInfo
(
    id               bigint auto_increment comment '接口id'
        primary key,
    name             varchar(255)                       not null comment '接口名称',
    method           varchar(255)                       not null comment '请求方法',
    description      varchar(255)                       null comment '接口描述',
    uri          varchar(255)                       not null comment '接口地址',
    host              varchar(255)                       not null comment '接口地址',
    requestHeader    text                               null comment '请求头',
    responseHeader   text                               null comment '响应信息',
    status           tinyint  default 1                 null comment '接口状态 1为可用 0为不可用',
    userId           bigint                             null comment '接口创建者id',
    pointsRequired   int                                not null comment '调用这个接口需要花费的积分',
    isDelete         tinyint  default 0                 not null comment '是否删除 0为没删除 1为删除',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    requestParams    text                               null comment '请求体参数',
    getRequestParams text                               null comment 'get请求参数',
    constraint id
        unique (id)
)
    comment '接口信息表';



create table yhapi.userInterfaceinfo
(
    id          bigint auto_increment comment 'id'
        primary key,
    userId      bigint                             not null comment '用户id',
    interfaceId bigint                             not null comment '接口id',
    remNum      int      default 10                not null comment '剩余调用次数',
    allNum      int      default 0                 not null comment '共调用多少次',
    status      tinyint  default 1                 null comment '用户是否能调用这个接口 1为可用 0为不可用',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    constraint id
        unique (id)
)
    comment '用户接口调用次数关系表';


create table yhapi.sdkfile
(
    id          bigint auto_increment comment 'id'
        primary key,
    userId      bigint                             not null comment '上传者id',
    name        varchar(255)           unique      not null comment 'sdk对应文件名称',
    filepath    varchar(255)           unique      not null comment 'sdk对应文件路径',
    description varchar(255)                                comment '更新介绍',
    num         int      default 0                 not null comment '对应下载次数',
    status      tinyint  default 1                 null comment '这个sdk是否可用 1为可用 0为不可用',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    constraint id
        unique (id)
)
    comment 'sdk版本管理表';


-- 审核模块数据库表结构
-- 创建对应数据库
create database if not exists api_audit;

-- 使用相关数据库
use api_audit;

-- 创建对应需要的表结构

-- api接口审核表
create table if not exists ApiInterfaceAudit
(
    id          bigint auto_increment comment 'id'
        primary key,
    apiId      bigint                                      comment '接口id',
    name    varchar(255)                            not null comment '接口名称',
    apiDescription varchar(255)                            comment '接口描述',
    uri     varchar(255)                       not null comment '接口uri',
    host    varchar(255)                       not null comment '接口host',
    method  varchar(255)                       not null comment '接口请求方法',
    pointsRequired int   default 0             not null comment '需要多少积分',
    requestHeader text                            null comment '接口请求头',
    responseHeader text                           null comment '接口响应头',
    requestParams text                            null comment '接口post请求参数',
    getRequestParams text                        null comment '接口请求体参数',
    userId     bigint                             not null comment '用户id',
    status      tinyint  default 0                 not null comment ' 审核状态 1 提交（还没审核） 2 gpt审核失败 3 gpt审核成功 4 人工审核中 5 人工审核通过 6 审核不通过 9 已经发布',
    description varchar(255)                       null comment '审核描述',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    constraint id
        unique (id)
)
    comment 'api接口审核表';

create database if not exists api_pay;

use api_pay;

create table if not exists CreditProducts
(
    id            bigint auto_increment comment 'id'
        primary key,
    description   varchar(255) comment '商品描述',
    price         int                                not null comment '商品价格',
    integral      int                                not null comment '商品对应多少积分',
    picture       varchar(255)                       not null comment '商品对应图片',
    discountPrice int                                not null comment '打折后的价格',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    constraint id
        unique (id)
)
    comment '积分商品表';


create table if not exists UserCredits
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    credit     int                                not null comment '用户剩余多少积分',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    constraint id
        unique (id)
)
    comment '用户剩余积分表';


# 产品订单
create table if not exists ProductsOrder
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           not null comment '订单号',
    codeUrl        varchar(256)                           null comment '二维码地址',
    userId         bigint                                 not null comment '创建人',
    productId      bigint                                 not null comment '商品id',
    orderName      varchar(256)                           not null comment '商品名称',
    total          bigint                                 not null comment '金额(分)',
    status         varchar(256) default 'NOTPAY'          not null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(256) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    productInfo    text                                   null comment '商品信息',
    formData       text                                   null comment '支付宝formData',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '商品订单';
INSERT INTO yhapi.user (id, userName, userAccount, userAvatar, gender, userRole, userPassword, accessKey, secretKey, createTime, updateTime, isDelete, credits) VALUES (1, 'root', 'root', 'https://cn.bing.com/images/search?view=detailV2&ccid=qziNmxyw&id=1E81246A717B63ACACD809FAA09D269FF34BBF84&thid=OIP.qziNmxywXqaT7lHXOgLJqgAAAA&mediaurl=https%3a%2f%2fp.qqan.com%2fup%2f2020-7%2f2020072017382224891.jpg&exph=400&expw=400&q=%e5%a4%b4%e5%83%8f&simid=607995953255237553&FORM=IRPRST&ck=9091D49B62444EACC536D2505651DB08&selectedIndex=15', 0, 'admin', '8d2edc95f411c2b98e66c0010ff54d3a', 'testak', 'testsk', '2023-09-04 19:03:38', '2024-01-21 22:57:16', 0, 100);

INSERT INTO api_pay.creditproducts (id, description, price, integral, picture, discountPrice, createTime, updateTime, isDelete) VALUES (1, '获得100积分', 1, 100, '无', 1, '2023-12-26 15:49:52', '2023-12-26 15:49:52', 0);
INSERT INTO api_pay.creditproducts (id, description, price, integral, picture, discountPrice, createTime, updateTime, isDelete) VALUES (2, '获得1100积分', 9.9, 1100, '无', 9.9, '2023-12-26 15:49:52', '2023-12-26 15:49:52', 0);
INSERT INTO api_pay.creditproducts (id, description, price, integral, picture, discountPrice, createTime, updateTime, isDelete) VALUES (3, '获得3500积分', 27.9, 3500, '无', 27.9, '2023-12-26 15:49:52', '2023-12-26 15:49:52', 0);
INSERT INTO api_pay.creditproducts (id, description, price, integral, picture, discountPrice, createTime, updateTime, isDelete) VALUES (4, '获得5000积分', 40, 5000, '无', 40, '2023-12-26 15:49:52', '2023-12-26 15:49:52', 0);


