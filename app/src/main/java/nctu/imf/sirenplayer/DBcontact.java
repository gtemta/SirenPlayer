package nctu.imf.sirenplayer;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBcontact {
    long _id;
    String _Command;
    String _Time;
    boolean selected;


    public DBcontact(){
        _Command= "";


    }

    public DBcontact(int id, String command, String time){
        this._id =id;
        this._Command= command;
        this._Time=time;

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
        return String.format(Locale.getDefault(),"%tF  %<tR", new Date(_Time));
    }
    public void Set_Time(String time){
        this._Time =time;
    }
    public boolean isSelected(){
        return selected;
    }
    public  void setSelected(boolean selected){
        this.selected =selected;
    }



}
