package com.mofancn.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.CookieUtils;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.MfUserMapper;
import com.mofancn.pojo.MfUser;
import com.mofancn.pojo.MfUserExample;
import com.mofancn.pojo.MfUserExample.Criteria;
import com.mofancn.sso.pojo.SmsDemo;
import com.mofancn.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	// private TbUserMapper TbUserMapper;
	private MfUserMapper MfUserMapper;

	@Autowired
	private jedisClient jedisClient;

	@Value("${SMS_REGISTER_REDIS_KEY}")
	private String SMS_REGISTER_REDIS_KEY;
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private int SSO_SESSION_EXPIRE;
	@Value("${TOKEN_MAXAGE}")
	private int TOKEN_MAXAGE;
	@Value("${WEIXIN_ID_TOKEN_KEY}")
	private String WEIXIN_ID_TOKEN_KEY;

	@Override
	public MofancnResult checkPhone(String phone) {
		// TODO Auto-generated method stub
		// TbUserExample example = new TbUserExample();
		MfUserExample example = new MfUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserPhoneEqualTo(phone);
		List<MfUser> list = MfUserMapper.selectByExample(example);
		if (list.isEmpty()) {
			return MofancnResult.ok();
		}
		return MofancnResult.build(500, "手机号已注册！");
		// Criteria criteria = example.createCriteria();
		// criteria.andPhoneEqualTo(phone);
		// List<TbUser> list = TbUserMapper.selectByExample(example);
		// if (list.isEmpty()) {
		// TbUser tbUser = new TbUser();
		// tbUser.setPhone(phone);
		// creatUser(tbUser);

		// }
		// return MofancnResult.ok(false);
	}

	@Override
	public MofancnResult checkCodeId(String phone, String codeId) {
		String hget = jedisClient.get(SMS_REGISTER_REDIS_KEY + ":" + phone);
		if (hget == null) {
			return MofancnResult.build(500, "验证码为空或已过期！");
		} else if (!hget.equals(codeId)) {
			return MofancnResult.build(500, "验证码错误！");

		}

		return MofancnResult.ok();
	}

	@Override
	public MofancnResult creatUser(MfUser mfUser, String code) {
		// TODO Auto-generated method stub
		MofancnResult result = new MofancnResult();
		result = checkCodeId(mfUser.getUserPhone(), code);
		if (result.getStatus() != 200) {
			return result;
		}
		result = checkPhone(mfUser.getUserPhone());
		if (result.getStatus() != 200) {
			return result;
		}
		mfUser.setUserGroup((byte) 1);
		mfUser.setUserNickName(UUID.randomUUID().toString());
		mfUser.setUserImage("/images/test.image");
		mfUser.setUserRegisterTime(new Date());
		mfUser.setUserUpdateTime(new Date());
		System.out.println(JsonUtils.objectToJson(mfUser));

		mfUser.setUserPassword(DigestUtils.md5DigestAsHex(mfUser.getUserPassword().getBytes()));

		System.out.println(JsonUtils.objectToJson(mfUser));
		MfUserMapper.insert(mfUser);
		return MofancnResult.ok();
	}

	@Override
	public MofancnResult userLogin(MfUser mfUser,HttpServletRequest request,HttpServletResponse response) {
		// TODO Auto-generated method stub
//		TbUserExample example = new TbUserExample();
		MfUserExample example = new MfUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserPhoneEqualTo(mfUser.getUserPhone());
		List<MfUser> list = MfUserMapper.selectByExample(example);

		if (list.isEmpty()) {
			return MofancnResult.build(500, "请先注册！");
			
		} 
		MfUser mfUser2 = list.get(0);

		
		if (!DigestUtils.md5DigestAsHex(mfUser.getUserPassword().getBytes()).equals(mfUser2.getUserPassword())) {
			return MofancnResult.build(500, "密码错误！");
		}
		String token = UUID.randomUUID().toString();
		mfUser2.setUserPassword(null);
		jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(mfUser2));
//		tbUser.setPassword(null);
//		jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(tbUser));
		jedisClient.expire(USER_SESSION_REDIS_KEY + ":"+token, SSO_SESSION_EXPIRE);
		CookieUtils.setCookie(request, response, "MF_TOKEN", token);
		return MofancnResult.ok(token);
	}

	@Override
	public MofancnResult getUserByToken(String token) {
		// TODO Auto-generated method stub
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		jedisClient.expire(USER_SESSION_REDIS_KEY + ":" + token, SSO_SESSION_EXPIRE);
		//return MofancnResult.ok(JsonUtils.jsonToPojo(string, MfUser.class));
		return MofancnResult.ok(string);
	}

	@Override
	public MofancnResult logoutByToken(String token) {
		// TODO Auto-generated method stub
		long del = jedisClient.del(USER_SESSION_REDIS_KEY + ":" + token);

		return MofancnResult.ok();
	}

	@Override
	public MofancnResult updateByToken(MfUser mfUser,String token) {
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser2 = JsonUtils.jsonToPojo(string, MfUser.class);
		if (mfUser.getUserId() != mfUser2.getUserId()) {
			return MofancnResult.build(500, "TOKEN值不匹配，用户非法修改！");
		}
		mfUser2.setUserUpdateTime(new Date());
		if (!mfUser.getUserEmail().isEmpty()) {
			mfUser2.setUserEmail(mfUser.getUserEmail());
		}
		if (mfUser.getUserGroup() != mfUser2.getUserGroup()) {
			mfUser2.setUserGroup(mfUser.getUserGroup());
		}
		if (!mfUser.getUserImage().isEmpty()) {
			mfUser2.setUserImage(mfUser.getUserImage());
		}
		if (!mfUser.getUserNickName().isEmpty()) {
			mfUser2.setUserNickName(mfUser.getUserNickName());
		}
		if (!mfUser.getUserPassword().isEmpty()) {
			mfUser2.setUserPassword(DigestUtils.md5DigestAsHex(mfUser.getUserPassword().getBytes()));
		}
		try {
			MfUserMapper.updateByPrimaryKey(mfUser2);			
		} catch (Exception e) {
			System.out.println("修改用户数据失败！");
			e.printStackTrace();
			return MofancnResult.build(500, "修改用户数据失败！");
			// TODO: handle exception
		}
		mfUser2.setUserPassword(null);
		try {
			jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(mfUser2));
			jedisClient.expire(USER_SESSION_REDIS_KEY + ":" + token, SSO_SESSION_EXPIRE);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		return MofancnResult.ok(JsonUtils.objectToJson(mfUser2));
	}

	@Override
	public MofancnResult forgetPassword(MfUser mfUser, String codeId) {
		// TODO Auto-generated method stub
		MofancnResult result = new MofancnResult();
		MfUserExample userExample = new MfUserExample();
		Criteria criteria = userExample.createCriteria();
		criteria.andUserPhoneEqualTo(mfUser.getUserPhone());
		List<MfUser> list = MfUserMapper.selectByExample(userExample);
		if (list.isEmpty()) {
			return MofancnResult.build(500, "用户不存在，请注册！");
		}
		MfUser mfUser2 = list.get(0);
		result = checkCodeId(mfUser.getUserPhone(),codeId);
		
		if (!mfUser.getUserPassword().isEmpty()) {
			mfUser2.setUserPassword(DigestUtils.md5DigestAsHex(mfUser.getUserPassword().getBytes()));
		}
		try {
			MfUserMapper.updateByPrimaryKey(mfUser2);			
		} catch (Exception e) {
			System.out.println("修改用户数据失败！");
			e.printStackTrace();
			return MofancnResult.build(500, "修改用户数据失败！");
			// TODO: handle exception
		}
		mfUser2.setUserPassword(null);
		result= MofancnResult.ok(JsonUtils.objectToJson(mfUser2));
		return result;
	}

	@Override
	public MofancnResult userLoginByWeixin(String userWeixinId,HttpServletRequest request,HttpServletResponse response) {

		try {
			String token1 = jedisClient.get(WEIXIN_ID_TOKEN_KEY + ":" + userWeixinId);
			
			if (!token1.equals(null)) {
				return MofancnResult.ok(token1);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		MfUserExample mfUserExample = new MfUserExample();
		Criteria criteria = mfUserExample.createCriteria();
		criteria.andUserWeixinIdEqualTo(userWeixinId);
		List<MfUser> list = MfUserMapper.selectByExample(mfUserExample);
		if (list.get(0).equals(null)) {
			return MofancnResult.build(500, "用户还未注册");
		}
		
		MfUser mfUser = list.get(0);
		String token = UUID.randomUUID().toString();
		mfUser.setUserPassword(null);
		try {
			jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(mfUser));
			jedisClient.set(WEIXIN_ID_TOKEN_KEY + ":" + userWeixinId, token);
//		tbUser.setPassword(null);
//		jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(tbUser));
//		jedisClient.expire(USER_SESSION_REDIS_KEY + ":"+token, SSO_SESSION_EXPIRE);
			CookieUtils.setCookie(request, response, "MF_TOKEN", token);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return MofancnResult.build(500, "微信登录写redis失败");
		}
		return MofancnResult.ok(token);
		
	}

}
