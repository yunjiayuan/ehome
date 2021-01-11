package com.busi.dao;

import com.busi.entity.Footmark;
import com.busi.entity.Footmarkauthority;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 寻人寻物失物招领Dao
 * author：zhaojiajie
 * create time：2018-10-9 12:01:16
 */
@Mapper
@Repository
public interface FootmarkDao {

    /***
     * 新增足迹
     * @param footmark
     * @return
     */
    @Insert("insert into footmark(userId,title,addTime,imgUrl,videoUrl,audioUrl,infoId,footmarkStatus,footmarkType) " +
            "values (#{userId},#{title},#{addTime},#{imgUrl},#{videoUrl},#{audioUrl},#{infoId},#{footmarkStatus},#{footmarkType})")
    @Options(useGeneratedKeys = true)
    int add(Footmark footmark);

    /***
     * 新增足迹权限
     * @param footmarkauthority
     * @return
     */
    @Insert("insert into footmarkauthority(userId,authority) " +
            "values (#{userId},#{authority})")
    @Options(useGeneratedKeys = true)
    int addAuthority(Footmarkauthority footmarkauthority);

    /***
     * 更新足迹
     * @param footmark
     * @return
     */
    @Update("<script>" +
            "update footmark set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"videoUrl != null and videoUrl != ''\">" +
            " videoUrl=#{videoUrl}," +
            "</if>" +
            "<if test=\"audioUrl != null and audioUrl != ''\">" +
            " audioUrl=#{audioUrl}," +
            "</if>" +
            " userId=#{userId}" +
            " where infoId = #{infoId} and userId=#{userId}" +
            "</script>")
    int updateFootmark(Footmark footmark);

    /***
     * 更新删除状态
     * @param footmark
     * @return
     */
    @Update("<script>" +
            "update footmark set" +
            " footmarkStatus=#{footmarkStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(Footmark footmark);

    /***
     * 更新删除状态
     * @param footmark
     * @return
     */
    @Update("<script>" +
            "update footmark set" +
            " footmarkStatus=1" +
            " where infoId = #{infoId} and userId=#{userId} and footmarkType=6" +
            "</script>")
    int delFootmarkPad(Footmark footmark);

    /***
     * 更新权限
     * @param footmarkauthority
     * @return
     */
    @Update("<script>" +
            "update footmarkauthority set" +
            " authority=#{authority}" +
            " where userId=#{userId}" +
            "</script>")
    int updateAuthority(Footmarkauthority footmarkauthority);

    /***
     * 根据Id查询足迹
     * @param id
     */
    @Select("select * from footmark where id=#{id} and footmarkStatus=0")
    Footmark findUserById(@Param("id") long id);

    /***
     * 根据Id查询足迹
     * @param id
     */
    @Select("select * from footmark where infoId = #{id} and footmarkStatus=0 and footmarkType=6")
    Footmark findByIds(@Param("id") long id);

    /***
     * 根据Id查询足迹权限
     * @param userId
     */
    @Select("select * from footmarkauthority where userId=#{userId}")
    Footmarkauthority findUserId(@Param("userId") long userId);

    /***
     * 分页查询 默认按时间降序排序
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from footmark" +
            " where 1=1" +
            " and footmarkStatus = 0" +
            "<if test=\"userId > 0\">" +
            " and userId=#{userId}" +
            "</if>" +
            //查全部
            "<if test=\"footmarkType == 0\">" +
            "<if test=\"beginDate != null\">" +
            " and addTime >= date_add(#{beginDate}, interval 0 DAY)" +
            "</if>" +
            "<if test=\"endDate != null\">" +
            " and addTime &lt;= date_add(#{endDate}, interval 0 DAY)" +
            "</if>" +
            " order by addTime asc" +
            "</if>" +
            //按类别查
            "<if test=\"footmarkType > 0\">" +
            " and footmarkType=#{footmarkType}" +
            " order by addTime desc" +
            "</if>" +
            //当日其他
            "<if test=\"footmarkType &lt; 0\">" +
            " and footmarkType &lt; 6" +
            "<if test=\"beginDate != null\">" +
            " and addTime >= date_add(#{beginDate}, interval 0 DAY)" +
            " and addTime &lt;= date_add(#{beginDate}, interval 1 DAY)" +
            "</if>" +
            " order by addTime desc" +
            "</if>" +
            "</script>")
    List<Footmark> findTimeList(@Param("userId") long userId, @Param("footmarkType") int footmarkType, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /***
     * 分页查询 默认按时间降序排序(时间为空时调用)
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from footmark" +
            " where 1=1" +
            "<if test=\"userId > 0\">" +
            " and userId=#{userId}" +
            "</if>" +
            "<if test=\"footmarkType > 0\">" +
            " and footmarkType=#{footmarkType}" +
            "</if>" +
            "<if test=\"footmarkType == -1\">" +
            " <![CDATA[ and footmarkType < 6 ]]>" +
            " and TO_DAYS(addTime)=TO_DAYS(NOW())" +
            "</if>" +
            " and footmarkStatus = 0" +
            " order by addTime desc" +
            "</script>")
    List<Footmark> findList(@Param("userId") long userId, @Param("footmarkType") int footmarkType);

}
