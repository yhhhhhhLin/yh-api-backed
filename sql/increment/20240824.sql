alter table yhapi.interfaceInfo
    add interfaceType tinyint default 1 not null comment '接口类型->interfaceTypeEnum';

alter table yhapi.interfaceInfo
    add dscId int default -1 not null comment '数据源id 没有的话为-1';