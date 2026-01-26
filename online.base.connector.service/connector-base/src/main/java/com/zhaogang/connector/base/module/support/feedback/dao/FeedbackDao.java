package com.zhaogang.connector.base.module.support.feedback.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.base.module.support.feedback.domain.FeedbackEntity;
import com.zhaogang.connector.base.module.support.feedback.domain.FeedbackQueryForm;
import com.zhaogang.connector.base.module.support.feedback.domain.FeedbackVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 意见反馈 dao
 *
 * @Author 连接器: 开云
 * @Date 2022-08-11 20:48:09
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Mapper
public interface FeedbackDao extends BaseMapper<FeedbackEntity> {

    /**
     * 分页查询
     */
    List<FeedbackVO> queryPage(Page page, @Param("query") FeedbackQueryForm query);
}