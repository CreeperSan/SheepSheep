package com.guuda.sheep.exception;

import com.guuda.sheep.utils.FormatUtils;

public class ParamsInvalidException extends Throwable {
    String paramsName = "";

    public ParamsInvalidException(String paramsName) {
        this.paramsName = paramsName;
    }

    public String getParamsName() {
        if (FormatUtils.isEmpty(paramsName)) {
            return "参数不合法";
        }
        return paramsName + " 不合法";
    }
}
