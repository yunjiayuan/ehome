package com.busi.dao;

import com.busi.entity.Groupsetup;
import com.busi.entity.Notice;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息通知Dao
 * author：zhaojiajie
 * create time：2018-9-13 13:42:49
 */
@Mapper
@Repository
public interface NoticeDao {

    /***
     * 新增消息通知
     * @param notice
     * @return
     */
    @Insert("insert into notice(userId, addTime, showContents, allDayExempts, exemptingStartTime, exemptingEndTime, shock, voice) " +
            "values (#{userId},#{addTime},#{showContents},#{allDayExempts},#{exemptingStartTime},#{exemptingEndTime},#{shock},#{voice})")
    @Options(useGeneratedKeys = true)
    int addNotice(Notice notice);

    /***
     * 新增群消息通知
     * @param groupsetup
     * @return
     */
    @Insert("insert into groupsetup(userId, addTime, groupId, setup) " +
            "values (#{userId},#{addTime},#{groupId},#{setup})")
    @Options(useGeneratedKeys = true)
    int addGroupsetup(Groupsetup groupsetup);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " newNotice=#{newNotice}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp0(Notice notice);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " showContents=#{showContents}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp1(Notice notice);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " allDayExempts=#{allDayExempts}," +
            "<if test=\"allDayExempts == 1\">" +
            " exemptingEndTime=#{exemptingEndTime}," +
            " exemptingStartTime=#{exemptingStartTime}," +
            " shock=#{shock}," +
            " voice=#{voice}," +
            " showContents=#{showContents}," +
            "</if>" +
            " userId=#{userId}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp3(Notice notice);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " exemptingEndTime=#{exemptingEndTime}," +
            " exemptingStartTime=#{exemptingStartTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp4(Notice notice);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " shock=#{shock}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp5(Notice notice);

    /***
     * 更新设置
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " voice=#{voice}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUp6(Notice notice);

    /***
     * 置空自定义免扰时间
     * @param notice
     * @return
     */
    @Update("<script>" +
            "update notice set" +
            " exemptingEndTime=#{exemptingEndTime}," +
            " exemptingStartTime=#{exemptingStartTime}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setTime(Notice notice);

    /***
     * 更新群设置
     * @param groupsetup
     * @return
     */
    @Update("<script>" +
            "update groupsetup set" +
            " setup=#{setup}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int setUpgroup(Groupsetup groupsetup);

    /***
     * 查询消息设置详情
     */
    @Select("select * from Notice where userId=#{userId}")
    Notice findSetUp(@Param("userId") long userId);

    /***
     * 查询群消息设置详情
     */
    @Select("select * from groupsetup where groupId=#{groupId}")
    Groupsetup findsetUpgroup(@Param("groupId") long groupId);

    /***
     * 分页查询群消息设置
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from Groupsetup" +
            " where userId=#{userId}" +
            "</script>")
    List<Groupsetup> findSetUpList(@Param("userId") long userId);

}
