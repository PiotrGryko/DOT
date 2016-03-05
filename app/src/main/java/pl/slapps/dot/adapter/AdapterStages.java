package pl.slapps.dot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.R;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 17.11.15.
 */
public class AdapterStages extends BaseAdapter {
    private Context mContext;
    private ArrayList<Stage> data;

    public AdapterStages(Context c, ArrayList<Stage> data) {
        mContext = c;
        this.data = data;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    class ViewHolder {
        public TextView tvIndex;
        public TextView tvName;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            convertView = LayoutInflater.from(mContext).inflate(R.layout.tile_stage, null);
            holder = new ViewHolder();
            holder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.tvIndex.setText("#" + Integer.toString(position));
        holder.tvName.setText(data.get(position).name);
        holder.tvIndex.setTextColor(Color.parseColor(data.get(position).config.colors.colorShip));
        holder.tvName.setTextColor(Color.parseColor(data.get(position).config.colors.colorShip));


        return convertView;
    }


}