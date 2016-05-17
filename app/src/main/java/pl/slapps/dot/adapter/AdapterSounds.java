package pl.slapps.dot.adapter;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsManager;
import pl.slapps.dot.SoundsService;
import pl.slapps.dot.generator.widget.PlayButton;

/**
 * Created by piotr on 14/03/16.
 */
public class AdapterSounds extends ArrayAdapter {

    public interface OnSoundClickListener
    {
        public void onClick(String sound);
    }

    private OnSoundClickListener listener;

    private ArrayList<String> sounds;
    private MainActivity context;
    private MediaPlayer player;
    private String currentPath="";

    public AdapterSounds(MainActivity context, ArrayList<String> sounds, OnSoundClickListener listener) {
        super(context, R.layout.row_sound,sounds);
        this.sounds = sounds;
        this.listener=listener;
        this.context = context;
        player = new MediaPlayer();
    }

    class ViewHolder {
        public TextView tvLabel;
        public PlayButton playButton;
    }


    public int getCount()
    {
        return sounds.size();
    }

    public Object getItem(int position)
    {
      return "sounds"+currentPath+"/"+sounds.get(position);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_sound, null);
            vh = new ViewHolder();
            vh.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            vh.playButton = (PlayButton) convertView.findViewById(R.id.play_btn);
            convertView.setTag(vh);
        } else
            vh = (ViewHolder) convertView.getTag();
        vh.tvLabel.setText(sounds.get(position));

        final PlayButton playButton = vh.playButton;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton.play((String)getItem(position));
            }
        });
        if(!sounds.get(position).endsWith(".mp3"))
            playButton.setVisibility(View.GONE);
        else
            playButton.setVisibility(View.VISIBLE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = sounds.get(position);
                if(name.endsWith(".mp3")||name.endsWith(".wav"))
                {
                    if(listener!=null)
                        listener.onClick((String)getItem(position));
                }
                else
                {
                    currentPath+="/"+name;

                    sounds = context.getActivityLoader().listSoundsFromAssets(currentPath);
                    notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }
}
