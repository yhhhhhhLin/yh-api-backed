package xyz.linyh.model.interfaceinfo.entitys;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息表
 * @TableName interfaceinfo
 */
@TableName(value ="interfaceInfo")
@Data
public class Interfaceinfo implements Serializable {
    /**
     * 接口id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口名称
     */
    private String name;


    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParams;


    /**
     * get请求参数
     */
    private String getRequestParams;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 需要花费的积分
     */
    private Integer pointsRequired;

    /**
     * 接口uri
     */
    private String uri;

    /**
     * 接口host
     */
    private String host;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应信息
     */
    private String responseHeader;

    /**
     * 接口状态 0为不可用 1为可用 2为待审核中 3为需要修改信息重新提交审核
     */
    private Integer status;

    /**
     * 接口创建者id
     */
    private Long userId;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0为没删除 1为删除
     */
    @TableLogic//逻辑删除
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Interfaceinfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", getRequestParams='" + getRequestParams + '\'' +
                ", description='" + description + '\'' +
                ", uri='" + uri + '\'' +
                ", host='" + host + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", responseHeader='" + responseHeader + '\'' +
                ", status=" + status +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interfaceinfo)) {
            return false;
        }

        Interfaceinfo that = (Interfaceinfo) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        if (getMethod() != null ? !getMethod().equals(that.getMethod()) : that.getMethod() != null) {
            return false;
        }
        if (getRequestParams() != null ? !getRequestParams().equals(that.getRequestParams()) : that.getRequestParams() != null)
        {
            return false;
        }
        if (getGetRequestParams() != null ? !getGetRequestParams().equals(that.getGetRequestParams()) : that.getGetRequestParams() != null)
        {
            return false;
        }
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
        {
            return false;
        }
        if (getUri() != null ? !getUri().equals(that.getUri()) : that.getUri() != null) {
            return false;
        }
        if (getHost() != null ? !getHost().equals(that.getHost()) : that.getHost() != null) {
            return false;
        }
        if (getRequestHeader() != null ? !getRequestHeader().equals(that.getRequestHeader()) : that.getRequestHeader() != null) {
            return false;
        }
        if (getResponseHeader() != null ? !getResponseHeader().equals(that.getResponseHeader()) : that.getResponseHeader() != null) {
            return false;
        }
        if (getStatus() != null ? !getStatus().equals(that.getStatus()) : that.getStatus() != null) {
            return false;
        }
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null) {
            return false;
        }
        if (getCreateTime() != null ? !getCreateTime().equals(that.getCreateTime()) : that.getCreateTime() != null) {
            return false;
        }
        if (getUpdateTime() != null ? !getUpdateTime().equals(that.getUpdateTime()) : that.getUpdateTime() != null) {
            return false;
        }
        return getIsDelete() != null ? getIsDelete().equals(that.getIsDelete()) : that.getIsDelete() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + (getRequestParams() != null ? getRequestParams().hashCode() : 0);
        result = 31 * result + (getGetRequestParams() != null ? getGetRequestParams().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getUri() != null ? getUri().hashCode() : 0);
        result = 31 * result + (getHost() != null ? getHost().hashCode() : 0);
        result = 31 * result + (getRequestHeader() != null ? getRequestHeader().hashCode() : 0);
        result = 31 * result + (getResponseHeader() != null ? getResponseHeader().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        result = 31 * result + (getUpdateTime() != null ? getUpdateTime().hashCode() : 0);
        result = 31 * result + (getIsDelete() != null ? getIsDelete().hashCode() : 0);
        return result;
    }
}