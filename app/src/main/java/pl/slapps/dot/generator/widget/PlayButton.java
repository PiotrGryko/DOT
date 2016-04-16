package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.IOException;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsService;

/**
 * Created by piotr on 14/03/16.
 */
public class PlayButton extends ImageView{

    //private MediaPlayer player;
    private MainActivity context;
    //private boolean isPrepared;
    public PlayButton(Context context) {
        super(context);
        this.context=(MainActivity) context;
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=(MainActivity) context;
    }

    public void play(String file)
    {


      //  this.player = new MediaPlayer();
        PlayButton.this.setImageDrawable(getContext().getResources().getDrawable(R.drawable.play_btn));

        MainActivity.sendAction(SoundsService.ACTION_RAW,file);

    }






}
