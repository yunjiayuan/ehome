package com.busi.dao;

import com.busi.entity.PartnerBuyGoods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 合伙购相关DAO
 * author：ZJJ
 * create time：2020-04-17 18:41:02
 */
@Mapper
@Repository
public interface PartnerBuyDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into PartnerBuyGoods(personnel,userId,imgUrl,goodsTitle,usedSort,levelOne,levelTwo,levelThree,levelFour,levelFive,videoCoverUrl,videoUrl," +
            "specs,price,details,remarks,state,limitTime,province,city,district,partnerPrice," +
            "limitNumber,number,releaseTime,usedSortId) " +
            "values (#{personnel},#{userId},#{imgUrl},#{goodsTitle},#{usedSort},#{levelOne},#{levelTwo},#{levelThree},#{levelFour},#{levelFive},#{videoCoverUrl},#{videoUrl}," +
            "#{specs},#{price},#{details},#{remarks},#{state},#{limitTime},#{province},#{city},#{district},#{partnerPrice}," +
            "#{limitNumber},#{number},#{releaseTime},#{usedSortId})")
    @Options(useGeneratedKeys = true)
    int add(PartnerBuyGoods homeShopGoods);

    /***
     * 更新
     * @param kitchenDishes
     * @return
     */
    @Update("<script>" +
            "update PartnerBuyGoods set" +
            "<if test=\"number > 0\">" +
            " personnel = #{personnel}," +
            " number = #{number}" +
            "</if>" +
            "<if test=\"state > 0\">" +
            " state = #{state}" +
            "</if>" +
            " where id=#{id}" +
            "</script>")
    int update(PartnerBuyGoods kitchenDishes);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from PartnerBuyGoods where id=#{id} and deleteType=0")
    PartnerBuyGoods findUserById(@Param("id") long id);

    /***
     * 查询列表
     * @param sort  查询条件:0全部，1我发起的，2我参与的
     * @param userId  查询者
     * @return
     */
    @Select("<script>" +
            "select * from PartnerBuyGoods" +
            " where deleteType=0" +
            "<if test=\"sort == 2\">" +
            " and personnel LIKE CONCAT('%',#{user},'%')" +
            "</if>" +
            "<if test=\"sort == 1\">" +
            " and userId=#{userId}" +
            "</if>" +
            " order by releaseTime desc" +
            "</script>")
    List<PartnerBuyGoods> findDishesSortList(@Param("sort") int sort, @Param("user") String user, @Param("userId") long userId);

}
