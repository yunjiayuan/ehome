package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 居委会人员设置
 * author ZJJ
 * Create time 2020-03-18 10:46:33
 */
@Setter
@Getter
public class CommunitySetUp {
    private long id;                //主键ID

    private long communityId;    //居委会ID

    private int post;      //职务:0书记 1主任 2副主任 3助理 4文书 5干事 6计生员 7社保协管员 8低保协管员 9卫生员 10治安员 11其他

    private String head; //头像

    private String name; //姓名
}
