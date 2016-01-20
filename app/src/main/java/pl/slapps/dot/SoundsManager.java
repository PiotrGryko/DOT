package pl.slapps.dot;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 17.11.15.
 */
public class SoundsManager {

    private String TAG = SoundsManager.class.getName();

    private Uri moveSound;
    private Uri crashSound;
    private Uri soundFinish;
    private Uri backgroundSound;
    private Uri soundBackgroundBirds;
    private Uri soundBackgroundBeach;
    private Uri soundBackgroundWater;


    private AsyncPlayer asyncPlayer;
    private AsyncPlayer asyncPlayerPress;
    private AsyncPlayer asyncPlayerCrash;
    private AsyncPlayer asyncPlayerBackground;
    private Context context;

    public static String DEFAULT_PRESS = "click2";
    public static String DEFAULT_CRASH = "spacebib";
    public static String DEFAULT_FINISH = "finish";




    public void configure(JSONObject sounds) {
        Log.d(TAG,"configure : "+sounds.toString());
        try {


            String background = sounds.has("background") ? sounds.getString("background") : "";

            String press = sounds.has("press") ? sounds.getString("press") : "";
            String crash = sounds.has("crash") ? sounds.getString("crash") : "";
            String finish = sounds.has("finish") ? sounds.getString("finish") : "";

            if (!background.equals(""))
                backgroundSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + background);
            else
                backgroundSound=null;

            if (!press.equals(""))
                moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + press);
            else
                moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/"+DEFAULT_PRESS);

            if (!crash.equals(""))
                crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + crash);
            else
                crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/"+DEFAULT_CRASH);

            if (!finish.equals(""))
                soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + finish);
            else
                soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/"+DEFAULT_FINISH);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public SoundsManager(Context context) {
        this.context = context;
        moveSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/click2");
        crashSound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/spacebib");
        soundFinish = Uri.parse("android.resource://" + context.getPackageName() + "/raw/finish");
        soundBackgroundBirds = Uri.parse("android.resource://" + context.getPackageName() + "/raw/birds");
        soundBackgroundWater = Uri.parse("android.resource://" + context.getPackageName() + "/raw/waves");
        soundBackgroundBeach = Uri.parse("android.resource://" + context.getPackageName() + "/raw/beach");

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
        Log.d(TAG,"play move sound "+moveSound.toString());
        //  mediaPlayerMove.start();
    }

    public void playCrashSound() {
        asyncPlayerCrash.play(context, crashSound, false, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public AsyncPlayer getAsyncPlayer()
    {
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
        Uri custom = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + filename);
        asyncPlayer.play(context, custom, false, AudioManager.STREAM_MUSIC);
        Log.d(TAG,"play raw file "+filename);

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
