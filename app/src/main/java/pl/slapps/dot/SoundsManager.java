package pl.slapps.dot;

import android.content.Context;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

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

    private Sounds sounds;

    public static AsyncPlayer getPlayer(String tag) {
        return new AsyncPlayer(tag);
    }

    public void stopMusic() {
        asyncPlayer.stop();
        asyncPlayerBackground.stop();
        asyncPlayerCrash.stop();
        asyncPlayerPress.stop();
    }

    public Uri parseSound(String filename) {
        if (!filename.contains(key))
            return Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + filename);
        else
            return Uri.parse(filename);
    }

    public void configure(Sounds sounds) {
        this.sounds = sounds;

        if (!sounds.soundBackground.equals(""))

            if (sounds.soundBackground.contains(key))
                backgroundSound = Uri.parse(sounds.soundBackground);
            else
                backgroundSound = parseSound(sounds.soundBackground);
        else
            backgroundSound = null;

        if (!sounds.soundPress.equals("")) {

            if (sounds.soundPress.contains(key))
                moveSound = Uri.parse(sounds.soundPress);
            else
                moveSound = parseSound(sounds.soundPress);
        } else
            moveSound = parseSound(DEFAULT_PRESS);

        if (!sounds.soundCrash.equals("")) {
            if (sounds.soundCrash.contains(key))
                crashSound = Uri.parse(sounds.soundCrash);
            else
                crashSound = parseSound(sounds.soundCrash);
        } else
            crashSound = parseSound(DEFAULT_CRASH);

        if (!sounds.soundFinish.equals("")) {
            if (sounds.soundFinish.contains(key))
                soundFinish = Uri.parse(sounds.soundFinish);
            else
                soundFinish = parseSound(sounds.soundFinish);

        } else
            soundFinish = parseSound(DEFAULT_FINISH);


    }

    public SoundsManager(Context context) {
        this.context = context;
        this.sounds = new Sounds();
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

    private void playOverlapped(Uri uri) {
        getPlayer(uri.toString()).play(context, uri, false, AudioManager.STREAM_MUSIC);
    }

    public void playFinishSound() {
        asyncPlayer.play(context, soundFinish, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playMoveSound() {
        if (!sounds.overlap)
            asyncPlayerPress.play(context, moveSound, false, AudioManager.STREAM_MUSIC);
        else
            playOverlapped(moveSound);
        //  mediaPlayerMove.start();

    }

    public void playCrashSound() {
        if (!sounds.overlap)

            asyncPlayerCrash.play(context, crashSound, false, AudioManager.STREAM_MUSIC);
        else
            playOverlapped(crashSound);

        //  mediaPlayerCrash.start();
    }


    public void playBackgroundSound() {
        asyncPlayerBackground.stop();
        if (backgroundSound == null)
            return;


        asyncPlayerBackground.play(context, backgroundSound, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playRawFile(String filename) {
        Uri custom = parseSound(filename);
        if (!sounds.overlap)
            asyncPlayer.play(context, custom, false, AudioManager.STREAM_MUSIC);
        else
            playOverlapped(custom);

    }

}
