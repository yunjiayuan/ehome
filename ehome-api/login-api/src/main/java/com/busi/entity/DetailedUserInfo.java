package com.busi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/***
 * 用户详细信息实体类
 * author：SunTianJie
 * create time：2018/6/25 9:40
 */
@Setter
@Getter
public class DetailedUserInfo {

    private long id;//主键ID
    private long userId;//用户ID

    @Max(value = 9, message = "相貌标签--相貌类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "相貌标签--相貌类型参数值有误，超出有效范围")
    private int appearanceTag;//相貌标签--相貌类型 0未选择,1屌丝,2文质彬彬,3西部牛仔,4阳光帅气,5风度翩翩,6成熟魅力,7健壮高大,8朴实无华,9内敛酷男

    @Max(value = 6, message = "相貌标签--体型类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "相貌标签--体型类型参数值有误，超出有效范围")
    private int somatotypes;//相貌标签--体型类型 0未选择,1苗条,2匀称,3高挑,4丰满,5健壮,6魁梧

    @Max(value = 7, message = "相貌标签--身高类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "相貌标签--身高类型参数值有误，超出有效范围")
    private int height;//相貌标签--身高类型 0未选择,1 139以下,2 140-149,3 150-159,4 160-169,5 170-179,6 180-189,7 190以上

    @Max(value = 8, message = "相貌标签--脸型类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "相貌标签--脸型类型参数值有误，超出有效范围")
    private int feature;//相貌标签--脸型类型 0未选择,1圆型形,2方脸型,3长脸型,4瓜子脸型,5鸭蛋脸型,6国字脸型,7三角脸型,8菱形脸型

    @Max(value = 9, message = "相貌标签--发色类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "相貌标签--发色类型参数值有误，超出有效范围")
    private int haircolor;//相貌标签--发色类型 0未选择,1黑色头发,2金色头发,3褐色头发,4栗色头发,5灰色头发,6红色头发,7白色头发,8挑染,9光头

    @Length(max = 140, message = "自我介绍参数introduction字数超时限制范围")
    private String introduction;//相貌标签--自我介绍

    @Max(value = 12, message = "生活态度--脾气秉性参数值有误，超出有效范围")
    @Min(value= 0 ,message= "生活态度--脾气秉性参数值有误，超出有效范围")
    private int temper;//生活态度--脾气秉性  0未选择,1外向豪放,2精明睿智,3乐观积极,4悲观消极,5温文尔雅,6童心未混,7仔细谨慎,8感性冲动,9稳重顾家,10风趣幽默,11浪漫迷人,12时尚男型

    @Max(value = 10, message = "生活态度--生活方式参数值有误，超出有效范围")
    @Min(value= 0 ,message= "生活态度--生活方式参数值有误，超出有效范围")
    private int lifeStyle;//生活态度--生活方式 0未选择,1购物,2泡吧,3旅行,4宅家里,5健身,6早睡早起,7熬夜,8睡觉,9玩游戏,10音乐会

    @Max(value = 10, message = "生活态度--约会方式参数值有误，超出有效范围")
    @Min(value= 0 ,message= "生活态度--约会方式参数值有误，超出有效范围")
    private int appointment;//生活态度--约会方式 0未选择,1烛光晚餐,2喝咖啡,3AA制,4假期旅行,5逛公园,6看电影,7游乐场,8音乐剧,9K歌,10野营

    @Max(value = 6, message = "生活态度--婚姻观点参数值有误，超出有效范围")
    @Min(value= 0 ,message= "生活态度--婚姻观点参数值有误，超出有效范围")
    private int matrimony;//生活态度--婚姻观点 0未选择,1两情相悦,2一见钟情,3门当户对,4闪婚,5同舟共济,6嫁入豪门

    @Max(value = 6, message = "择偶要求--年龄类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--年龄类型参数值有误，超出有效范围")
    private int mateAge;//择偶要求--年龄类型  0未选择,1 18-29岁,2 30-39岁,3 40-49岁,4 50-59岁,5 60-69岁,6 70岁以上

    @Max(value = 2, message = "择偶要求--性别类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--性别类型参数值有误，超出有效范围")
    private int mateSex;//择偶要求--性别类型  0未选择,1男,2女

    @Max(value = 7, message = "择偶要求--身高类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--身高类型参数值有误，超出有效范围")
    private int mateHeight;//择偶要求--身高类型  0未选择,1 139以下,2 140-149,3 150-159,4 160-169,5 170-179,6 180-189,7 190以上

    @Max(value = 4, message = "择偶要求--婚姻状况参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--婚姻状况参数值有误，超出有效范围")
    private int mateMaritalStatus;//择偶要求--婚姻状况  0未选择,1已婚,2未婚,3离异,4丧偶

    @Max(value = 8, message = "择偶要求--学历情况参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--学历情况参数值有误，超出有效范围")
    private int mateStudyRank;//择偶要求--学历情况  0未选择,1小学,2初中,3高中,4大专,5本科,6硕士,7博士,8博士后

    @Length(max = 10, message = "地区住址参数mateRegion有误")
    private String mateRegion;//择偶要求--地区住址  0,0,1

    @Max(value = 12, message = "择偶要求--伴侣性格参数值有误，超出有效范围")
    @Min(value= 0 ,message= "择偶要求--伴侣性格参数值有误，超出有效范围")
    private int mateCharacter;//择偶要求--伴侣性格 0未选择,1活泼开朗,2理性智慧,3乐观积极,4悲观消极,5内向害羞,6天真可爱,7大大咧咧,8仔细认真,9感性冲动,10温柔贤惠,11浪漫迷人,12时尚女郎

    @Max(value = 4, message = "其他--血型类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他--血型类型参数值有误，超出有效范围")
    private int bloodType;//其他--血型类型 0 未选择,1 A型,2 B型,3 AB型,4 O型

    @Max(value = 4, message = "其他资料--宗教信仰参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--宗教信仰参数值有误，超出有效范围")
    private int religion;//其他--宗教信仰 0 未选择,1基督教,2佛教,3道教,4伊斯兰教

    @Max(value = 5, message = "其他资料--作息类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--作息类型参数值有误，超出有效范围")
    private int workHabits;//其他资料--作息类型 0 未选择,1早睡早起,2经常熬夜,3总是早起,4偶尔懒散,5没有规律

    @Max(value = 8, message = "其他资料--吸烟饮酒参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--吸烟饮酒参数值有误，超出有效范围")
    private int drinking;//其他资料--吸烟饮酒 0 未选择,1不喝酒,2不吸烟,3偶尔喝酒,4偶尔吸烟,5离不开酒,6烟瘾很大,7讨厌吸烟,8讨厌喝酒

    @Max(value = 7, message = "其他资料--最大消费参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--最大消费参数值有误，超出有效范围")
    private int bigCost;//其他资料--最大消费 0 未选择,1美食消费,2服装消费,3娱乐消费,4出行消费,5交友消费,6文化消费,7教育消费

    @Max(value = 5, message = "其他资料--制造浪漫参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--制造浪漫参数值有误，超出有效范围")
    private int romance;//其他资料--制造浪漫 0 未选择,1经常浪漫,2偶尔浪漫,3从不浪漫,4讨厌浪漫,5无所谓浪漫

    @Max(value = 8, message = "其他资料--专业类型参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--专业类型参数值有误，超出有效范围")
    private int majors;//其他资料--专业类型 0 未选择,1文化类,2IT,3制造类,4服务类,5艺术类,6教育类,7商务类,8金融类

    @Max(value = 8, message = "其他资料--生活技能参数值有误，超出有效范围")
    @Min(value= 0 ,message= "其他资料--生活技能参数值有误，超出有效范围")
    private int lifeSkills;//其他资料--生活技能 0 未选择,1交朋友,2上网,3理财,4烹饪,5健康运动,6文化艺术,7减肥,8社交活动

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;//修改时间

    private int redisStatus;//该对象在缓存中的存在形式  0空对象 无数据库对应数据  1数据已有对应数据  与数据无关字段

}
