package com.zhaogang.connector.base.module.support.mail;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaogang.connector.base.module.support.mail.domain.MailTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 *
 * @Author 连接器-创始人兼主任:卓大
 * @Date 2024/8/5
 * @Wechat zhuoda1024

 * @Copyright <a href="https://www.zhaogang.com">连接器</a> ，Since 2012
 */
@Mapper
public interface MailTemplateDao extends BaseMapper<MailTemplateEntity> {

}
