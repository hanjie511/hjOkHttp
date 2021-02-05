# hjOkHttp
自己封装的一个基于okhttp的网络请求工具类
## Gradle  
implementation 'com.github.hanjie511:hjOkHttp:1.0.0'  
## 如何使用 
* Step1 实现OkHttpTools.RequestResult接口
* Step2 调用OkHttpTools工具类进行网络请求  
## sample  
```java  
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
    public void onRequestSuccess(String eventMsg, String responseStr) {//response.code()==200时回调
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
    public void onResponseError(String responseStr) {//网络请求失败的回调

    }

    @Override
    public void onError(String errorMsg) {//网络请求失败的回调

    }
}  
```
