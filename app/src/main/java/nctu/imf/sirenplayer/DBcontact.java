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



    public DBcontact(){
        _Command= "";


    }

    public DBcontact(int id, String command, String time,double lat,double lng ){
        this._id =id;
        this._Command= command;
        this._Time=time;
        this._Lat=lat;
        this._Lng=lng;

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

    public String get_Time(){

        return _Time;
    }

    public String getLocaleDatetime(){
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate =new Date(System.currentTimeMillis());
        _Time = format.format(curDate);
        return _Time;
    }
    public void Set_Time(String time){

          this._Time =time;
    }
    public double get_Lat(){
        return  _Lat;
    }
    public void set_Lat(double lat){
        this._Lat=lat;
    }
    public double get_Lng(){
        return _Lng;
    }
    public void set_Lng(double lng){
        this._Lng=lng;
    }
//    public boolean isSelected(){
//        return selected;
//    }
//    public  void setSelected(boolean selected){
//        this.selected =selected;
//    }



}
