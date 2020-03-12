package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: ehome
 * @description: 医生、律师咨询信息实体
 * @author: ZHaoJiaJie
 * @create: 2020-03-12 14:09:45
 */
@Setter
@Getter
public class ConsultationFee {

    private long id;                    // 主键ID

    private int occupation;          // 职业：0医生  1律师

    private int title;            // 职称：（occupation=0时 0副主任 主任 专家  1其他）  （occupation=1时 0初级律师 1中级律师  2高级律师）

    private int type;           // 咨询类型：0语音、视频  1图文

    private int duration;       //时长：5分钟  15分钟 30分钟  60分钟

    private double cost;           //费用：

}
