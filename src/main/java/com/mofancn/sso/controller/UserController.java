package com.mofancn.sso.controller;

import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.pojo.MfUser;
import com.mofancn.pojo.TbUser;
import com.mofancn.sso.service.UserService;
import com.mofancn.sso.service.sendSMS;
import com.mofancn.sso.service.uploadPictureService;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired 
	private uploadPictureService uploadPictureService;
	@Autowired
	private sendSMS sendSMS;

	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ApiOperation(value = "用户注册", httpMethod = "POST", response = MofancnResult.class, notes = "用户注册")
	public MofancnResult userRegister(
			@ApiParam(required = true, value = "手机号码", name = "userPhone") @RequestParam(value = "userPhone") String userPhone,
			@ApiParam(required = true, value = "密码", name = "userPassword") @RequestParam(value = "userPassword") String userPassword,
			@ApiParam(required = false, value = "微信ID", name = "userWeixinId",defaultValue="null") @RequestParam(value = "userWeixinId") String userWeixinId,
			@ApiParam(required = true, value = "验证码", name = "codeId") @RequestParam(value = "codeId") String codeId) {

		MfUser mfUser = new MfUser();
		mfUser.setUserPhone(userPhone);
		mfUser.setUserWeixinId(userWeixinId);
		mfUser.setUserPassword(userPassword);
		MofancnResult result = userService.creatUser(mfUser, codeId);
		return result;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "用户登录", httpMethod = "POST", response = MofancnResult.class, notes = "用户登录")
	public MofancnResult userLogin(
			@ApiParam(required = true, value = "手机号码", name = "userPhone") @RequestParam(value = "userPhone") String userPhone,
			@ApiParam(required = true, value = "密码", name = "userPassword") @RequestParam(value = "userPassword") String userPassword,
			
			HttpServletRequest request, HttpServletResponse response) {

		MfUser mfUser = new MfUser();
		mfUser.setUserPhone(userPhone);
		mfUser.setUserPassword(userPassword);
		
		MofancnResult result = userService.userLogin(mfUser, request, response);
		return result;
	}
	@RequestMapping(value = "/loginbyweixin", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "用户使用微信ID登录", httpMethod = "POST", response = MofancnResult.class, notes = "用户使用微信ID登录")
	public MofancnResult userLoginByWeixin(
			@ApiParam(required = true, value = "微信ID", name = "userWeixinId") @RequestParam(value = "userWeixinId") String userWeixinId,
			
			HttpServletRequest request, HttpServletResponse response) {

		
		
		MofancnResult result = userService.userLoginByWeixin(userWeixinId, request, response);
		return result;
	}

	@RequestMapping(value = "/logout/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "退出登录", httpMethod = "GET", response = MofancnResult.class, notes = "退出登录")
	public MofancnResult userLogoutByToken(
			@ApiParam(required = true, value = "用户TOKEN", name = "token")@PathVariable String token) {

		MofancnResult result = userService.logoutByToken(token);
		return result;
	}

	@RequestMapping(value = "/token/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "获取用户信息BY——TOKEN", httpMethod = "GET", response = MofancnResult.class, notes = "获取用户信息")
	public Object getUserByToken(
			@ApiParam(required = true, value = "用户TOKEN", name = "token")@PathVariable String token,
			String callback) {
		MofancnResult result = userService.getUserByToken(token);
		if (StringUtils.isBlank(callback)) {
			return result;
		}
		// MappingJacksonValue mappingJacksonValue = new
		// MappingJacksonValue(result);
		// mappingJacksonValue.setJsonpFunction(callback);

		// return mappingJacksonValue;
		return result;
	}

	@RequestMapping(value = "/phone", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "发送短信", httpMethod = "GET", response = MofancnResult.class, notes = "发送短信")
	public MofancnResult phoneRegister(
			@ApiParam(required = true, value = "手机号码", name = "phoneNumber") @RequestParam(value = "phoneNumber") String phoneNumber) {
		MofancnResult result = new MofancnResult();
		try {
			SendSmsResponse smsResponse = sendSMS.sendSms(phoneNumber);

			if (smsResponse.getCode().toString().equals("OK")) {

				result.setStatus(200);
				result.setMsg("OK");

			} else {
				result.setStatus(500);
				result.setMsg(smsResponse.getMessage().toString());

			}
			System.out.println("Code=" + smsResponse.getCode());
			System.out.println("Message=" + smsResponse.getMessage());
			System.out.println("RequestId=" + smsResponse.getRequestId());
			System.out.println("BizId=" + smsResponse.getBizId());
		} catch (ClientException e) {
			return MofancnResult.build(500, "error");
		}
		return result;
	}
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "修改用户信息", httpMethod = "POST", response = MofancnResult.class, notes = "修改用户信息")
	public MofancnResult updateUserInfo(
			@ApiParam(required = true, value = "用户ID", name = "userId") @RequestParam(value = "userId") Long userId,
			@ApiParam(value = "邮箱", name = "userEmail") @RequestParam(value = "userEmail") String userEmail,
			@ApiParam(value = "密码", name = "userPassword") @RequestParam(value = "userPassword") String userPassword,
			@ApiParam(value = "头像", name = "userImage") @RequestParam(value = "userImage") String userImage,
			@ApiParam(value = "昵称", name = "userNickName") @RequestParam(value = "userNickName") String userNickName,
			@ApiParam(value = "用户类型", name = "userGroup") @RequestParam(value = "userGroup") Byte userGroup,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token
			){
		MfUser mfUser = new MfUser();
		mfUser.setUserId(userId);
		mfUser.setUserEmail(userEmail);
		mfUser.setUserGroup(userGroup);
		mfUser.setUserImage(userImage);
		mfUser.setUserPassword(userPassword);
		mfUser.setUserNickName(userNickName);
		
		MofancnResult updateByToken = userService.updateByToken(mfUser, token);
		
		return MofancnResult.ok(updateByToken);
			
	}
	@RequestMapping(value = "/forgetpassword",method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "忘记密码", httpMethod = "POST", response = MofancnResult.class, notes = "忘记密码")
	public MofancnResult forgetPassword(
			@ApiParam(required = true, value = "手机号码", name = "userPhone") @RequestParam(value = "userPhone") String userPhone,
			@ApiParam(required = true, value = "密码", name = "userPassword") @RequestParam(value = "userPassword") String userPassword,
			@ApiParam(required = true, value = "验证码", name = "codeId") @RequestParam(value = "codeId") String codeId){
		MfUser mfUser = new MfUser();
		mfUser.setUserPhone(userPhone);
		mfUser.setUserPassword(userPassword);
		
		MofancnResult result = userService.forgetPassword(mfUser, codeId);
		return result;
		
	}
	
	@RequestMapping(value = "/updateuserimage",method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "修改用户头像", httpMethod = "POST", response = MofancnResult.class, notes = "修改用户头像")	
	public MofancnResult updateUserInfo(
			@ApiParam(required = true, value = "图像文件", name = "inputImageFile") @RequestParam(value = "inputImageFile") MultipartFile inputImageFile,
			@ApiParam(required = true, value = "用户TOKEN", name = "token") @RequestParam(value = "token") String token
			){
	
		
		MofancnResult mofancnResult = uploadPictureService.uploadPictureByInputFile(inputImageFile, token);
		
		return mofancnResult;
			
	}
	
}
