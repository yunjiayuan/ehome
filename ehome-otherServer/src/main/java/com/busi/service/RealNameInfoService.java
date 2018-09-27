package com.busi.service;

import com.busi.dao.RealNameInfoDao;
import com.busi.entity.RealNameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 实名认证Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class RealNameInfoService {

    @Autowired
    private RealNameInfoDao realNameInfoDao;

    /***
     * 新增
     * @param realNameInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addRealNameInfo(RealNameInfo realNameInfo){
        return  realNameInfoDao.add(realNameInfo);
    }

    /***
     * 查询实名信息
     * @param realName 真实姓名
     * @param cardNo   身份证号
     * @return
     */
    public List<RealNameInfo> findRealNameInfo(String realName,String cardNo){
        List<RealNameInfo> list = realNameInfoDao.findRealNameInfo(realName,cardNo);
//        RealNameInfo realNameInfo = null;
//        if(list!=null&&list.size()>0){
//            realNameInfo = list.get(0);
//        }
        return list;
    }

}
