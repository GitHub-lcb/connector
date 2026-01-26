package com.zhaogang.connector.base.module.support.reload.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * t_reload_item 数据表 实体类
 *
 * @Author 连接器-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Data
@TableName("t_reload_item")
public class ReloadItemEntity {

    /**
     * 加载项标签
     */
    @TableId(type = IdType.INPUT)
    private String tag;

    /**
     * 参数
     */
    private String args;

    /**
     * 运行标识
     */
    private String identification;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
