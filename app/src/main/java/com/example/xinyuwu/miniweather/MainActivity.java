package com.example.xinyuwu.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinyuwu.bean.TodayWeather;
import com.example.xinyuwu.util.NetUtil;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER=1;
    private ImageView mUpdateBtn,mCitySelect;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv,now_temperature_Tv;
    private ImageView weatherImg,pmImg;
    private Handler mHandler=new Handler(){
        /**
         *
         * 处理子线程发来的消息，更新UI界面
         *
         * @param msg 需要更新的天气信息
         */
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case UPDATE_TODAY_WEATHER:
                    if(((TodayWeather) msg.obj).getCity()!="") {
                        updateTodayWeather((TodayWeather) msg.obj);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     *
     * 重写父类Oncreate方法
     * 检查网络并调用initView初始化天气界面
     *
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络正常");
            Toast.makeText(MainActivity.this, "网络正常", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "请检查网络");
            Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    /**
     * 初始化天气界面
     * 将所有文本框的初值设为N/A（下一步改进为将初值设为SharedPreferences中的存储值）
     */
    void initView(){
        city_name_Tv=(TextView)findViewById(R.id.title_city_name);
        cityTv=(TextView)findViewById(R.id.city);
        timeTv=(TextView)findViewById(R.id.time);
        humidityTv=(TextView)findViewById(R.id.humidity);
        now_temperature_Tv=(TextView)findViewById(R.id.now_temperature);//当前温度信息
        weekTv=(TextView)findViewById(R.id.week_today);
        pmDataTv=(TextView)findViewById(R.id.pm_data);
        pmQualityTv=(TextView)findViewById(R.id.pm2_5_quality);
        pmImg=(ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv=(TextView)findViewById(R.id.temperature);
        climateTv=(TextView)findViewById(R.id.climate);
        windTv=(TextView)findViewById(R.id.wind);
        weatherImg=(ImageView)findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        now_temperature_Tv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    /**
     * 重写父类onClick方法
     * 用户点按city_manger时发出Intent，切换至SelectCity的Acticity
     * 用户点按update_button时读取SharedPreferences中的值，检查网络并调用queryWeatherCode从网络获取天气信息
     *
     * @param view
     */
    @Override
    public void onClick(View view){
            if(view.getId()==R.id.title_city_manager){
                Intent intent=new Intent(this,SelectCity.class);
                startActivityForResult(intent,1);
                //startActivity(intent);

            }
            if(view.getId()==R.id.title_update_btn){
                SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
                String cityCode=sharedPreferences.getString("main_city_code","101010100");
                Log.d("myWeather",cityCode);
                if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                    Log.d ("myWeather","网络正常");
                    queryWeatherCode(cityCode);
                }else{
                    Log.d("myWeather","请检查网络");
                    Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
    }

    /**
     *
     * 回调方法，在获得SelectCity的Intent返回结果后进行判断
     * 如果网络正常且返回的城市代码不为空，则从网络获取天气信息
     *
     * @param requestCode 传递至SelectCity的Intent编号
     * @param resultCode SelectCity回传的结果信息
     * @param data Intent内容，包含附加的城市信息
     */
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络正常");
                if(newCityCode!="")
                queryWeatherCode(newCityCode);
            }
            else{
                Log.d("myWeather","请检查网络");
                Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * 根据城市代码生成网址
     * 从该网址获取天气信息
     *
     * @param cityCode 城市代码
     */
   private void queryWeatherCode(String cityCode) {
       final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
       Log.d("myWeather", address);
       /**
        * 在新线程中获取网络信息
        */
       new Thread(new Runnable() {
           @Override
           public void run() {
               TodayWeather todayWeather=null;
               //HttpURLConnection con = null;

               /**
                * 使用OkHttp获取网络数据
                * 调用parseXML解析得到的xml文件
                */
               try {
                   /*//Log.d("myWeather", address);
                   URL url = new URL(address);
                   con = (HttpURLConnection) url.openConnection();
                   con.setRequestMethod("GET");
                   con.setConnectTimeout(8000);
                   con.setReadTimeout(8000);
                   InputStream in = con.getInputStream();
                   //InputStream in=null;
                   Log.d("myWeather", "8000");
                   BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                   StringBuilder response = new StringBuilder();
                   String str;*/
                   OkHttpClient client = new OkHttpClient.Builder()
                           .connectTimeout(500, TimeUnit.MILLISECONDS)
                           .readTimeout(5000, TimeUnit.MILLISECONDS)
                           .build();//防止出现超时问题将读取时间设为5000秒
                   Request request=new Request.Builder().url(address).build();
                   Response response=client.newCall(request).execute();
                   String responseStr=response.body().string();
                   /*while ((str = reader.readLine()) != null) {
                       response.append(str);
                       Log.d("myWeather", str);
                   }
                   String responseStr = response.toString();*/
                   Log.d("myWeather", responseStr);
                   todayWeather=parseXML(responseStr);
                   if(todayWeather!=null){//如果解析成功向主线程发送消息
                       Log.d("myWeather",todayWeather.toString());

                       Message msg=new Message();
                       msg.what=UPDATE_TODAY_WEATHER;
                       msg.obj=todayWeather;
                       mHandler.sendMessage(msg);
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               /*} /*finally {
                   if (con != null) {
                       con.disconnect();
                   }*/
               }
           }
       }).start();
   }

    /**
     * 使用XmlPullParser对xml文本进行解析
     * @param xmldata 从网络获取的xml文本
     * @return 解析完成的信息
     */
   private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dateCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType=xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){//读取到文件结束就停止
                switch(eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG://读到标签开始信息进行判断
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather=new TodayWeather();
                        }
                        if(todayWeather!=null){
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("shidu")){
                                eventType=xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                                Log.d("myWeather","shidu: "+xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("wendu")){
                                eventType=xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                                Log.d("myWeather","wendu: "+xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("pm25")){
                                eventType=xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                                Log.d("myWeather","pm25: "+xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("quality")){
                                eventType=xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                                Log.d("myWeather","quality: "+xmlPullParser.getText());
                            }
                            //以下标签由于有多个信息，只读取今日信息（之后更新为读取后面多天的信息）
                            else if(xmlPullParser.getName().equals("fengxiang")&&fengxiangCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                Log.d("myWeather","fengxiang: "+xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if(xmlPullParser.getName().equals("fengli")&&fengliCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                Log.d("myWeather","fengli: "+xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if(xmlPullParser.getName().equals("date")&&dateCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                Log.d("myWeather","date: "+xmlPullParser.getText());
                                dateCount++;
                            }
                            else if(xmlPullParser.getName().equals("high")&&highCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather","high: "+xmlPullParser.getText());
                                highCount++;
                            }
                            else if(xmlPullParser.getName().equals("low")&&lowCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather","low: "+xmlPullParser.getText());
                                lowCount++;
                            }
                            else if(xmlPullParser.getName().equals("type")&&typeCount==0){
                                eventType=xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                Log.d("myWeather","type: "+xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();//每次读取下一个标签
            }
            }catch(XmlPullParserException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return todayWeather;
   }

    /**
     * 在UI界面中更新天气信息
     * @param todayWeather 解析好的天气信息
     */
   void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        now_temperature_Tv.setText("温度："+todayWeather.getWendu()+"℃");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+'~'+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        /*String pm25result="R.drawable.biz_plugin_weather_"+todayWeather.getpm25Pic(todayWeather.getPm25());
       Log.d("myWeather",pm25result);
       pmImg.setImageDrawable();*/
       /**
        * 根据pm2.5数据更改提示图片
        */
       switch(todayWeather.getpm25Pic(todayWeather.getPm25())) {
            case 1:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
                break;
            case 2:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                break;
            case 3:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                break;
            case 4:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                break;
            case 5:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                break;
            default:
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                break;
        }
       /**
        * 根据天气数据更改提示图片
        */
        switch(todayWeather.getType()){
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
        }
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
   }
}
