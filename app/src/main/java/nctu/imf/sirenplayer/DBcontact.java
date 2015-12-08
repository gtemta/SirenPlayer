package nctu.imf.sirenplayer;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBcontact {
    long _id;
    String _Command;
    String _Time ;
    double _Lat;
    double _Lng;

    long _Wid;
    String _Words;

    public DBcontact(){
        _Command= "";
    }

    public DBcontact(long id, String command, String time,double lat,double lng ){
        this._id =id;
        this._Command= command;
        this._Time=time;
        this._Lat=lat;
        this._Lng=lng;

    }

    public DBcontact(long id, String words, String time){
        this._Wid =id;
        this._Words =words;
        this._Time = time;
    }

    public long getId(){
        return  _id ;
    }
    public void Setid(long id){
        this._id=id;
    }

    public String get_Command(){
        return _Command;
    }
    public void Set_Command(String command){
        this._Command=command;
    }
    public String get_Time(){    return _Time; }

    public String getLocaleDatetime(){
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate =new Date(System.currentTimeMillis());
        _Time = format.format(curDate);
        return _Time;}
    public void Set_Time(String time){   this._Time =time; }
    public double get_Lat(){
        return  _Lat;
    }
    public void set_Lat(double lat){
        this._Lat=lat;
    }
    public double get_Lng(){
        return _Lng;
    }
    public void set_Lng(double lng){ this._Lng=lng; }

    public long get_Wid(){
        return _Wid;
    }
    public void  set_Wid(long wid){
        this._Wid =wid;
    }
    public  String get_Words(){
        return _Words;
    }
    public void set_Words(String words){
        this._Words=words;
    }



}
