package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 找人接口 提供精确找人 条件找人 附近的人功能
 * author：SunTianJie
 * create time：2018/7/11 13:10
 */
public interface SearchUserApiController {

    /**
     * 精确找人接口
     *
     * @param searchType 查找类型 0门牌号查找(默认) 1手机号查找
     * @param param      当searchType=0时，此处为省简称与门牌号组合，格式：0_1003001
     *                   当searchType=1时，此处为手机号，格式为：15901213694
     * @return
     */
    @GetMapping("accurateSearchUser/{searchType}/{param}")
    ReturnData accurateSearchUser(@PathVariable int searchType, @PathVariable String param);

    /**
     * 条件找人接口
     *
     * @param name          用户名
     * @param beginAge      起始年龄（包含） 默认0
     * @param endAge        结束年龄（包含） 默认0 endAge>beginAge
     * @param sex           性别 0不限 1男2女
     * @param province      省  -1为不限
     * @param city          市  -1为不限
     * @param district      区  -1为不限
     * @param studyrank     学历  0：不限  1:"中专",2:"专科",3:"本科",4:"双学士",5:"硕士",6:"博士",7:"博士后",8:"其他"
     * @param maritalstatus 婚否  0：不限  1:"已婚",2:"未婚",3:"离异",4:"丧偶"
     * @param talkToSomeoneStatus  倾诉状态 -1 表示不限 0表示不接受倾诉  1表示接受倾诉
     * @param chatnteractionStatus 聊天互动功能的状态 -1 表示不限 0表示不接受别人找你互动  1表示接受别人找你互动
     * @param page          页码 第几页 起始值1
     * @param count         每页条数
     * @return
     */
    @GetMapping("fuzzySearchUser/{name}/{beginAge}/{endAge}/{sex}/{province}/{city}/{district}/{studyrank}/{maritalstatus}/{talkToSomeoneStatus}/{chatnteractionStatus}/{page}/{count}")
    ReturnData fuzzySearchUser(@PathVariable String name, @PathVariable int beginAge, @PathVariable int endAge,
                               @PathVariable int sex, @PathVariable int province, @PathVariable int city,
                               @PathVariable int district, @PathVariable int studyrank, @PathVariable int maritalstatus,
                               @PathVariable int talkToSomeoneStatus, @PathVariable int chatnteractionStatus,
                               @PathVariable int page, @PathVariable int count);

    /***
     * 查找附近的人接口
     * @param sex 性别 0不限 1男2女
     * @param lat 纬度 小数点后6位
     * @param lon 经度 小数点后6位
     * @return
     */
    @GetMapping("nearbySearchUser/{sex}/{lat}/{lon}")
    ReturnData nearbySearchUser(@PathVariable int sex, @PathVariable double lat, @PathVariable double lon);

    /***
     * 随机艳遇蛋人员
     * @return
     */
    @GetMapping("randomPeople")
    ReturnData randomPeople();

    /***
     * 找人倾诉、找人互动人员推荐接口
     * @param talkToSomeoneStatus  倾诉状态 -1 表示不限 0表示不接受倾诉  1表示接受倾诉
     * @param chatnteractionStatus 聊天互动功能的状态 -1 表示不限 0表示不接受别人找你互动  1表示接受别人找你互动
     * @return
     */
    @GetMapping("talkToSomeoneRecommend/{talkToSomeoneStatus}/{chatnteractionStatus}")
    ReturnData talkToSomeoneRecommend(@PathVariable int talkToSomeoneStatus, @PathVariable int chatnteractionStatus);
}
