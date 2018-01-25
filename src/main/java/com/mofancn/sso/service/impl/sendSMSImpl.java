package com.mofancn.sso.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.TbUserMapper;
import com.mofancn.pojo.TbUser;
import com.mofancn.pojo.TbUserExample;
import com.mofancn.pojo.TbUserExample.Criteria;
import com.mofancn.sso.service.sendSMS;
@Service
public class sendSMSImpl implements sendSMS {

	@Autowired
	private jedisClient jedisClient;
	@Autowired
	private TbUserMapper TbUserMapper;
	
	
	@Value("${SMS_REGISTER_REDIS_KEY}")
	private  String SMS_REGISTER_REDIS_KEY;
	@Value("${SMS_REGISTER_EXPIRE}")
	private  String SMS_REGISTER_EXPIRE;

	
	String product = "Dysmsapi";
	
	String domain = "dysmsapi.aliyuncs.com";
	
	
	String accessKeyId = "LTAI7dPVbcu3tgWb";
	String accessKeySecret = "weugflsFzQmdVzxAaosZqd45hn9ocO";
	@Override
	public SendSmsResponse sendSms(String phone) throws ClientException {
		
		
		// TODO Auto-generated method stub
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		System.out.println("send");
		
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//生成4位随机数字
		String str = "123456789";
		StringBuilder sb = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		
		String codeId = "{\"code\":\"" + sb.toString() + "\"}";

		SendSmsRequest request = new SendSmsRequest();
		HashMap<String,String> hashMap = new HashMap<String, String>();
		hashMap.put("codeId", codeId);
		System.out.println(hashMap.get("codeId"));
		System.out.println("hashmap");
		request.setMethod(MethodType.POST);
		request.setPhoneNumbers(phone);
		request.setSignName("漠帆");
		request.setTemplateCode("SMS_105845112");
		request.setTemplateParam(hashMap.get("codeId"));

		try {
			jedisClient.set(SMS_REGISTER_REDIS_KEY+":"+phone,sb.toString());
			jedisClient.expire(SMS_REGISTER_REDIS_KEY+":"+phone,600);
			System.out.println(phone);
			System.out.println("jedis is ok!");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

		return sendSmsResponse;
	}
	public  QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

		
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		
		QuerySendDetailsRequest request = new QuerySendDetailsRequest();
	
		request.setPhoneNumber("13723858886");
		
		request.setBizId(bizId);

		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		request.setSendDate(ft.format(new Date()));

		request.setPageSize(10L);

		request.setCurrentPage(1L);


		QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

		return querySendDetailsResponse;
	}

}
