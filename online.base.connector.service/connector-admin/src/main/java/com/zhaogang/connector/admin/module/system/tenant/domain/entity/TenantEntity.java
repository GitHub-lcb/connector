package com.zhaogang.connector.admin.module.system.tenant.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_tenant")
public class TenantEntity {
    @TableId(type = IdType.AUTO)
    private Long tenantId;
    private String tenantName;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
