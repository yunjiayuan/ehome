package com.busi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =IdCardValidator.class )
public @interface IdCardConstraint {
    String message() default "身份证号格式有误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
