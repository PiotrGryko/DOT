package pl.slapps.dot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.R;

/**
 * Created by piotr on 17.11.15.
 */
public class AdapterStages extends ArrayAdapter {

    private Context context;
    private ArrayList<JSONObject> stages;

    public AdapterStages(Context context, ArrayList<JSONObject> stages) {
        super(context, R.layout.row_stages, stages);
        this.context = context;
        this.stages = stages;

    }

    class ViewHolder {
        public TextView tvLabel;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_stages, null);
            holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject jsonStage = stages.get(position);
        //Log.d("XXX",jsonStage.toString());
        try {
            final String name = jsonStage.has("name") ? jsonStage.getString("name") : "";


            JSONObject colors = jsonStage.has("colors") ? jsonStage.getJSONObject("colors") : new JSONObject();
            final String backgroundColor = colors.has("background") ? colors.getString("background") : "#000000";
            holder.tvLabel.setTextColor(Color.parseColor(backgroundColor));
            holder.tvLabel.setText("#" + position + " " + name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
