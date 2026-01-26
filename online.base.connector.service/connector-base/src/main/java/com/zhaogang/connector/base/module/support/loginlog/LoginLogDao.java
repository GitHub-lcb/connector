package com.zhaogang.connector.base.module.support.loginlog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaogang.connector.base.module.support.loginlog.domain.LoginLogEntity;
import com.zhaogang.connector.base.module.support.loginlog.domain.LoginLogQueryForm;
import com.zhaogang.connector.base.module.support.loginlog.domain.LoginLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录日志
 *
 * @Author 连接器-主任: 卓大
 * @Date 2022/07/22 19:46:23
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */
@Mapper
public interface LoginLogDao extends BaseMapper<LoginLogEntity> {

    /**
     * 分页查询
     *
     * @param page
     * @param queryForm
     * @return LoginLogVO
     */
    List<LoginLogVO> queryByPage(Page page, @Param("query") LoginLogQueryForm queryForm);

    /**
     * 查询上一个登录记录
     *
     * @param userId
     * @param userType
     * @return LoginLogVO
     */
    LoginLogVO queryLastByUserId(@Param("userId") Long userId,@Param("userType") Integer userType, @Param("loginLogResult")Integer loginLogResult);


}
