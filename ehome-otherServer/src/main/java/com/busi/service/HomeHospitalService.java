package com.busi.service;

import com.busi.dao.HomeHospitalDao;
import com.busi.entity.HomeHospital;
import com.busi.entity.PageBean;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 家医馆
 * @author: ZHaoJiaJie
 * @create: 2020-01-07 16:25
 */
@Service
public class HomeHospitalService {

    @Autowired
    private HomeHospitalDao homeHospitalDao;

    /***
     * 新建
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeHospital hourlyWorker) {
        return homeHospitalDao.add(hourlyWorker);
    }

    /***
     * 更新
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeHospital hourlyWorker) {
        return homeHospitalDao.update(hourlyWorker);
    }

    /***
     * 更新营业状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateBusiness(hourlyWorker);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public HomeHospital findByUserId(long userId) {
        return homeHospitalDao.findByUserId(userId);
    }

    /***
     * 更新删除状态
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateDel(hourlyWorker);
    }

    /***
     * 更新帮助人数
     * @param hourlyWorker
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(HomeHospital hourlyWorker) {
        return homeHospitalDao.updateNumber(hourlyWorker);
    }

    /***
     * 查询列表
     * @param department  科室
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<HomeHospital> findList(int watchVideos, long userId, int department, String search, int province, int city, int district, int page, int count) {

        List<HomeHospital> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (department >= 0) {//按科室
            list = homeHospitalDao.findList(watchVideos,userId, department);
        } else {
            String departId = "";
            if (!CommonUtils.checkFull(search)) {
                String[] name = Constants.department;//科室
                //匹配科室
                for (int i = 0; i < name.length; i++) {
                    if (name[i].indexOf(search) >= 0) {
                        if (i < name.length - 1) {
                            departId += i + ",";
                        } else {
                            departId += i;
                        }
                    }
                }
                list = homeHospitalDao.findList2(watchVideos,userId, departId.split(","), search, province, city, district);
            } else {
                list = homeHospitalDao.findList3(watchVideos,userId, province, city, district);
            }
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询列表
     * @param users    用户
     * @return
     */
    public List<HomeHospital> findUsersList(String[] users) {

        List<HomeHospital> list = null;
        list = homeHospitalDao.findUsersList(users);
        return list;
    }
}
