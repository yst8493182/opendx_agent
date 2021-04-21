package com.daxiang.action;

import com.daxiang.core.action.annotation.Action;
import com.daxiang.core.action.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by jiangyitao.
 * id: 5000 - 10000
 */
//note by yifeng, 这个Action 函数有点问题，先不要在这里填写
@Slf4j
public class YourCustomAction {

    @Action(id = 5000, name = "[通用]随机字符串（数字 & 字母）")
    public static String randomAlphanumeric(@Param(description = "字符串长度") int count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

}
