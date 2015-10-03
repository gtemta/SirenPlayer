package nctu.imf.sirenplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBAdapter extends ArrayAdapter<DBcontact>
     {
        //畫面資源標號
        private int resource ;
        //包裝的記事資料
        private List<DBcontact> dBcontacts;

         public DBAdapter(Context context, int resource, List<DBcontact> objects) {
             super(context, resource, objects);
             this.resource = resource;
             this.dBcontacts = objects;
         }

         @Override
         public View getView (int position, View convertView, ViewGroup parent){
             LinearLayout recordView;
             //讀取目前位置的記事物件
             final DBcontact dBcontact = getItem(position);
             if(convertView == null){
                 //建立項目畫面元件
                 recordView = new LinearLayout(getContext());
                 String inflater =Context.LAYOUT_INFLATER_SERVICE;
                 LayoutInflater li =(LayoutInflater)getContext().getSystemService(inflater);
                 li.inflate(resource,recordView,true);
             }
             else{
                 recordView = (LinearLayout)convertView;
             }
             //讀取 指令 日期時間元件及 確認碼
             TextView CommandView =(TextView)recordView.findViewById(R.id.record_command);
             TextView TimeView = (TextView)recordView.findViewById(R.id.record_time);
             TextView ConfirmView = (TextView)recordView.findViewById(R.id.record_confirm);
             CommandView.setText(dBcontact.get_Command());
             TimeView.setText(dBcontact.getLocaleDatetime());
             ConfirmView.setText(dBcontact.get_Confirm());

             return recordView;
         }


     }
