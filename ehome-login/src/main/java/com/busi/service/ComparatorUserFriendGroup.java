package com.busi.service;

import java.text.Collator;
import java.util.Comparator;
import com.busi.entity.UserRelationShip;
import com.busi.utils.CommonUtils;

/** 
 *
 * @author SunTianJie 
 *
 * @version create time：2015-6-16 下午5:05:16 
 * 
 */
public class ComparatorUserFriendGroup implements Comparator<UserRelationShip>{

	/***
	 * 优先根据备注排序 再按用户名排序
	 */
	@Override
	public int compare(UserRelationShip s1, UserRelationShip s2) {
		Collator collator = Collator.getInstance(java.util.Locale.CHINA);
		if(!CommonUtils.checkFull(s1.getRemarkName())&&!CommonUtils.checkFull(s2.getRemarkName())){
			return collator.getCollationKey(s1.getRemarkName()).compareTo(collator.getCollationKey(s2.getRemarkName()));
		}
		if(!CommonUtils.checkFull(s1.getRemarkName())&&CommonUtils.checkFull(s2.getRemarkName())){
			return collator.getCollationKey(s1.getRemarkName()).compareTo(collator.getCollationKey(s2.getName()));
		}
		if(CommonUtils.checkFull(s1.getRemarkName())&&!CommonUtils.checkFull(s2.getRemarkName())){
			return collator.getCollationKey(s1.getName()).compareTo(collator.getCollationKey(s2.getRemarkName()));
		}
		if(CommonUtils.checkFull(s1.getRemarkName())&&CommonUtils.checkFull(s2.getRemarkName())){
			return collator.getCollationKey(s1.getName()).compareTo(collator.getCollationKey(s2.getName()));
		}
		return 0;
	}
}
