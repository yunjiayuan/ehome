package com.busi.dao;

import com.busi.entity.ShopFloorShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店购物车
 * @author: ZHaoJiaJie
 * @create: 2019-12-09 13:58
 */
@Mapper
@Repository
public interface ShopFloorShoppingCartDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into ShopFloorShoppingCart(userId,goodsId,goodsCoverUrl,goodsTitle,basicDescribe,specs,price,addTime,number,levelOne,levelTwo,levelThree) " +
            "values (#{userId},#{goodsId},#{goodsCoverUrl},#{goodsTitle},#{basicDescribe},#{specs},#{price},#{addTime},#{number},#{levelOne},#{levelTwo},#{levelThree})")
    @Options(useGeneratedKeys = true)
    int add(ShopFloorShoppingCart homeShopGoods);

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update ShopFloorShoppingCart set" +
            " number=#{number}," +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(ShopFloorShoppingCart homeShopGoods);

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update ShopFloorShoppingCart set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateDels(@Param("ids") String[] ids);

    /***
     * 删除购物车商品
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from ShopFloorShoppingCart" +
            " where deleteType=0 and goodsId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delGoods(@Param("ids") String[] ids);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from ShopFloorShoppingCart where userId=#{userId} and deleteType=0 order by addTime desc")
    List<ShopFloorShoppingCart> findList(@Param("userId") long userId);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from ShopFloorShoppingCart where userId=#{userId} and deleteType>=1 order by deleteTime desc")
    List<ShopFloorShoppingCart> findDeleteGoods(@Param("userId") long userId);

    /***
     * 根据goodsId查询
     * @param goodsId
     */
    @Select("select * from ShopFloorShoppingCart " +
            "where userId=#{userId} " +
            "<if test=\"type == 0\">" +
            " and deleteType=0" +
            "</if>" +
            "<if test=\"type == 1\">" +
            " and deleteType>0" +
            "</if>" +
            "and goodsId=#{goodsId}")
    ShopFloorShoppingCart findGoodsId(@Param("type") int type, @Param("userId") long userId, @Param("goodsId") long goodsId);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from ShopFloorShoppingCart where userId=#{userId} and id=#{id}")
    ShopFloorShoppingCart findId(@Param("userId") long userId, @Param("id") long id);

    /***
     * 统计用户购物车商品数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from ShopFloorShoppingCart" +
            " where 1=1 " +
            " and userId = #{userId} and deleteType=0" +
            "</script>")
    int findNum(@Param("userId") long userId);
}
