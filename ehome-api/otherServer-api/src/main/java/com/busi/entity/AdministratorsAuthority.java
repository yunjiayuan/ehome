package com.busi.entity;

import lombok.Getter;
import lombok.Setter;


/**
 * @program: ehome
 * @description: 管理员对应权限实体
 * @author: ZhaoJiaJie
 * @create: 2020-09-27 13:59:16
 */
@Setter
@Getter
public class AdministratorsAuthority {

    private long id;                    // 主键ID

    private int levels;                //级别：0普通管理员 1高级管理员 2最高管理员

    private  String authorityId;          // 对应权限id,逗号分隔

}
