package com.zhaogang.connector.base.module.support.apiencrypt.service;

/**
 * 接口加密、解密 Service
 *
 * @Author 连接器-主任:卓大
 * @Date 2023/10/21 11:41:46
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */

public interface ApiEncryptService {

    /**
     * 解密
     * @param data
     * @return
     */
    String decrypt(String data);

    /**
     * 加密
     *
     * @param data
     * @return
     */
    String encrypt(String data);

}
