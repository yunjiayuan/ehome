package com.busi.validator;


import com.busi.utils.CommonUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义参数验证 验证身份证
 * author：SunTianJie
 * create time：2018/6/25 20:07
 */
public class IdCardValidator implements ConstraintValidator<IdCardConstraint,Object> {
    @Override
    public void initialize(IdCardConstraint constraintAnnotation) {

    }

    /***
     * 主要验证逻辑
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value==null){
            return true;
        }
        return CommonUtils.isIdCard((String)value);
    }
}
