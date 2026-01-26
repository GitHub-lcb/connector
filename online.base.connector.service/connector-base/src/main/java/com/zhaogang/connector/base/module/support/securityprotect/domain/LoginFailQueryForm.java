package com.zhaogang.connector.base.module.support.securityprotect.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.zhaogang.connector.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 登录失败 分页查询表单
 *
 * @Author 连接器-主任-卓大
 * @Date 2023-10-17 18:02:37
 * @Copyright 连接器
 */

@Data
public class LoginFailQueryForm extends PageParam {

    @Schema(description = "登录名")
    private String loginName;

    @Schema(description = "锁定状态")
    private Boolean lockFlag;

    @Schema(description = "登录失败锁定时间")
    private LocalDate loginLockBeginTimeBegin;

    @Schema(description = "登录失败锁定时间")
    private LocalDate loginLockBeginTimeEnd;

}