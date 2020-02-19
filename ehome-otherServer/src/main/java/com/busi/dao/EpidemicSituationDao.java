package com.busi.dao;

import com.busi.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 疫情相关Dao
 * author：zhaojiajie
 * create time：2020-02-15 11:30:28
 */
@Mapper
@Repository
public interface EpidemicSituationDao {

    /***
     * 新增
     * @param epidemicSituation
     * @return
     */
    @Insert("insert into EpidemicSituation(modifyTime,createTime,imgUrl,dailyPic,summary,deleted,countRemark,currentConfirmedCount,confirmedCount,suspectedCount,curedCount,deadCount,seriousCount,remark1,remark2,remark3," +
            "remark4,remark5,note1,note2,note3,generalRemark,abroadRemark,quanguoTrendCharts,hbFeiHbTrendCharts,listByArea,listByOther)" +
            "values (#{modifyTime},#{createTime},#{imgUrl},#{dailyPic},#{summary},#{deleted},#{countRemark},#{currentConfirmedCount},#{confirmedCount},#{suspectedCount},#{curedCount},#{deadCount},#{seriousCount},#{remark1},#{remark2},#{remark3}" +
            ",#{remark4},#{remark5},#{note1},#{note2},#{note3},#{generalRemark},#{abroadRemark},#{quanguoTrendCharts},#{hbFeiHbTrendCharts},#{listByArea},#{listByOther})")
    @Options(useGeneratedKeys = true)
    int add(EpidemicSituation epidemicSituation);

    /***
     * 查询列表
     * @return
     */
    @Select("<script>" +
            "SELECT * FROM EpidemicSituation" +
            " order by id desc" +
            "</script>")
    List<EpidemicSituation> findList();

    /***
     * 查询疫情（最新一条）
     * @return
     */
    @Select("<script>" +
            "SELECT * FROM EpidemicSituation where id=( SELECT MAX(id) FROM EpidemicSituation )" +
            "</script>")
    EpidemicSituation findNew();

    /***
     * 查询列表(天气平台)
     * @return
     */
    @Select("<script>" +
            "SELECT * FROM EpidemicSituationTianqi" +
            " order by id desc" +
            "</script>")
    List<EpidemicSituationTianqi> findTQlist();

    /***
     * 查询疫情(天气平台)
     * @return
     */
    @Select("<script>" +
            "SELECT * FROM EpidemicSituationTianqi where id=( SELECT MAX(id) FROM EpidemicSituationTianqi )" +
            "</script>")
    EpidemicSituationTianqi findNewEStianQi();

    /***
     * 新增我和疫情
     * @param dishes
     * @return
     */
    @Insert("insert into EpidemicSituationAbout(userId,lat,lon,address,whatAmIdoing,donateMoney,benevolence,other,shoutSentence,imagine,wantToDo,wantToGo,addTime) " +
            "values (#{userId},#{lat},#{lon},#{address},#{whatAmIdoing},#{donateMoney},#{benevolence},#{other},#{shoutSentence},#{imagine},#{wantToDo},#{wantToGo},#{addTime})")
    @Options(useGeneratedKeys = true)
    int addESabout(EpidemicSituationAbout dishes);

    /***
     * 更新我和疫情
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update EpidemicSituationAbout set" +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " address=#{address}," +
            " whatAmIdoing=#{whatAmIdoing}," +
            " donateMoney=#{donateMoney}," +
            " benevolence=#{benevolence}," +
            " other=#{other}," +
            " imagine=#{imagine}," +
            " wantToDo=#{wantToDo}," +
            " wantToGo=#{wantToGo}," +
            " shoutSentence=#{shoutSentence}" +
            " where userId=#{userId}" +
            "</script>")
    int changeESabout(EpidemicSituationAbout kitchenDishes);

    /***
     * 根据ID查询我和疫情
     * @param id
     * @return
     */
    @Select("select * from EpidemicSituationAbout where userId=#{id}")
    EpidemicSituationAbout findESabout(@Param("id") long id);

    /***
     * 更新评选作品删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update CampaignAwardActivity set" +
            " status=#{status}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(CampaignAwardActivity kitchen);

    /***
     * 新增评选作品信息
     * @param selectionActivities
     * @return
     */
    @Insert("insert into CampaignAwardActivity(userId,title,content,imgUrl,videoUrl,videoCoverUrl,votesCounts,status" +
            ",time) " +
            "values (#{userId},#{title},#{content},#{imgUrl},#{videoUrl},#{videoCoverUrl},#{votesCounts},#{status}" +
            ",#{time})")
    @Options(useGeneratedKeys = true)
    int addSelection(CampaignAwardActivity selectionActivities);

    /***
     * 新增评选作品投票
     * @param selectionVote
     * @return
     */
    @Insert("insert into CampaignAwardVote(myId,userId,campaignAwardId,time) " +
            "values (#{myId},#{userId},#{campaignAwardId},#{time})")
    @Options(useGeneratedKeys = true)
    int addVote(CampaignAwardVote selectionVote);

    /***
     * 更新评选作品信息
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update CampaignAwardActivity set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"videoUrl != null and videoUrl != ''\">" +
            " videoUrl=#{videoUrl}," +
            "</if>" +
            "<if test=\"videoCoverUrl != null and videoCoverUrl != ''\">" +
            " videoCoverUrl=#{videoCoverUrl}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateSelection(CampaignAwardActivity selectionActivities);

    /***
     * 更新评选作品投票数
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update CampaignAwardActivity set" +
            " votesCounts=#{votesCounts}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(CampaignAwardActivity selectionActivities);

    /***
     * 根据ID查询参加评选作品的详细信息
     * @param id
     * @return
     */
    @Select("select * from CampaignAwardActivity where id = #{id}")
    CampaignAwardActivity findById(@Param("id") long id);

    /***
     * 查询是否给该评选作品投过票
     * @param campaignAwardId
     * @return
     */
    @Select("select * from CampaignAwardVote where campaignAwardId=#{campaignAwardId} " +
            " and myId = #{myId} " +
            " and TO_DAYS(time)=TO_DAYS(NOW())"
    )
    CampaignAwardVote findTicket(@Param("myId") long myId, @Param("campaignAwardId") long campaignAwardId);

    /***
     * 分页查询评选作品列表
     * @param findType   查询类型： 0默认全部，1票数最高 2票数最低
     * @param userId   用戶ID
     * @return
     */
    @Select("<script>" +
            "select * from CampaignAwardActivity" +
            " where 1=1" +
            " and status = 0" +
            "<if test=\"userId > 0\">" +
            " and userId =#{userId}" +
            "</if>" +
            " <if test=\"findType==0\">" +
            " ORDER BY votesCounts DESC,time DESC" +
            "</if>" +
            " <if test=\"findType==1\">" +
            " order by votesCounts desc" +
            "</if>" +
            " <if test=\"findType==2\">" +
            " ORDER BY votesCounts ASC" +
            "</if>" +
            "</script>")
    List<CampaignAwardActivity> findsSelectionList(@Param("userId") long userId, @Param("findType") int findType);

    /***
     * 删除轨迹
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from MyTrajectory" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId=#{userId}" +
            "</script>")
    int delTrajectory(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 新增轨迹
     * @param selectionVote
     * @return
     */
    @Insert("insert into MyTrajectory(userId,departTime,placeOfDeparture,setOutLat,setOutLon,arriveTime,destination,arriveLat,arriveLon,vehicle,trainNumber,time) " +
            "values (#{userId},#{departTime},#{placeOfDeparture},#{setOutLat},#{setOutLon},#{arriveTime},#{destination},#{arriveLat},#{arriveLon},#{vehicle},#{trainNumber},#{time})")
    @Options(useGeneratedKeys = true)
    int addTrajectory(MyTrajectory selectionVote);

    /***
     * 更新轨迹
     * @param selectionActivities
     * @return
     */
    @Update("<script>" +
            "update MyTrajectory set" +
            " departTime=#{departTime}," +
            " placeOfDeparture=#{placeOfDeparture}," +
            " setOutLat=#{setOutLat}," +
            " setOutLon=#{setOutLon}," +
            " arriveTime=#{arriveTime}," +
            " destination=#{destination}," +
            " arriveLat=#{arriveLat}," +
            " arriveLon=#{arriveLon}," +
            " vehicle=#{vehicle}," +
            " trainNumber=#{trainNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int editTrajectory(MyTrajectory selectionActivities);

    /***
     * 根据ID查询轨迹
     * @param id
     * @return
     */
    @Select("select * from MyTrajectory where id = #{id}")
    MyTrajectory findTrajectory(@Param("id") long id);

    /***
     * 分页查询轨迹列表
     * @param userId   用戶ID
     * @return
     */
    @Select("<script>" +
            "select * from MyTrajectory" +
            " where 1=1" +
            " and userId =#{userId}" +
            " ORDER BY time desc" +
            "</script>")
    List<MyTrajectory> findTrajectoryList(@Param("userId") long userId);
}
