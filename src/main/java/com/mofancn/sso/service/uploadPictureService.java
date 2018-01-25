package com.mofancn.sso.service;

import org.springframework.web.multipart.MultipartFile;

import com.mofancn.common.utils.MofancnResult;

public interface uploadPictureService {
	MofancnResult uploadPictureByInputFile(MultipartFile inputImageFile,String token);

}
