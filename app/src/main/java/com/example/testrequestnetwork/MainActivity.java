package com.example.testrequestnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myokhttp.OkHttpTools;

import okhttp3.FormBody;

public class MainActivity extends AppCompatActivity implements OkHttpTools.RequestResult {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
    }
    public void sendClick(View v){
     String url="http://47.106.46.147:8080/ylt/app/querySearchDamage.html?pageNo=1&damageType=B01&damageStatus=-2&cityCode=101280101&pageSize=10&content=&repairTeam=00&readOwn=0&cityCode=101280101&userID=gz&colUserId=20201215091350341665DBCBCCAAD047&officeCode=0001";
     new OkHttpTools(OkHttpTools.RequestType_getData,url,"请求数据",this);
    }
    public void postClick(View v){
        String url="http://192.168.1.99:8080/ylt/app/testFormBody.html";
        new OkHttpTools(OkHttpTools.RequestType_postFormData,url,getFormBody(),"提交数据",this);
    }
    private FormBody getFormBody(){
        FormBody.Builder builder=new FormBody.Builder();
        builder.add("id","1234567");
        builder.add("damnum","1234567");
        builder.add("damkinds","1234567");
        builder.add("damtype","1234567");
        return builder.build();
    }
    @Override
    public void onRequestSuccess(String eventMsg, String responseStr) {
        System.out.println("responseStr:"+responseStr);
        if("请求数据".equals(eventMsg)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(responseStr);
                }
            });
        }else if("提交数据".equals(eventMsg)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("数据提交成功");
                }
            });
        }
    }

    @Override
    public void onResponseError(String responseStr) {

    }

    @Override
    public void onError(String errorMsg) {

    }
}