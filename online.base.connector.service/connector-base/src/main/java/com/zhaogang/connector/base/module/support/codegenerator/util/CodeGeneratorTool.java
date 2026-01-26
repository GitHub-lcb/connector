package com.zhaogang.connector.base.module.support.codegenerator.util;

import com.google.common.base.CaseFormat;
import com.zhaogang.connector.base.common.constant.StringConst;

/**
 * 代码生成 velocity 工具类
 *
 * @Author 连接器-主任:卓大
 * @Date 2022/9/30 19:02:17
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */

public class CodeGeneratorTool {

    /**
     * 小写驼峰，转为大写驼峰
     */
    public String lowerCamel2UpperCamel(String str) {
        if (str == null) {
            return StringConst.EMPTY;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, str);
    }

    /**
     * 小写驼峰，转为小写中划线
     */
    public String lowerCamel2LowerHyphen(String str) {
        if (str == null) {
            return StringConst.EMPTY;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
    }


    /**
     * 去掉 注释 枚举类型
     */
    public String removeEnumDesc(String str) {
        if (str == null) {
            return StringConst.EMPTY;
        }

        int index = str.indexOf("[");
        if (index == -1) {
            index = str.indexOf("【");
        }

        if (index == -1) {
            return str;
        }

        return str.substring(0, index - 1);
    }

}
