package pl.slapps.dot;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.model.Sounds;

/**
 * Created by piotr on 17.11.15.
 */
public class SoundsManager {

    private String TAG = SoundsManager.class.getName();

    private Uri moveSound;
    private Uri crashSound;
    private Uri soundFinish;
    private Uri backgroundSound;


    private AsyncPlayer asyncPlayer;
    private AsyncPlayer asyncPlayerPress;
    private AsyncPlayer asyncPlayerCrash;
    private AsyncPlayer asyncPlayerBackground;
    private Context context;

    public static String DEFAULT_PRESS = "click2";
    public static String DEFAULT_CRASH = "spacebib";
    public static String DEFAULT_FINISH = "finish";

    private String key = "cache/";


    public void stopMusic()
    {
        asyncPlayer.stop();
        asyncPlayerBackground.stop();
        asyncPlayerCrash.stop();
        asyncPlayerPress.stop();
    }

    public void configure(Sounds sounds) {
        Log.d(TAG, "configure : " + sounds.toString());


        if (!sounds.soundBackground.equals(""))

            if (sounds.soundBackground.contains(key))
                backgroundSound = Uri.parse(sounds.soundBackground);
            else
                backgroundSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + sounds.soundBackground);
        else
            backgroundSound = null;

        if (!sounds.soundPress.equals("")) {

            if (sounds.soundPress.contains(key))
                moveSound = Uri.parse(sounds.soundPress);
            else
                moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + sounds.soundPress);
        } else
            moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + DEFAULT_PRESS);

        if (!sounds.soundCrash.equals("")) {
            if (sounds.soundCrash.contains(key))
                crashSound = Uri.parse(sounds.soundCrash);
            else
                crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + sounds.soundCrash);
        } else
            crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + DEFAULT_CRASH);

        if (!sounds.soundFinish.equals("")) {
            if (sounds.soundFinish.contains(key))
                soundFinish = Uri.parse(sounds.soundFinish);
            else
                soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + sounds.soundFinish);

        } else
            soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + DEFAULT_FINISH);


    }

    public SoundsManager(Context context) {
        this.context = context;
        moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/click2");
        crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/spacebib");
        soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/finish");

        asyncPlayer = new AsyncPlayer("action");
        asyncPlayerBackground = new AsyncPlayer("background");
        asyncPlayerPress = new AsyncPlayer("press");
        asyncPlayerCrash = new AsyncPlayer("crash");


    }

    public void stopBackgroundPlayer() {
        asyncPlayerBackground.stop();
    }

    public void playFinishSound() {
        asyncPlayer.play(context, soundFinish, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playMoveSound() {
            asyncPlayerPress.play(context, moveSound, false, AudioManager.STREAM_MUSIC);
            Log.d(TAG, "play move sound " + moveSound.toString());
            //  mediaPlayerMove.start();

    }

    public void playCrashSound() {
        asyncPlayerCrash.play(context, crashSound, false, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public AsyncPlayer getAsyncPlayer() {
        return asyncPlayer;
    }


    public void playBackgroundSound() {
        asyncPlayerBackground.stop();
        if (backgroundSound == null)
            return;


        asyncPlayerBackground.play(context, backgroundSound, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playRawFile(String filename) {

        if (!filename.contains(key)) {
            Uri custom = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + filename);
            asyncPlayer.play(context, custom, false, AudioManager.STREAM_MUSIC);
            Log.d(TAG, "play raw file " + custom.toString());

        } else {
            Uri custom = Uri.parse(filename);
            asyncPlayer.play(context, custom, false, AudioManager.STREAM_MUSIC);
            Log.d(TAG, "play raw file " + custom.toString());

        }

    }

}
