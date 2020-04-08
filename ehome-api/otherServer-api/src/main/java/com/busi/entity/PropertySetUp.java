package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 物业人员设置
 * author ZJJ
 * Create time 2020-04-08 11:54:26
 */
@Setter
@Getter
public class PropertySetUp {

    private long id;                //主键ID

    private long propertyId;    //物业ID

    private int post;      //职位：董事长、副董事长、董事、总经理、副总经理、助理、部门经理、会计、出纳、文秘、主管、公关、前台、保安、水电维修、园林绿化、家政保洁、其他 （ 从0开始 董事长是0）

    private String head; //头像

    private String name; //姓名
}
