package ${basePackage}.generator.model;

import lombok.Data;

/**
* 生成需要的实体类
* @author ${author}
*/
@Data
public class DataModel {

<#list modelConfig.models as modelInfo>

    /**
    * ${modelInfo.description}
    */
    public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = <#if modelInfo.type=='boolean'>${modelInfo.defaultValue?c} <#else>"${modelInfo.defaultValue}"</#if></#if>;


</#list>
}
