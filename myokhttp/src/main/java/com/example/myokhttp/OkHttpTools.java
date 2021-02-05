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
	private FormBody formBody;//要传给后台的formBody
	private String requestUrl;//请求的链接
	private String requestType;//调用哪个网络请求的名称
	private String eventMsg="";
	private RequestBody requestBody;
	private  RequestResult requestResult;
	private List<String> fileList;
	private String MediaTypeStr;
	/*
	 * get方式请求数据
	 */

	/**
	 *
	 * @param requestType  请求类型
	 * @param requestUrl   请求的链接
	 * @param eventMsg	   请求回调时的消息标识
	 * @param requestResult 请求回调的接口对象
	 */
	public OkHttpTools(String requestType, String requestUrl, String eventMsg,RequestResult requestResult) {
		this.requestType=requestType;
		this.requestUrl=requestUrl;
		this.eventMsg=eventMsg;
		this.requestResult=requestResult;
		RequestData(requestType);
	}
	/*
	 * post方式表单提交数据
	 */

	/**
	 *
	 * @param requestType  请求的类型
	 * @param requestUrl   请求的链接
	 * @param formBody     提交的formbody
	 * @param eventMsg     请求回调时的消息标识
	 * @param requestResult  请求回调时的接口对象
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
	 * @param requestType 请求的类型
	 * @param requestUrl  请求的链接
	 * @param fileList    上传文件路径的集合
	 * @param MediaTypeStr  设置MediaType
	 * @param eventMsg      请求回调时的消息标识
	 * @param requestResult  请求回调时的接口对象
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
			if("getData".equals(requestStr)) {//模拟向后台获取数据
				getResultByGetToArray(requestUrl);
			}else if("postFormData".equals(requestStr)) {//模拟向后台提交数据
				getResultByPost(requestUrl, formBody);
			}else if("postFile".equals(requestStr)) {//模拟向后台提交数据
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
			requestResult.onRequestSuccess("文件上传成功","文件上传成功");
		}else{
			requestResult.onResponseError("文件上传失败");
			requestResult.onError("文件上传失败");
		}
	}
	private boolean postFile(String url,String filePath,String MediaTypeStr) {
		boolean flag=false;
		try{
			OkHttpClient okHttpClient = new OkHttpClient();
			MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
			MediaType mediaType=MediaType.Companion.parse(MediaTypeStr);
			//获得文件路径为：filePath的文件
			File file = new File(filePath);
			//设置请求体 header
			RequestBody body = RequestBody.Companion.create(file,mediaType);
			builder.addFormDataPart("file", filePath, body);
			MultipartBody postBody = builder.build();
			//设置请求体 footer
			//开始请求
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
			public void onResponse(Call call, Response resp)  {//访问成功
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
					requestResult.onError("网络请求出错");
				}
			}
			@Override
			public void onFailure(Call arg0, IOException arg1) {//网络错误
				// TODO Auto-generated method stub
				requestResult.onError("网络请求出错");
			}
		});
	}
	private void getResultByPost(String url, FormBody formBody) {
		OkHttpClient client=new OkHttpClient();
		Request request=new Request.Builder().url(url).post(formBody).build();
		Call call=client.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response resp) throws IOException {//访问成功
				// TODO Auto-generated method stub
				try {
					String str=resp.body().string();
					if(200==resp.code()) {
						//后台请求成功的相应操作，
						requestResult.onRequestSuccess(eventMsg,str);
					}else {
						//后台请求失败的相应操作，
						requestResult.onResponseError(str);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					requestResult.onError("网络请求出错");
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {//网络错误
				// TODO Auto-generated method stub
				requestResult.onError("网络请求出错");
			}
		});
	}
	public interface RequestResult{
		void onRequestSuccess(String eventMsg,String responseStr);
		void onResponseError(String responseStr);
		void onError(String errorMsg);
	}
}
