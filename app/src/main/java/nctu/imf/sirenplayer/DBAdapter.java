package nctu.imf.sirenplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBAdapter extends ArrayAdapter<DBcontact> {

    private int resource;
    private DbDAO dbDAO;
    private DBcontact dBcontact;
    private List<DBcontact> dBcontacts;


    public DBAdapter(Context context, int resource, List<DBcontact> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.dBcontacts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout recordView;

        final DBcontact dBcontact = getItem(position);

        if (convertView == null) {
            recordView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, recordView, true);


        } else {
            recordView = (LinearLayout) convertView;
        }
        TextView CommandView = (TextView) recordView.findViewById(R.id.record_command);
        TextView TimeView = (TextView) recordView.findViewById(R.id.record_time);
        CommandView.setText(dBcontact.get_Command());
        TimeView.setText(dBcontact.get_Time());
            return recordView;
    }

    public DBcontact get(int index){
        return dBcontacts.get(index);
    }

}
