package com.busi.dao;

import com.busi.entity.Hotel;
import com.busi.entity.SelectionActivities;
import com.busi.entity.SelectionVote;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评选活动Dao
 * author：zhaojiajie
 * create time：2018-10-16 14:38:24
 */
@Mapper
@Repository
public interface SelectionDao {

    /***
     * 新增活动信息
     * @param selectionActivities
     * @return
     */
    @Insert("insert into selectionActivities(userId,selectionType,s_province,s_city,s_district,s_name,s_sex,s_birthday,s_job,s_maritalStatus,s_height" +
            ",s_weight,s_introduce,imgUrl,votesCounts,time,status,activityVideo,activityCover) " +
            "values (#{userId},#{selectionType},#{s_province},#{s_city},#{s_district},#{s_name},#{s_sex},#{s_birthday},#{s_job},#{s_maritalStatus},#{s_height}" +
            ",#{s_weight},#{s_introduce},#{imgUrl},#{votesCounts},#{time},#{status},#{activityVideo},#{activityCover})")
    @Options(useGeneratedKeys = true)
    int addSelection(SelectionActivities selectionActivities);

    /***
     * 新增投票
     * @param selectionVote
     * @return
     */
    @Insert("insert into SelectionVote(myId,userId,selectionType,time) " +
            "values (#{myId},#{userId},#{selectionType},#{time})")
    @Options(useGeneratedKeys = true)
    int addVote(SelectionVote selectionVote);

    /***
     * 更新活动信息
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update selectionActivities set" +
            "<if test=\"s_name != null and s_name != ''\">" +
            " s_name=#{s_name}," +
            "</if>" +
            "<if test=\"s_introduce != null and s_introduce != ''\">" +
            " s_introduce=#{s_introduce}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"s_birthday != null\">" +
            " s_birthday=#{s_birthday}," +
            "</if>" +
            " s_city=#{s_city}," +
            " s_district=#{s_district}," +
            " s_sex=2," +
            " s_job=#{s_job}," +
            " activityVideo=#{activityVideo}," +
            " s_maritalStatus=#{s_maritalStatus}," +
            " s_height=#{s_height}," +
            " s_weight=#{s_weight}," +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateSelection(SelectionActivities selectionActivities);

    /***
     * 更新投票数
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update selectionActivities set" +
            " votesCounts=#{votesCounts}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(SelectionActivities selectionActivities);

    @Update("<script>" +
            "update selectionActivities set" +
            " auditType=#{auditType}" +
            " where id=#{id}" +
            "</script>")
    int changeAuditState(SelectionActivities selectionActivities);

    /***
     * 更新封面&活动图片&视频地址
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update selectionActivities set" +
            "<if test=\"activityCover != null and activityCover != ''\">" +
            " activityCover=#{activityCover}" +
            "</if>" +
            "<if test=\"activityVideo != null and activityVideo != ''\">" +
            " activityVideo=#{activityVideo}" +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}" +
            "</if>" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setCover(SelectionActivities selectionActivities);

    /***
     * 查询是否已经参加
     * @param selectionType
     * @return
     */
    @Select("select * from SelectionActivities where userId = #{userId} and selectionType = #{selectionType} ")
    SelectionActivities findDetails(@Param("userId") long userId, @Param("selectionType") long selectionType);

    /***
     * 分页查询参加活动的人员列表
     * @param findType   查询类型： 0表示默认，1表示查询有视频的
     * @param orderVoteCountType  排序规则 0按票数从高到低 1按票数从低到高
     * @param s_province  省ID -1不限
     * @param s_city   市ID -1不限
     * @param s_district  区ID -1不限
     * @return
     */
    @Select("<script>" +
            "select * from SelectionActivities" +
            " where 1=1" +
            " and status = 0" +
            " and selectionType=#{selectionType}" +
            "<if test=\"findType == 1\">" +
            " and activityVideo is not null" +
            "</if>" +
            " <if test=\"s_province>=0\">" +
            " and s_province=#{s_province}" +
            "</if>" +
            " <if test=\"s_city>=0\">" +
            " and s_city=#{s_city}" +
            "</if>" +
            " <if test=\"s_district>=0\">" +
            " and s_district=#{s_district}" +
            "</if>" +
            " <if test=\"orderVoteCountType!=0\">" +
            " ORDER BY votesCounts ASC" +
            "</if>" +
            " <if test=\"orderVoteCountType==0\">" +
            " ORDER BY votesCounts DESC" +
            "</if>" +
            "</script>")
    List<SelectionActivities> findsSelectionList1(@Param("findType") int findType, @Param("orderVoteCountType") int orderVoteCountType,
                                                  @Param("s_province") int s_province, @Param("s_city") int s_city, @Param("s_district") int s_district, @Param("selectionType") int selectionType);

    /***
     * 分页查询参加活动的人员列表
     * @param findType   查询类型： 0表示默认，1表示查询有视频的
     * @param orderVoteCountType  排序规则 0按票数从高到低 1按票数从低到高
     * @param s_province  省ID -1不限
     * @param s_job   selectionType为1时=职业 "0":"请选择","1":"在校学生","2":"计算机/互联网/IT","3":"电子/半导体/仪表仪器","4":"通讯技术","5":"销售","6":"市场拓展","7":"公关/商务","8":"采购/贸易","9":"客户服务/技术支持","10":"人力资源/行政/后勤","11":"高级管理","12":"生产/加工/制造","13":"质检/安检","14":"工程机械","15":"技工","16":"财会/审计/统计","17":"金融/证券/投资/保险","18":"房地产/装修/物业","19":"仓储/物流","20":"交通/运输","21":"普通劳动力/家政服务","22":"普通服务行业","23":"航空服务业","24":"教育/培训","25":"咨询/顾问","26":"学术/科研","27":"法律","28":"设计/创意","29":"文学/传媒/影视","30":"餐饮/旅游","31":"化工","32":"能源/地址勘察","33":"医疗/护理","34":"保健/美容","35":"生物/制药/医疗机械","36":"体育工作者","37":"翻译","38":"公务员/国家干部","39":"私营业主","40":"农/林/牧/渔业","41":"警察/其他","42":"自由职业者","43":"其他"
     * 	  			  selectionType为2时=学校名称：0，1，2，3
     * @return
     */
    @Select("<script>" +
            "select * from SelectionActivities" +
            " where 1=1" +
            " and status = 0" +
            " and selectionType=2" +
            "<if test=\"findType == 1\">" +
            " and activityVideo is not null" +
            "</if>" +
            " <if test=\"s_province>0\">" +
            " and s_province=#{s_province}" +
            "</if>" +
            " <if test=\"s_job>0\">" +
            " and s_job=#{s_job}" +
            "</if>" +
            " <if test=\"orderVoteCountType!=0\">" +
            " ORDER BY votesCounts ASC" +
            "</if>" +
            " <if test=\"orderVoteCountType==0\">" +
            " ORDER BY votesCounts DESC" +
            "</if>" +
            "</script>")
    List<SelectionActivities> findsSelectionList2(@Param("findType") int findType, @Param("orderVoteCountType") int orderVoteCountType,
                                                  @Param("s_province") int s_province, @Param("s_job") int s_job);

    /***
     * 分页查询参加活动的人员列表
     * @param searchType  排序 0按条件查询 1按编号查询 2按名字查询
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @param findType   查询类型： 0表示默认，1表示查询有视频的
     * @param infoId  被查询参加活动人员的活动ID
     * @param s_name  名字
     * @return
     */
    @Select("<script>" +
            "select * from SelectionActivities" +
            " where 1=1" +
            " and status = 0" +
            " and selectionType=#{selectionType}" +
            "<if test=\"findType == 1\">" +
            " and activityVideo is not null" +
            "</if>" +
            "<if test=\"searchType == 1\">" +
            " and id=#{infoId}" +
            "</if>" +
            "<if test=\"s_name != null and s_name != '' and searchType == 2 \">" +
            " and s_name LIKE CONCAT('%',#{s_name},'%')" +
            "</if>" +
            "</script>")
    List<SelectionActivities> findsSelectionList3(@Param("searchType") int searchType, @Param("selectionType") int selectionType, @Param("findType") int findType, @Param("infoId") long infoId, @Param("s_name") String s_name);

    /***
     * 根据ID查询参加活动人员的详细信息
     * @param id
     * @return
     */
    @Select("select * from SelectionActivities where id = #{id}")
    SelectionActivities findById(@Param("id") long id);

    /***
     * 查询是否给该用户投过票
     * @param selectionType
     * @return
     */
    @Select("select * from SelectionVote where userId=#{userId} " +
            " and myId = #{myId} " +
            " and selectionType = #{selectionType}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())"
    )
    SelectionVote findTicket(@Param("myId") long myId, @Param("userId") long userId, @Param("selectionType") int selectionType);

    /***
     * 分页查询投票历史
     * @param userId  用户ID
     * @param selectionType  评选类型 1城市小姐  2校花  3城市之星   4青年创业
     * @return
     */
    @Select("<script>" +
            "select * from SelectionVote" +
            " where 1=1" +
            " and userId=#{userId}" +
            " and selectionType = #{selectionType}" +
            " order by time desc" +
            "</script>")
    List<SelectionVote> findVoteList(@Param("userId") long userId, @Param("selectionType") int selectionType);

    /***
     * 统计各类审核数量
     * @return
     */
    @Select("<script>" +
            "select * from SelectionActivities" +
            " where status = 0 and selectionType= #{selectionType}" +
            "</script>")
    List<SelectionActivities> countAuditType(@Param("selectionType") int selectionType);

    /***
     * 分页查询参加活动的人员列表
     * @param selectionType  0云家园招募令 1城市小姐  2校花  3城市之星   4青年创业
     * @param infoId  被查询参加活动人员的活动ID
     * @param s_name  名字
     * @param auditType   0待审核,1通过
     * @return
     */
    @Select("<script>" +
            "select * from SelectionActivities" +
            " where 1=1" +
            " and status = 0" +
            " and selectionType=#{selectionType}" +
            "<if test=\"infoId > 0\">" +
            " and id= #{infoId}" +
            "</if>" +
            " and auditType=#{auditType}" +
            "<if test=\"s_name != null and s_name != '' \">" +
            " and s_name LIKE CONCAT('%',#{s_name},'%')" +
            "</if>" +
            "</script>")
    List<SelectionActivities> findMyRecordList(@Param("selectionType") int selectionType, @Param("infoId") long infoId, @Param("s_name") String s_name, @Param("auditType") int auditType);

}
