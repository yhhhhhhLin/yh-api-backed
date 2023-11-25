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



INSERT INTO yhapi.user (id, userName, userAccount, userAvatar, gender, userRole, userPassword, accessKey, secretKey, createTime, updateTime, isDelete) VALUES (1, 'root', 'root', 'https://cn.bing.com/images/search?view=detailV2&ccid=qziNmxyw&id=1E81246A717B63ACACD809FAA09D269FF34BBF84&thid=OIP.qziNmxywXqaT7lHXOgLJqgAAAA&mediaurl=https%3a%2f%2fp.qqan.com%2fup%2f2020-7%2f2020072017382224891.jpg&exph=400&expw=400&q=%e5%a4%b4%e5%83%8f&simid=607995953255237553&FORM=IRPRST&ck=9091D49B62444EACC536D2505651DB08&selectedIndex=15', 0, 'admin', '8d2edc95f411c2b98e66c0010ff54d3a', 'testak', 'testsk', '2023-09-04 19:03:38', '2023-09-25 20:22:30', 0);


