package com.busi.service;

import com.busi.entity.UserInfo;
import com.busi.dao.UserInfoDao;
import com.busi.entity.PageBean;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    /***
     * 新增用户
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(UserInfo userInfo) {
        return userInfoDao.add(userInfo);
    }

    /***
     * 根据用户ID查询userInfo信息
     * @param id
     * @return
     */
    public UserInfo findUserById(long id) {
        return userInfoDao.findUserById(id);
    }

    /***
     * 根据用户手机号查询userInfo信息
     * @param phone
     * @return
     */
    public UserInfo findUserByPhone(String phone) {
        return userInfoDao.findUserByPhone(phone);
    }

    /***
     * 根据第三方平台账号查询用户信息
     * @param otherPlatformType
     * @param otherPlatformKey
     * @return
     */
    public UserInfo findUserByOtherPlatform(int otherPlatformType, String otherPlatformKey) {
        return userInfoDao.findUserByOtherPlat(otherPlatformType, otherPlatformKey);
    }

    /***
     * 根据门牌号 查找用户信息
     * @param proType
     * @param houseNumber
     * @return
     */
    public UserInfo findUserByHouseNumber(int proType, String houseNumber) {
        return userInfoDao.findUserByHouseNumber(proType, houseNumber);
    }

    /***
     * 更新用户基本信息
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(UserInfo userInfo) {
        return userInfoDao.update(userInfo);
    }

    /***
     * 根据用户ID删除用户
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delete(UserInfo userInfo) {
        return userInfoDao.delete(userInfo);
    }

    /***
     * 搬家更新
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateByHouseMoving(UserInfo userInfo) {
        return userInfoDao.updateByHouseMoving(userInfo);
    }

    /***
     * 更新用户头像
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHead(UserInfo userInfo) {
        return userInfoDao.updateUserHead(userInfo);
    }

    /***
     * 更新用户密码
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changePassWord(UserInfo userInfo) {
        return userInfoDao.changePassWord(userInfo);
    }

    /***
     * 更新用户涂鸦头像
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUserGraffitiHead(UserInfo userInfo) {
        return userInfoDao.updateUserGraffitiHead(userInfo);
    }

    /***
     * 更新用户访问权限
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUserAccessRights(UserInfo userInfo) {
        return userInfoDao.updateUserAccessRights(userInfo);
    }

    /***
     * 更新用户新人红包的标识
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIsNewUser(UserInfo userInfo) {
        return userInfoDao.updateIsNewUser(userInfo);
    }

    /***
     * 更新用户V认证的标识
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateUserCe(UserInfo userInfo) {
        return userInfoDao.updateUserCe(userInfo);
    }
    /***
     * 更新用户找人倾诉状态
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTalkToSomeoneStatus(UserInfo userInfo) {
        return userInfoDao.updateTalkToSomeoneStatus(userInfo);
    }
    /***
     * 更新用户聊天互动状态
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateChatnteractionStatus(UserInfo userInfo) {
        return userInfoDao.updateChatnteractionStatus(userInfo);
    }

    /***
     * 修改生活圈首次视频发布状态
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHomeBlogStatus(UserInfo userInfo) {
        return userInfoDao.updateHomeBlogStatus(userInfo);
    }

    /***
     * 修改用户账号状态 正常 停用
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateAccountStatus(UserInfo userInfo) {
        return userInfoDao.updateAccountStatus(userInfo);
    }

    /***
     * 修改新用户系统欢迎消息状态接口
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateWelcomeInfoStatus(UserInfo userInfo) {
        return userInfoDao.updateWelcomeInfoStatus(userInfo);
    }

    /***
     * 更新用户手机号绑定状态
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBindPhone(UserInfo userInfo) {
        return userInfoDao.updateBindPhone(userInfo);
    }

    /***
     * 更新用户第三方平台账号绑定状态
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBindOther(UserInfo userInfo) {
        return userInfoDao.updateBindOther(userInfo);
    }

    /***
     * 完善资料
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int perfectUserInfo(UserInfo userInfo) {
        return userInfoDao.perfectUserInfo(userInfo);
    }

    /***
     * 分页条件查询
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<UserInfo> findList(String name, int beginAge, int endAge, int sex, int province, int city,
                                       int district, int studyrank, int maritalstatus,int talkToSomeoneStatus,int chatnteractionStatus,
                                       int page, int count) {
        //对可为空的参数进行特殊处理 避免mybatis空值拼接检索匹配不到的问题
        if (CommonUtils.checkFull(name)) {
            name = null;
        }
        List<UserInfo> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = userInfoDao.findList(name, beginAge, endAge, sex, province, city, district, studyrank, maritalstatus,talkToSomeoneStatus,chatnteractionStatus);
        for (int i = 0; i < list.size(); i++) {
            UserInfo u = list.get(i);
            if (u != null) {
                u.setPassword("");//过滤登录密码
                u.setIm_password("");//过滤环信登录密码
            }
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 条件查找用户信息
     * @return
     */
    public List<UserInfo> findCondition() {
        List<UserInfo> list;
        list = userInfoDao.findCondition();
        return list;
    }

    /***
     * 更新用户代言人身份标识
     * @param userInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateSpokesmanStatus(UserInfo userInfo) {
        return userInfoDao.updateSpokesmanStatus(userInfo);
    }
}
