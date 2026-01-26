package com.zhaogang.connector.base.module.support.file.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件信息
 *
 * @Author 连接器: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
public class FileUploadVO {

    @Schema(description = "文件id")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "fileUrl")
    private String fileUrl;

    @Schema(description = "fileKey")
    private String fileKey;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;
}
