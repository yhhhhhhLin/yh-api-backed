package ${groupName}.${artifactName};

<#if needMybatisPlus>
import org.mybatis.spring.annotation.MapperScan;
</#if>
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
*
* @author ${authorName}
*/
@SpringBootApplication
<#if needMybatisPlus>
@MapperScan("${groupName}.${artifactName}.mapper")
</#if>
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
