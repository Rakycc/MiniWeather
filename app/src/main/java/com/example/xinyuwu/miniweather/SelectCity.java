package com.example.xinyuwu.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinyuwu.app.MyApplication;
import com.example.xinyuwu.bean.City;
import com.example.xinyuwu.bean.MyAdapter;
import com.example.xinyuwu.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * SelectCity Activity显示城市列表
 * 并向MainActivity返回用户选择的城市
 */

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private ClearEditText mClearEditText;
    private List<City> cityList;
    private ArrayList<City> filterDataList=new ArrayList<City>(0);
    private ListView mList;
    private MyAdapter myadapter;
    private TextView mNowCity;
    private String returnCityCode;
    private String returnCityName;

    /**
     * 重写父类onCreate方法
     * 初始化选择城市的界面
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.select_city);
        initViews();
        mClearEditText=(ClearEditText)findViewById(R.id.search_city);
        mList=(ListView)findViewById(R.id.title_list);
        mClearEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){
                filterData(s.toString());
                mList.setAdapter(myadapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s,int start,int before,int count){
            }
        });
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            /**
             * 重写onItemClick方法
             * 返回当前点击的城市信息
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long l){
                //City city=filterDataList.get(position);
                City city=filterDataList.get(position);
                returnCityCode=city.getNumber();
                returnCityName=city.getCity();
                mNowCity.setText("当前城市："+returnCityName);
            }
        });
    }

    private void filterData(String filterStr){//根据输入过滤数据
        filterDataList=new ArrayList<City>();
        if(TextUtils.isEmpty(filterStr)){
            for(City city:cityList){
                filterDataList.add(city);
            }
        }
        else{
            filterDataList.clear();
            for(City city:cityList){
                if(city.getCity().indexOf(filterStr.toString())!=-1){
                    filterDataList.add(city);
                }
            }
        }
        myadapter=new MyAdapter(SelectCity.this,filterDataList);
    }

    /**
     * 重写父类onCLick方法
     * 如果点按back按钮，在已选择城市的情况下更新SharedPreferences
     * 并返回Intent和附加信息，最后结束Activity
     *
     * @param view
     */
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.title_back:
                if(returnCityCode!="") {
                    SharedPreferences.Editor editor = getSharedPreferences("config", MODE_PRIVATE).edit();
                    editor.putString("main_city_code", returnCityCode);
                    editor.putString("main_city_name", returnCityName);
                    editor.apply();
                }
                Intent intent=new Intent();
                intent.putExtra("cityCode",returnCityCode);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 界面初始化
     * 从myApplication中获取所有的城市列表
     * 在LisView中显示所有城市
     */
    private void initViews(){
        mNowCity=(TextView)findViewById(R.id.title_name);
        SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        String cityCode=sharedPreferences.getString("main_city_name","北京");
        mNowCity.setText("当前城市："+cityCode);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mClearEditText=(ClearEditText)findViewById(R.id.search_city);
        MyApplication myApplication =(MyApplication)getApplication();
        cityList=myApplication.getCityList();
        /*for(City city:cityList){
            filterDataList.add(city);
        }*/
        for(City city:cityList){
            filterDataList.add(city);
        }
        myadapter=new MyAdapter(SelectCity.this,cityList);
        mList=(ListView)findViewById(R.id.title_list);
        mList.setAdapter(myadapter);
        /*mList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            /**
             * 重写onItemClick方法
             * 返回当前点击的城市信息

            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long l){
                //City city=filterDataList.get(position);
                City city=cityList.get(position);
                returnCityCode=city.getNumber();
                returnCityName=city.getCity();
                mNowCity.setText("当前城市："+returnCityName);
            }
        });*/

    }
}
