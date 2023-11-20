package xyz.linyh.model.apiaudit.eneitys;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName ApiInterfaceAudit
 */
@TableName(value ="ApiInterfaceAudit")
@Data
public class ApiInterfaceAudit implements Serializable {
    private Long id;

    private Long apiId;

    private String name;

    private String apiDescription;

    private String uri;

    private String host;

    private String method;

    private String requestheader;

    private String responseheader;

    private String requestparams;

    private String getrequestparams;

    private Long userId;

    private Integer status;

    private String description;

    private Date createtime;

    private Date updatetime;

    private Integer isdelete;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ApiInterfaceAudit other = (ApiInterfaceAudit) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApiId() == null ? other.getApiId() == null : this.getApiId().equals(other.getApiId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getApiDescription() == null ? other.getApiDescription() == null : this.getApiDescription().equals(other.getApiDescription()))
            && (this.getUri() == null ? other.getUri() == null : this.getUri().equals(other.getUri()))
            && (this.getHost() == null ? other.getHost() == null : this.getHost().equals(other.getHost()))
            && (this.getMethod() == null ? other.getMethod() == null : this.getMethod().equals(other.getMethod()))
            && (this.getRequestheader() == null ? other.getRequestheader() == null : this.getRequestheader().equals(other.getRequestheader()))
            && (this.getResponseheader() == null ? other.getResponseheader() == null : this.getResponseheader().equals(other.getResponseheader()))
            && (this.getRequestparams() == null ? other.getRequestparams() == null : this.getRequestparams().equals(other.getRequestparams()))
            && (this.getGetrequestparams() == null ? other.getGetrequestparams() == null : this.getGetrequestparams().equals(other.getGetrequestparams()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
            && (this.getIsdelete() == null ? other.getIsdelete() == null : this.getIsdelete().equals(other.getIsdelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getApiId() == null) ? 0 : getApiId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getApiDescription() == null) ? 0 : getApiDescription().hashCode());
        result = prime * result + ((getUri() == null) ? 0 : getUri().hashCode());
        result = prime * result + ((getHost() == null) ? 0 : getHost().hashCode());
        result = prime * result + ((getMethod() == null) ? 0 : getMethod().hashCode());
        result = prime * result + ((getRequestheader() == null) ? 0 : getRequestheader().hashCode());
        result = prime * result + ((getResponseheader() == null) ? 0 : getResponseheader().hashCode());
        result = prime * result + ((getRequestparams() == null) ? 0 : getRequestparams().hashCode());
        result = prime * result + ((getGetrequestparams() == null) ? 0 : getGetrequestparams().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getIsdelete() == null) ? 0 : getIsdelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", apiId=").append(apiId);
        sb.append(", name=").append(name);
        sb.append(", apiDescription=").append(apiDescription);
        sb.append(", uri=").append(uri);
        sb.append(", host=").append(host);
        sb.append(", method=").append(method);
        sb.append(", requestheader=").append(requestheader);
        sb.append(", responseheader=").append(responseheader);
        sb.append(", requestparams=").append(requestparams);
        sb.append(", getrequestparams=").append(getrequestparams);
        sb.append(", userId=").append(userId);
        sb.append(", status=").append(status);
        sb.append(", description=").append(description);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", isdelete=").append(isdelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}