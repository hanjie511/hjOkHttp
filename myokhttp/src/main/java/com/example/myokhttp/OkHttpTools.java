package com.example.myokhttp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpTools {
	public static final String RequestType_getData="getData";
	public static final String RequestType_postFormData="postFormData";
	public static final String RequestType_postFile="postFile";
	private FormBody formBody;//Ҫ������̨��formBody
	private String requestUrl;//���������
	private String requestType;//�����ĸ��������������
	private String eventMsg="";
	private RequestBody requestBody;
	private  RequestResult requestResult;
	private List<String> fileList;
	private String MediaTypeStr;
	/*
	 * get��ʽ��������
	 */

	/**
	 *
	 * @param requestType  ��������
	 * @param requestUrl   ���������
	 * @param eventMsg	   ����ص�ʱ����Ϣ��ʶ
	 * @param requestResult ����ص��Ľӿڶ���
	 */
	public OkHttpTools(String requestType, String requestUrl, String eventMsg,RequestResult requestResult) {
		this.requestType=requestType;
		this.requestUrl=requestUrl;
		this.eventMsg=eventMsg;
		this.requestResult=requestResult;
		RequestData(requestType);
	}
	/*
	 * post��ʽ���ύ����
	 */

	/**
	 *
	 * @param requestType  ���������
	 * @param requestUrl   ���������
	 * @param formBody     �ύ��formbody
	 * @param eventMsg     ����ص�ʱ����Ϣ��ʶ
	 * @param requestResult  ����ص�ʱ�Ľӿڶ���
	 */
	public OkHttpTools(String requestType, String requestUrl, FormBody formBody, String eventMsg,RequestResult requestResult) {
		this.requestType=requestType;
		this.requestUrl=requestUrl;
		this.formBody=formBody;
		this.eventMsg=eventMsg;
		this.requestResult=requestResult;
		RequestData(requestType);
	}

	/**
	 *
	 * @param requestType ���������
	 * @param requestUrl  ���������
	 * @param fileList    �ϴ��ļ�·���ļ���
	 * @param MediaTypeStr  ����MediaType
	 * @param eventMsg      ����ص�ʱ����Ϣ��ʶ
	 * @param requestResult  ����ص�ʱ�Ľӿڶ���
	 */
	public OkHttpTools(String requestType, String requestUrl, List<String> fileList,String MediaTypeStr, String eventMsg, RequestResult requestResult) {
		this.requestType=requestType;
		this.requestUrl=requestUrl;
		this.formBody=formBody;
		this.eventMsg=eventMsg;
		this.requestResult=requestResult;
		this.fileList=fileList;
		this.MediaTypeStr=MediaTypeStr;
		RequestData(requestType);
	}
	private void RequestData(String requestType) {
		new Thread(new RequestThread(requestType)).start();
	}
	private class RequestThread implements Runnable{
		private String requestType;
		private RequestThread(String reqestType1) {
			requestType=reqestType1;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String requestStr=requestType;
			if("getData".equals(requestStr)) {//ģ�����̨��ȡ����
				getResultByGetToArray(requestUrl);
			}else if("postFormData".equals(requestStr)) {//ģ�����̨�ύ����
				getResultByPost(requestUrl, formBody);
			}else if("postFile".equals(requestStr)) {//ģ�����̨�ύ����
				uploadFile(requestUrl,fileList);
			}
		}

	}
	private void uploadFile(String url,List<String> fileList){
		int currentNum=0;
		for(int index=0;index<fileList.size();index++){
			boolean flag=postFile(url, fileList.get(index),MediaTypeStr);
			if(!flag){
				break;
			}else{
				currentNum++;
			}
		}
		if(currentNum==fileList.size()){
			requestResult.onRequestSuccess("�ļ��ϴ��ɹ�","�ļ��ϴ��ɹ�");
		}else{
			requestResult.onResponseError("�ļ��ϴ�ʧ��");
			requestResult.onError("�ļ��ϴ�ʧ��");
		}
	}
	private boolean postFile(String url,String filePath,String MediaTypeStr) {
		boolean flag=false;
		try{
			OkHttpClient okHttpClient = new OkHttpClient();
			MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
			MediaType mediaType=MediaType.Companion.parse(MediaTypeStr);
			//����ļ�·��Ϊ��filePath���ļ�
			File file = new File(filePath);
			//���������� header
			RequestBody body = RequestBody.Companion.create(file,mediaType);
			builder.addFormDataPart("file", filePath, body);
			MultipartBody postBody = builder.build();
			//���������� footer
			//��ʼ����
			Request request = new Request.Builder().url(url).post(postBody).build();
			Response response = okHttpClient.newCall(request).execute();
			if(response.code()==200){
				flag=true;
			}else{
				flag=false;
			}
		}catch (Exception e){
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}

	private void getResultByGetToArray(String url) {
		OkHttpClient client=new OkHttpClient();
		Request request=new Request.Builder().url(url).build();
		Call call=client.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response resp)  {//���ʳɹ�
				// TODO Auto-generated method stub
				try {
					String str=resp.body().string();
					if(resp.code()==200){
						requestResult.onRequestSuccess(eventMsg,str);
					}else{
						requestResult.onResponseError(str);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					requestResult.onError("�����������");
				}
			}
			@Override
			public void onFailure(Call arg0, IOException arg1) {//�������
				// TODO Auto-generated method stub
				requestResult.onError("�����������");
			}
		});
	}
	private void getResultByPost(String url, FormBody formBody) {
		OkHttpClient client=new OkHttpClient();
		Request request=new Request.Builder().url(url).post(formBody).build();
		Call call=client.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response resp) throws IOException {//���ʳɹ�
				// TODO Auto-generated method stub
				try {
					String str=resp.body().string();
					if(200==resp.code()) {
						//��̨����ɹ�����Ӧ������
						requestResult.onRequestSuccess(eventMsg,str);
					}else {
						//��̨����ʧ�ܵ���Ӧ������
						requestResult.onResponseError(str);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					requestResult.onError("�����������");
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {//�������
				// TODO Auto-generated method stub
				requestResult.onError("�����������");
			}
		});
	}
	public interface RequestResult{
		void onRequestSuccess(String eventMsg,String responseStr);
		void onResponseError(String responseStr);
		void onError(String errorMsg);
	}
}
