package com.mofancn.sso.pojo;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.dysmsapi.transform.v20170525.SendSmsResponseUnmarshaller;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;


import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import com.mofancn.common.pojo.*;
import com.mofancn.common.utils.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created on 17/6/7. 閻厺淇夾PI娴溠冩惂閻ㄥ嚍EMO缁嬪绨�,瀹搞儳鈻兼稉顓炲瘶閸氼偂绨℃稉锟芥稉鐚刴sDemo缁紮绱濋惄瀛樺复闁俺绻�
 * 閹笛嗩攽main閸戣姤鏆熼崡鍐插讲娴ｆ捇鐛欓惌顓濅繆娴溠冩惂API閸旂喕鍏�(閸欘亪娓剁憰浣哥殺AK閺囨寧宕查幋鎰磻闁矮绨℃禍鎴︼拷姘繆-閻厺淇婃禍褍鎼ч崝鐔诲厴閻ㄥ嚈K閸楀啿褰�) 瀹搞儳鈻兼笟婵婄娴滐拷2娑撶尐ar閸栵拷(鐎涙ɑ鏂侀崷銊ヤ紣缁嬪娈憀ibs閻╊喖缍嶆稉锟�)
 * 1:aliyun-java-sdk-core.jar 2:aliyun-java-sdk-dysmsapi.jar
 *
 * 婢跺洦鏁�:Demo瀹搞儳鈻肩紓鏍垳闁插洨鏁TF-8 閸ヤ粙妾惌顓濅繆閸欐垿锟戒浇顕崟鍨棘閻撗勵劃DEMO
 */

public class SmsDemo {
	@Autowired
	private static jedisClient jedisClient;
	
	@Value("${SMS_REGISTER_REDIS_KEY}")
	private static String SMS_REGISTER_REDIS_KEY;

	// 娴溠冩惂閸氬秶袨:娴滄垿锟芥矮淇婇惌顓濅繆API娴溠冩惂,瀵拷閸欐垼锟藉懏妫ら棁锟介弴鎸庡床
	static final String product = "Dysmsapi";
	// 娴溠冩惂閸╃喎鎮�,瀵拷閸欐垼锟藉懏妫ら棁锟介弴鎸庡床
	static final String domain = "dysmsapi.aliyuncs.com";
	
	// TODO 濮濄倕顦╅棁锟界憰浣规禌閹广垺鍨氬锟介崣鎴ｏ拷鍛板殰瀹歌京娈慉K(閸︺劑妯嬮柌灞肩隘鐠佸潡妫堕幒褍鍩楅崣鏉款嚢閹碉拷)
	static final String accessKeyId = "LTAI7dPVbcu3tgWb";
	static final String accessKeySecret = "weugflsFzQmdVzxAaosZqd45hn9ocO";
	
	public static SendSmsResponse sendSms(String phone) throws ClientException {

		// 閸欘垵鍤滈崝鈺勭殶閺佺绉撮弮鑸垫闂傦拷
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		System.out.println("send");
		
		// 閸掓繂顫愰崠鏈糲sClient,閺嗗倷绗夐弨顖涘瘮region閸栵拷
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
		// 缂佸嫯顥婄拠閿嬬湴鐎电钖�-閸忚渹缍嬮幓蹇氬牚鐟欎焦甯堕崚璺哄酱-閺傚洦銆傞柈銊ュ瀻閸愬懎顔�
		SendSmsRequest request = new SendSmsRequest();
		HashMap<String,String> hashMap = new HashMap<String, String>();
		hashMap.put("codeId", codeId);
		System.out.println(hashMap.get("codeId"));
		System.out.println("hashmap");
		request.setMethod(MethodType.POST);
		// 韫囧懎锝�:瀵板懎褰傞柅浣瑰閺堝搫褰�
		request.setPhoneNumbers(phone);
		// 韫囧懎锝�:閻厺淇婄粵鎯ф倳-閸欘垰婀惌顓濅繆閹貉冨煑閸欓鑵戦幍鎯у煂
		request.setSignName("漠帆");
		// 韫囧懎锝�:閻厺淇婂Ο鈩冩緲-閸欘垰婀惌顓濅繆閹貉冨煑閸欓鑵戦幍鎯у煂
		request.setTemplateCode("SMS_105845112");
		// 閸欘垶锟斤拷:濡剝婢樻稉顓犳畱閸欐﹢鍣洪弴鎸庡床JSON娑擄拷,婵″倹膩閺夊灝鍞寸�归�涜礋"娴滆尙鍩嶉惃锟�${name},閹劎娈戞宀冪槈閻椒璐�${code}"閺冿拷,濮濄倕顦╅惃鍕拷闂磋礋
		request.setTemplateParam(hashMap.get("codeId"));
		System.out.println(codeId);
		try {
			jedisClient.hset(SMS_REGISTER_REDIS_KEY, phone,hashMap.get("codeId"));
			//jedisClient.expire(phone, 1800);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 闁锝�-娑撳﹨顢戦惌顓濅繆閹碘晛鐫嶉惍锟�(閺冪姷澹掑▓濠囨付濮瑰倻鏁ら幋鐤嚞韫囩晫鏆愬銈呯摟濞堬拷)
		// request.setSmsUpExtendCode("90997");

		// 閸欘垶锟斤拷:outId娑撶儤褰佹笟娑氱舶娑撴艾濮熼弬瑙勫⒖鐏炴洖鐡у▓锟�,閺堬拷缂佸牆婀惌顓濅繆閸ョ偞澧藉☉鍫熶紖娑擃厼鐨㈠銈咃拷鐓庣敨閸ョ偟绮扮拫鍐暏閼帮拷
		//request.setOutId("yourOutId");

		// hint 濮濄倕顦╅崣顖濆厴娴兼碍濮忛崙鍝勭磽鐢潻绱濆▔銊﹀壈catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

		return sendSmsResponse;
	}

	public static QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

		// 閸欘垵鍤滈崝鈺勭殶閺佺绉撮弮鑸垫闂傦拷
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		// 閸掓繂顫愰崠鏈糲sClient,閺嗗倷绗夐弨顖涘瘮region閸栵拷
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 缂佸嫯顥婄拠閿嬬湴鐎电钖�
		QuerySendDetailsRequest request = new QuerySendDetailsRequest();
		// 韫囧懎锝�-閸欓鐖�
		request.setPhoneNumber("13723858886");
		// 閸欘垶锟斤拷-濞翠焦鎸夐崣锟�
		request.setBizId(bizId);
		// 韫囧懎锝�-閸欐垿锟戒焦妫╅張锟� 閺�顖涘瘮30婢垛晛鍞寸拋鏉跨秿閺屻儴顕楅敍灞剧壐瀵紳yyyMMdd
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		request.setSendDate(ft.format(new Date()));
		// 韫囧懎锝�-妞ら潧銇囩亸锟�
		request.setPageSize(10L);
		// 韫囧懎锝�-瑜版挸澧犳い鐢电垳娴狅拷1瀵拷婵顓搁弫锟�
		request.setCurrentPage(1L);

		// hint 濮濄倕顦╅崣顖濆厴娴兼碍濮忛崙鍝勭磽鐢潻绱濆▔銊﹀壈catch
		QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

		return querySendDetailsResponse;
	}

/*	public static void main(String[] args) throws ClientException, InterruptedException {

		// 閸欐垹鐓穱锟�
		SendSmsResponse response = sendSms();
		System.out.println("閻厺淇婇幒銉ュ經鏉╂柨娲栭惃鍕殶閹癸拷----------------");
		System.out.println("Code=" + response.getCode());
		System.out.println("Message=" + response.getMessage());
		System.out.println("RequestId=" + response.getRequestId());
		System.out.println("BizId=" + response.getBizId());
		System.out.println("Ok1");
		// Thread.sleep(3000L);

		// 閺屻儲妲戠紒锟�
		if (response.getCode() != null && response.getCode().equals("OK")) {
			QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(response.getBizId());
			System.out.println("閻厺淇婇弰搴ｇ矎閺屻儴顕楅幒銉ュ經鏉╂柨娲栭弫鐗堝祦----------------");
			System.out.println("Code=" + querySendDetailsResponse.getCode());
			System.out.println("Message=" + querySendDetailsResponse.getMessage());
			int i = 0;
			for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
					.getSmsSendDetailDTOs()) {
				System.out.println("SmsSendDetailDTO[" + i + "]:");
				System.out.println("Content=" + smsSendDetailDTO.getContent());
				System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
				System.out.println("OutId=" + smsSendDetailDTO.getOutId());
				System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
				System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
				System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
				System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
				System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
			}
			System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
			System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
			System.out.println("Ok2");
		}
		System.out.println("Ok3");

	}*/
}
