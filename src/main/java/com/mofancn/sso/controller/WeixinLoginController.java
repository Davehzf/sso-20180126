package com.mofancn.sso.controller;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.http.HttpRequest;
import com.mofancn.common.utils.HttpClientUtil;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;

import net.sf.json.JSONObject;

@Controller

public class WeixinLoginController {
	
	
	@Value("${APP_ID}")
	private String APP_ID;
	@Value("${APP_SECRET}")
	private String APP_SECRET;
	@Value("${WEIXIN_BACK_URL}")
	private String WEIXIN_BACK_URL;
	
	/**
	 * 公众号请求接口
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @throws IOException
	 */
	@RequestMapping("/weixinlogin")
	
	public void LoginServlet(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws IOException{
		
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + "wx017aa4bd797f4477"
				+ "&redirect_uri=" + URLEncoder.encode("http://sso.mofancn.com/sso/weixinauth/callback")
				+ "&response_type=code"
				+ "&scope=snsapi_userinfo"
				+ "&state=STATE#wechat_redirect";
		httpServletResponse.sendRedirect(url);
		
		
		
	}
	
	
	/**
	 * 回调 地址
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 */
	@RequestMapping("/weixinauth/callback")
	@ResponseBody
	public MofancnResult LoginCallback(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
		
		String code = httpServletRequest.getParameter("code");
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + "wx017aa4bd797f4477"
				+ "&secret=" + "f7f30168085cd7928452e2307b0e6e7f"
				+ "&code=" + code
				+ "&grant_type=authorization_code";
		
		String string = HttpClientUtil.doGet(url, null);
		JSONObject jsonObject = JSONObject.fromObject(string);
		String access_token = jsonObject.getString("access_token");
		String openid = jsonObject.getString("openid");
		
		
		String userInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token
				+ "&openid=" + openid
				+ "&lang=zh_CN";
		
		String string2 = HttpClientUtil.doGet(userInfo, null);
		JSONObject jsonObject2 = JSONObject.fromObject(string2);
		System.out.println(JsonUtils.objectToJson(jsonObject2));
		
		return MofancnResult.ok(JsonUtils.objectToJson(jsonObject2));
	}
	
	/**
	 * 开放平台授权请求(暂时未实现)
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @throws IOException
	 */
	
	@RequestMapping("/openplatformlogin")
	public void openPlatformLoginServlet(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws IOException{
		
		
//		"https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=xxx";
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + "wx017aa4bd797f4477"
				+ "&redirect_uri=" + URLEncoder.encode("http://sso.mofancn.com/sso/weixinauth/callback")
				+ "&response_type=code"
				+ "&scope=snsapi_userinfo"
				+ "&state=STATE#wechat_redirect";
		httpServletResponse.sendRedirect(url);
		
		
		
	}
	/**
	 * 请求微信accesstoken
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 */
	
	@RequestMapping("/getaccesstoken")
	@ResponseBody
	public MofancnResult getAccessToken(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
		MofancnResult mofancnResult = new MofancnResult();
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
				+ "&appid=" + "wx017aa4bd797f4477"
				+ "&secret=" + "f7f30168085cd7928452e2307b0e6e7f";
		String string = HttpClientUtil.doGet(url);
		
		JSONObject object = JSONObject.fromObject(string);
		
		return mofancnResult.ok(JsonUtils.objectToJson(object));
	}
	
	@RequestMapping("/getuserinfobyaccesstoken")
	@ResponseBody
	public MofancnResult getUserInfoByAccessToken(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
		MofancnResult mofancnResult = new MofancnResult();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + "6_i68dg8q2szft1YI4jnwl96iyyjlMrNQAOPWCGIFth3j655z54o-gFL9ptqQcDXSQ8m6_c5-qfajopx7xwsDIHiuvK-qUH22JOQx0l3BHr2cQNSpBrIUZD-ERlV9M9wS3ZdOpxNxiP7pQKubZTAXaAIANUR"
				+ "&openid=" + "oXjgns_u3UjCvdRAPE7LgNM1LOY8"
				+ "&lang=zh_CN";
		String string = HttpClientUtil.doGet(url);
		
		JSONObject object = JSONObject.fromObject(string);
		
		return mofancnResult.ok(JsonUtils.objectToJson(object));
	}
	
	
	
	
	



}
