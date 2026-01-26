package com.zhaogang.connector.base.module.support.file.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件服务
 *
 * @Author 连接器: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
@TableName(value = "t_file")
public class FileEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 文件夹类型
     */
    private Integer folderType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件key，用于文件下载
     */
    private String fileKey;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 创建人，即上传人
     */
    private Long creatorId;

    /**
     * 用户类型
     */
    private Integer creatorUserType;

    /**
     * 创建人 姓名
     */
    private String creatorName;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}

