package com.example.xinyuwu.bean;

public class TodayWeather {
    private String city="",updatetime="",wendu="",shidu="",pm25="",quality="",fengxiang="",fengli="",date="",high="",low="",type="";
    //private final static String you="0_50",liang="51_100",lightwuran="101_150",mediumwuran="151_200",heavywuran="200_300";
    /*private final static String baoxue="baoxue",baoyu="baoyu",dabaoyu="dabaoyu",
            daxue="daxue",dayu="dayu",duoyun="duoyun",
            leizhenyu="leizhenyu",leizhenyubingbao="leizhenyubingbao",qing="qing",
            shachenbao="shachenbao",tedabaoyu="tedabaoyu",wu="wu",
            xiaoxue="xiaoxue",xiaoyu="xiaoyu",yin="yin",
            yujiaxue="yujiaxue",zhenxue="zhenxue",zhenyu="zhenyu",
            zhongxue="zhongxue",zhongyu="zhongyu";*/
    public String getCity(){
        return city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getDate() {
        return date;
    }

    public String getWendu() {
        return wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public String getFengli() {
        return fengli;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getPm25() {
        return pm25;
    }

    public String getType(){
        return type;
    }

    public String getQuality() {
        return quality;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }
    @Override
    public String toString(){
        return "TodayWeather{"+
                "city='"+city+'\''+
                ",updatetime='"+updatetime+'\''+
                ",wendu='"+wendu+'\''+
                ",shidu='"+shidu+'\''+
                ",pm25='"+pm25+'\''+
                ",quality='"+quality+'\''+
                ",fengxiang='"+fengxiang+'\''+
                ",fengli='"+fengli+'\''+
                ",date='"+date+'\''+
                ",high='"+high+'\''+
                ",low='"+low+'\''+
                ",type='"+type+'\''+
                '}';
    }
    public int getpm25Pic(String pm25){
        int pm25number=Integer.valueOf(pm25);
        if(pm25number>=0&&pm25number<=50)return 1;
        else if(pm25number>50&&pm25number<=100)return 2;
        else if(pm25number>100&&pm25number<=150)return 3;
        else if(pm25number>150&&pm25number<=200)return 4;
        else if(pm25number>200&&pm25number<300)return 5;
        else return 6;
    }
}
