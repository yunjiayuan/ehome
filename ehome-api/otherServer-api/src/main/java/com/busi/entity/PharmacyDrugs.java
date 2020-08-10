package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * @program: ehome
 * @description: 药品实体
 * @author: ZhaoJiaJie
 * @create: 2020-07-29 10:41:32
 */
@Setter
@Getter
public class PharmacyDrugs {

    private long id;                    // ID

    private long userId;                // 商家ID

    private long pharmacyId;          // 药房ID

    private String name;       //名称

    private String describes;       //药品说明：请详细描述药品的主治功能、用法用量、注意事项、有效期、生产厂家等

    private String picture;        //图片

    private double cost;                // 价格

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;            // 添加时间

    private int prescriptionType;            // 处方分类 0处方药 1非处方药

    private int natureType;            // 药品性质分类：中药材、中药饮片、中成药、中西成药，化学原料药及其制剂、抗生素、生化药品、放射性药品、血清、疫苗、血液制品、诊断药品、保健药品、其他药品

    private String specifications;            // 药品规格：如：50g/片

    private int company;            // 药品出售单位：单选 瓶、盒、桶、箱、袋、剂

    private int numbers;            // 药品出售数量：选择数字1-100

    private int deleteType;            // 删除标志:0未删除,1用户删除,2管理员删除
}
