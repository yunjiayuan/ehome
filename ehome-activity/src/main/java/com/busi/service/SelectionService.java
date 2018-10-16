package com.busi.service;

import com.busi.dao.SelectionDao;
import com.busi.entity.PageBean;
import com.busi.entity.SelectionActivities;
import com.busi.entity.SelectionVote;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 评选活动
 * @author: ZHaoJiaJie
 * @create: 2018-10-16 14:11
 */
@Service
public class SelectionService {

    @Autowired
    private SelectionDao selectionDao;

    /***
     * 新增活动信息
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSelection(SelectionActivities selectionActivities) {
        return selectionDao.addSelection(selectionActivities);
    }

    /***
     * 新增投票
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addVote(SelectionVote selectionVote) {
        return selectionDao.addVote(selectionVote);
    }

    /***
     * 更新活动信息
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateSelection(SelectionActivities selectionActivities) {
        return selectionDao.updateSelection(selectionActivities);
    }

    /***
     * 更新投票数
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(SelectionActivities selectionActivities) {
        return selectionDao.updateNumber(selectionActivities);
    }

    /***
     * 更新封面&活动图片&视频地址
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setCover(SelectionActivities selectionActivities) {
        return selectionDao.setCover(selectionActivities);
    }

    /***
     * 查询是否已经参加
     * @param selectionType
     * @return
     */
    public SelectionActivities findDetails(long userId, int selectionType) {
        return selectionDao.findDetails(userId, selectionType);
    }

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
    public PageBean<SelectionActivities> findsSelectionActivitiesList(int searchType, int selectionType, int findType, long infoId, int orderVoteCountType,
                                                                      String s_name, int s_province, int s_city, int s_district, int s_job,
                                                                      int page, int count) {

        List<SelectionActivities> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (searchType == 0) {
            if (selectionType == 1) {
                list = selectionDao.findsSelectionList1(findType,
                        infoId, orderVoteCountType, s_name, s_province, s_city,
                        s_district);
            }
            if (selectionType == 2) {
                list = selectionDao.findsSelectionList2(findType,
                        infoId, orderVoteCountType, s_name, s_province, s_city,
                        s_district, s_job);
            }
        }
        list = selectionDao.findsSelectionList3(selectionType, findType, infoId, s_name);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据ID查询参加活动人员的详细信息
     * @param id
     * @return
     */
    public SelectionActivities findById(long id) {
        return selectionDao.findById(id);
    }

    /***
     * 查询是否给该用户投过票
     * @param selectionType
     * @return
     */
    public SelectionVote findTicket(long myId, long userId, int selectionType) {
        return selectionDao.findTicket(myId, userId, selectionType);
    }

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<SelectionVote> findVoteList(long userId, int selectionType, int page, int count) {

        List<SelectionVote> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = selectionDao.findVoteList(userId, selectionType);

        return PageUtils.getPageBean(p, list);
    }


}
