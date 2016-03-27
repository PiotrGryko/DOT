package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.IOException;

import pl.slapps.dot.R;

/**
 * Created by piotr on 14/03/16.
 */
public class PlayButton extends ImageView{

    private MediaPlayer player;
    private Uri uri;
    private Context context;
    private boolean isPrepared;
    public PlayButton(Context context) {
        super(context);
        this.context=context;
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public void play(Uri uri)
    {

        this.uri = uri;

        if(player != null && player.isPlaying())
        {
            player.stop();
            player.release();
            PlayButton.this.setImageDrawable(getContext().getResources().getDrawable(R.drawable.play_btn));
            player = null;
            return;
        }
        this.player = new MediaPlayer();

        //player=new MediaPlayer();

        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                PlayButton.this.setImageDrawable(getContext().getResources().getDrawable(R.drawable.play_btn));
            }
        });

        this.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isPrepared = true;
                PlayButton.this.player.start();
            }
        });


        try {
            player.setDataSource(context, uri);
            player.prepareAsync();
            PlayButton.this.setImageDrawable(context.getResources().getDrawable(R.drawable.stop));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }






}