package pl.slapps.dot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.slapps.dot.R;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 08.12.15.
 */
public class AdapterWorlds extends BaseAdapter {
    private Context mContext;
    private ArrayList<World> data;

    public AdapterWorlds(Context c, ArrayList<World> data) {
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

    class ViewHolder
    {
        TextView tvCount;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            convertView = LayoutInflater.from(mContext).inflate(R.layout.tile_world, null);
            vh = new ViewHolder();
            vh.tvCount = (TextView)convertView.findViewById(R.id.tv_count);
            convertView.setTag(vh);


        }
        else
            vh = (ViewHolder)convertView.getTag();

        vh.tvCount.setText("#"+position);

        return convertView;
    }


}