package com.busi.controller.api;


import com.busi.entity.ReturnData;
import com.busi.entity.SelectionActivities;
import com.busi.entity.SelectionVote;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 活动相关接口
 * author：zhaojiajie
 * create time：2018-10-10 13:09:46
 */
public interface SelectionApiController {

    /***
     * 新增
     * @param selectionActivities
     * @param bindingResult
     * @return
     */
    @PostMapping("joinActivity")
    ReturnData joinActivity(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult);

    /**
     * @Description: 更新
     * @Param: selectionActivities
     * @return:
     */
    @PutMapping("editActivity")
    ReturnData editActivity(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult);

    /***
     * 分页查询参加活动的人员列表
     * @param searchType  排序 0按条件查询 1按编号查询 2按名字查询
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @param findType   查询类型： 0表示默认，1表示查询有视频的
     * @param infoId  被查询参加活动人员的活动ID
     * @param orderVoteCountType  排序规则 0按票数从高到低 1按票数从低到高
     * @param s_name  名字
     * @param s_province  省ID -1不限
     * @param s_city   市ID -1不限
     * @param s_district  区ID -1不限
     * @param s_job   selectionType为1时=职业 "0":"请选择","1":"在校学生","2":"计算机/互联网/IT","3":"电子/半导体/仪表仪器","4":"通讯技术","5":"销售","6":"市场拓展","7":"公关/商务","8":"采购/贸易","9":"客户服务/技术支持","10":"人力资源/行政/后勤","11":"高级管理","12":"生产/加工/制造","13":"质检/安检","14":"工程机械","15":"技工","16":"财会/审计/统计","17":"金融/证券/投资/保险","18":"房地产/装修/物业","19":"仓储/物流","20":"交通/运输","21":"普通劳动力/家政服务","22":"普通服务行业","23":"航空服务业","24":"教育/培训","25":"咨询/顾问","26":"学术/科研","27":"法律","28":"设计/创意","29":"文学/传媒/影视","30":"餐饮/旅游","31":"化工","32":"能源/地址勘察","33":"医疗/护理","34":"保健/美容","35":"生物/制药/医疗机械","36":"体育工作者","37":"翻译","38":"公务员/国家干部","39":"私营业主","40":"农/林/牧/渔业","41":"警察/其他","42":"自由职业者","43":"其他"
     * 	  			  selectionType为2时=学校名称：0，1，2，3
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findMyRecord/{searchType}/{selectionType}/{findType}/{infoId}/{orderVoteCountType}/{s_name}/{s_province}/{s_city}/{s_district}/{s_job}/{page}/{count}")
    ReturnData findMyRecord(@PathVariable int searchType, @PathVariable int selectionType, @PathVariable int findType, @PathVariable long infoId, @PathVariable int orderVoteCountType,
                            @PathVariable String s_name, @PathVariable int s_province, @PathVariable int s_city, @PathVariable int s_district, @PathVariable int s_job,
                            @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 更新封面
     * @Param: selectionActivities
     * @return:
     */
    @PutMapping("setCover")
    ReturnData setCover(@Valid @RequestBody SelectionActivities selectionActivities, BindingResult bindingResult);

    /**
     * @Description: 删除活动图片
     * @return:
     */
//    @DeleteMapping("delPic/{delImgUrl}/{id}")
//    ReturnData delPic(@PathVariable String delImgUrl, @PathVariable long id);

    /**
     * 查询用户是否参加过活动
     *
     * @param selectionType 评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @return
     */
    @GetMapping("isJoin/{selectionType}")
    ReturnData isJoin(@PathVariable int selectionType);

    /**
     * 查询参加活动人员的详细信息
     *
     * @param id
     * @return
     */
    @GetMapping("findJoin/{id}")
    ReturnData findJoin(@PathVariable long id);

    /***
     * 投票
     * @param selectionVote
     * @param bindingResult
     * @return
     */
    @PostMapping("vote")
    ReturnData vote(@Valid @RequestBody SelectionVote selectionVote, BindingResult bindingResult);

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findVoteHistory/{userId}/{selectionType}/{page}/{count}")
    ReturnData findVoteHistory(@PathVariable long userId, @PathVariable int selectionType, @PathVariable int page, @PathVariable int count);

}
