package xyz.linyh.model.apiaudit.eneitys;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName ApiInterfaceAudit
 */
@TableName(value ="ApiInterfaceAudit")
@Data
@Getter
@Setter
public class ApiInterfaceAudit implements Serializable {
    private Long id;

    private Long apiId;

    private String name;

    private String apiDescription;

    private String uri;

    private String host;

    private String method;

    /**
     * 需要花费的积分
     */
    private Integer pointsRequired;

    private String requestHeader;

    private String responseHeader;

    private String requestParams;

    private String getRequestParams;

//    发布用户id
    private Long userId;

    /**
     * 审核状态 1 提交（还没审核） 2 gpt审核失败 3 gpt审核成功 4 人工审核中 5 人工审核通过 6 审核不通过 9 已经发布';
     */
    private Integer status;

//    审核建议
    private String description;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;


}