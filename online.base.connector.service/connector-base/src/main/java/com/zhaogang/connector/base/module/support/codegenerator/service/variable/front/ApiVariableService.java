package com.zhaogang.connector.base.module.support.codegenerator.service.variable.front;

import com.zhaogang.connector.base.module.support.codegenerator.domain.form.CodeGeneratorConfigForm;
import com.zhaogang.connector.base.module.support.codegenerator.service.variable.CodeGenerateBaseVariableService;

import java.util.*;

/**
 * @Author 连接器-主任:卓大
 * @Date 2022/9/29 17:20:41
 * @Wechat zhuoda1024

 * @Copyright  <a href="https://www.zhaogang.com">连接器</a>
 */

public class ApiVariableService extends CodeGenerateBaseVariableService {

    @Override
    public boolean isSupport(CodeGeneratorConfigForm form) {
        return true;
    }

    @Override
    public Map<String, Object> getInjectVariablesMap(CodeGeneratorConfigForm form) {
        Map<String, Object> variablesMap = new HashMap<>();

        return variablesMap;
    }
}
