package com.mofancn.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.FtpUtil;
import com.mofancn.common.utils.IDUtils;
import com.mofancn.common.utils.JsonUtils;
import com.mofancn.common.utils.MofancnResult;
import com.mofancn.mapper.MfUserMapper;
import com.mofancn.pojo.MfUser;
import com.mofancn.sso.service.uploadPictureService;

@Service
public class uploadPictureServiceImpl implements uploadPictureService {

	@Value("${FTP_ADDRESS}")
	private String FTP_ADDRESS;
	@Value("${FTP_PORT}")
	private Integer FTP_PORT;
	@Value("${FTP_USERNAME}")
	private String FTP_USERNAME;
	@Value("${FTP_PASSWORD}")
	private String FTP_PASSWORD;
	@Value("${FTP_BASEPATH}")
	private String FTP_BASEPATH;
	@Value("${IMAGE_BASEPATH}")
	private String IMAGE_BASEPATH;
	
	@Value("${SSO_SESSION_EXPIRE}")
	private int SSO_SESSION_EXPIRE;
	
	@Value("${USER_SESSION_REDIS_KEY}")
	private String USER_SESSION_REDIS_KEY;
	@Autowired
	private jedisClient jedisClient;
	@Autowired
	private MfUserMapper MfUserMapper;
	
	@Override
	public MofancnResult uploadPictureByInputFile(MultipartFile inputImageFile,String token) {
		//
		
		String string = jedisClient.get(USER_SESSION_REDIS_KEY + ":" + token);
		if (StringUtils.isBlank(string)) {
			return MofancnResult.build(500, "登录已过期，请重新登录！");
		}
		MfUser mfUser2 = JsonUtils.jsonToPojo(string, MfUser.class);
		
		MfUser mfUser = MfUserMapper.selectByPrimaryKey(mfUser2.getUserId());
		
		try {
			
			String oldName = inputImageFile.getOriginalFilename();
			String newName = IDUtils.genImageName();
			String imagePath = new DateTime().toString("/yyyy/mm/dd");
			newName = newName + oldName.substring(oldName.lastIndexOf("."));
			System.out.println(FTP_BASEPATH + imagePath);
			boolean uploadFile = FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD, FTP_BASEPATH, imagePath, newName, inputImageFile.getInputStream());
			if (!uploadFile) {
				
				return MofancnResult.build(500, "上传文件失败");
			}
			String newUserImage = IMAGE_BASEPATH + imagePath + "/" + newName;
			mfUser.setUserImage(newUserImage);
			
			MfUserMapper.updateByPrimaryKey(mfUser);
			
			
			mfUser.setUserPassword(null);
			try {
				jedisClient.set(USER_SESSION_REDIS_KEY + ":"+token, JsonUtils.objectToJson(mfUser));
				jedisClient.expire(USER_SESSION_REDIS_KEY + ":" + token, SSO_SESSION_EXPIRE);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return MofancnResult.ok(newUserImage);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return MofancnResult.build(500, "上传发生异常");
		}
		
	}

}
