package pl.slapps.dot;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;

/**
 * Created by piotr on 17.11.15.
 */
public class SoundsManager {

    private Uri moveSound;
    private Uri crashSound;
    private Uri soundFinish;
    private Uri soundBackgroundBirds;
    private Uri soundBackgroundBeach;
    private Uri soundBackgroundWater;



    private AsyncPlayer asyncPlayer;
    private AsyncPlayer asyncPlayerBackground;
    private Context context;


    public SoundsManager(Context context)
    {
        this.context = context;
        moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/click2");
        crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/click");
        soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/finish");
        soundBackgroundBirds = Uri.parse("android.resource://" + context.getPackageName() + "/raw/birds");
        soundBackgroundWater = Uri.parse("android.resource://" + context.getPackageName() + "/raw/waves");
        soundBackgroundBeach = Uri.parse("android.resource://" + context.getPackageName() + "/raw/beach");

        asyncPlayer = new AsyncPlayer("action");
        asyncPlayerBackground = new AsyncPlayer("background");
    }

    public void stopBackgroundPlayer()
    {
        asyncPlayerBackground.stop();
    }

    public void playFinishSound() {
        asyncPlayer.play(context, soundFinish, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playMoveSound() {
        asyncPlayer.play(context, moveSound, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playCrashSound() {
        asyncPlayer.play(context, crashSound, false, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundBirds() {
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(context, soundBackgroundBirds, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundBeach() {
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(context, soundBackgroundBeach, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundWaves() {
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(context, soundBackgroundWater, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }
}
