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
import pl.slapps.dot.generator.widget.PlayButton;

/**
 * Created by piotr on 14/03/16.
 */
public class AdapterSounds extends ArrayAdapter {

    private ArrayList<String> sounds;
    private MainActivity context;
    private MediaPlayer player;

    public AdapterSounds(MainActivity context, ArrayList<String> sounds) {
        super(context, R.layout.row_sound, sounds);
        this.sounds = sounds;
        this.context = context;
        player = new MediaPlayer();
    }

    class ViewHolder {
        public TextView tvLabel;
        public PlayButton playButton;
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
                playButton.play(context.getSoundsManager().parseSound(sounds.get(position)));
            }
        });

        return convertView;
    }
}
