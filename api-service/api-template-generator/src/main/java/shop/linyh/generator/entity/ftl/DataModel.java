package shop.linyh.generator.entity.ftl;

import lombok.Data;

/**
* 生成需要的实体类
* @author linyh
*/
@Data
public class DataModel {


    /**
    * 是否需要post实例代码(创建post的数据库表)
    */
    public boolean needPostExample;



    /**
    * 是否需要user实例和一些权限校验代码(创建User的数据库表)
    */
    public boolean needUserExample;



    /**
    * 项目包名
    */
    public String groupName  = "shop.linyh";



    /**
    * 项目artifact名
    */
    public String artifactName  = "template";



    /**
    * 项目名
    */
    public String projectName  = "template";



    /**
    * 是否需要跨域文件
    */
    public boolean needCors;



    /**
    * 是否需要springboot web依赖框架
    */
    public boolean needBootWeb;



    /**
    * 是否需要springboot aop依赖
    */
    public boolean needBootAop;



    /**
    * 是否需要MybatisPlus依赖
    */
    public boolean needMybatisPlus;



    /**
    * 是否需要Mybatis依赖
    */
    public boolean needMybatis;



    /**
    * 是否需要Mysql依赖
    */
    public boolean needMysql;



    /**
    * 是否需要Redis依赖
    */
    public boolean needRedis;



    /**
    * 是否需要Gson依赖
    */
    public boolean needGson;



    /**
    * 是否需要FastJson依赖
    */
    public boolean needFastJson;



    /**
    * 是否需要Hutool依赖
    */
    public boolean needHutool;



    /**
    * 是否需要Knife4j依赖
    */
    public boolean needKnife4j;



    /**
    * 是否需要lombok依赖
    */
    public boolean needLombok;

    /**
     * 作者名字
     */
    public String authorName;
}
