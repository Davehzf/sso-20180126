package com.mofancn.sso.service;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

public interface sendSMS {
	
	
	public SendSmsResponse sendSms(String phone) throws ClientException;
	public QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException;

}
