package com.zhaogang.connector.admin.module.business.connector.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 密钥 Excel VO
 *
 * @author chenbo.li
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectorSecretKeyExcelVO {

    @ExcelProperty("ID")
    private Long keyId;

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("Access Key")
    private String accessKey;

    @ExcelProperty("Secret Key")
    private String secretKey;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
