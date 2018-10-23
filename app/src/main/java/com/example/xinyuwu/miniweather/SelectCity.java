package com.example.xinyuwu.miniweather;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.xinyuwu.app.MyApplication;
import com.example.xinyuwu.bean.City;
import com.example.xinyuwu.bean.MyAdapter;
import com.example.xinyuwu.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private ClearEditText mClearEditText;
    private List<City> cityList;
    private ArrayList<City> filterDataList=new ArrayList<City>(0);
    private ListView mList;
    private MyAdapter myadapter;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.select_city);
        initViews();
        /*mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);*/
        /*mClearEditText=(ClearEditText)findViewById(R.id.search_city);
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
        });*/
    }

    /*private void filterData(String filterStr){//根据输入过滤数据
        filterDataList=new ArrayList<City>();
        Log.d(TAG,filterStr);
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
        //myadapter.updateListView(filterDataList);
        //myadapter.setNotifyOnChange(filterDataList);
    }*/
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.title_back:
                Intent intent=new Intent();
                intent.putExtra("cityCode","101160101");
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }//按返回输出兰州的城市码
    private void initViews(){
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mClearEditText=(ClearEditText)findViewById(R.id.search_city);
        MyApplication myApplication =(MyApplication)getApplication();
        cityList=myApplication.getCityList();
        /*for(City city:cityList){
            filterDataList.add(city);
        }*/
        myadapter=new MyAdapter(SelectCity.this,cityList);
        mList=(ListView)findViewById(R.id.title_list);
        mList.setAdapter(myadapter);
        /*mList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long l){
                City city=filterDataList.get(position);
                Intent i=new Intent();
                i.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK,i);
                finish();
            }
        });*/

    }
}
