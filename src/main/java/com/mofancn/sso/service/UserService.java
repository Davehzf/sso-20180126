package com.mofancn.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;

import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfUser;
import com.mofancn.pojo.TbUser;

public interface UserService {
	public MofancnResult checkPhone(String content);
	public MofancnResult checkCodeId(String phone,String codeId);
	public MofancnResult creatUser(MfUser mfUser,String code);
	public MofancnResult userLogin(MfUser mfUser,HttpServletRequest request,HttpServletResponse response);
	public MofancnResult userLoginByWeixin(String userWeixinId,HttpServletRequest request,HttpServletResponse response);
	public MofancnResult getUserByToken(String token);
	public MofancnResult logoutByToken(String token);
	public MofancnResult updateByToken(MfUser mfUser,String token);
	public MofancnResult forgetPassword(MfUser mfUser,String codeId);

}
