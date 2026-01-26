package com.zhaogang.connector.base.module.support.config.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配置信息
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022-03-14 20:46:27
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class ConfigVO {
    @Schema(description = "主键")
    private Long configId;

    @Schema(description = "参数key")
    private String configKey;

    @Schema(description = "参数的值")
    private String configValue;

    @Schema(description = "参数名称")
    private String configName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "上次修改时间")
    private LocalDateTime updateTime;
}
