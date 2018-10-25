package com.example.xinyuwu.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.xinyuwu.bean.City;
import com.example.xinyuwu.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 准备城市信息列表
 */

public class MyApplication extends Application {
    private static final String TAG="MyAPP";
    private static MyApplication mApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

    /**
     * 重写onCreate方法
     * 调用initCityList
     */
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->Oncreate");
        mApplication=this;
        mCityDB=openCityDB();
        initCityList();
    }

    /**
     * 初始化城市列表
     */
    private void initCityList(){
        mCityList=new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    /**
     * 存储每个城市的名称和代码
     *
     * @return 成功存储返回true
     */
    private boolean prepareCityList(){
        mCityList=mCityDB.getAllCity();
        int i=0;
        for(City city:mCityList){
            i++;
            String cityName=city.getCity();
            String cityCode=city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }
    public List<City> getCityList(){
        return mCityList;
    }
    public static MyApplication getInstance(){
        return mApplication;
    }

    /**
     * 构建路径
     * 从数据库中读取所有城市信息
     * @return 返回城市列表和存储路径
     */
    private CityDB openCityDB(){
        String path ="/data"
                +Environment.getDataDirectory().getAbsolutePath()
                +File.separator+getPackageName()
                +File.separator+"database1"
                +File.separator+CityDB.CITY_DB_NAME;
        File db=new File(path);
        Log.d(TAG,path);
        if(!db.exists()){//只执行一次，从city.db中读取信息并创建db
            String pathfolder="/data"
                    +Environment.getDataDirectory().getAbsolutePath()
                    +File.separator+getPackageName()
                    +File.separator+"database1"
                    +File.separator;
            File dirFirstFolder=new File(pathfolder);
            if(!dirFirstFolder.exists()) {
                dirFirstFolder.mkdir();
                Log.i(TAG, "mkdirs");
            }
            Log.i(TAG,"db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();//清空缓存区数据
                }
                fos.close();
                is.close();
            }catch(IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this,path);
    }
}
