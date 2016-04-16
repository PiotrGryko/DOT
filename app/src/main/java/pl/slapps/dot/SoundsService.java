package pl.slapps.dot;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AsyncPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import pl.slapps.dot.model.Sounds;

/**
 * Created by piotr on 16/04/16.
 */
public class SoundsService extends Service {


    public static final int ACTION_BACKGROUND = 0;
    public static final int ACTION_CRASH = 1;
    public static final int ACTION_PRESS = 2;
    public static final int ACTION_FINISH = 3;
    public static final int ACTION_COIN = 4;
    public static final int ACTION_MUTE = 5;
    public static final int ACTION_CONFIG = 6;
    public static final int ACTION_RAW = 7;


    AssetManager am;

    private String DEFAULT_PRESS = "click2";
    private String DEFAULT_CRASH = "spacebib";
    private String DEFAULT_FINISH = "finish";
    private String DEFAULT_COIN = "coin";

    private int BACKGROUND_SOUND;
    private int PRESS_SOUND;
    private int CRASH_ONE_SOUND;
    private int CRASH_TWO_SOUND;
    private int FINISH_SOUND;
    private int COIN_SOUND;

    private AsyncPlayer asyncPlayer;


    private String parseSound(String filename) {
        if (!filename.endsWith(".mp3"))
            filename += ".mp3";
        if(!filename.startsWith("sounds/"))
            filename="sounds/"+filename;
        return filename;

    }

    private void playRawFile(String filename) {

        MediaPlayer m = new MediaPlayer();
        filename = parseSound(filename);
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = am.openFd(filename);
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            //m.setLooping(true);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp=null;
                }
            });
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("yyy",e.toString());
        }

        //  asyncPlayer.play(this, custom, false, AudioManager.STREAM_MUSIC);


    }

    private boolean crashFlag;

    SoundPool sounds;

    protected void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Log.d("YYY", "message received " + msg.what);
            switch (msg.what) {
                case ACTION_BACKGROUND:
                    playBackgrondSound();
                    break;
                case ACTION_PRESS:
                    playMoveSound();
                    break;
                case ACTION_CRASH:
                    playCrashSound();
                    break;
                case ACTION_FINISH:
                    playFinishSound();
                    break;
                case ACTION_COIN:
                    playCoinSound();
                    break;
                case ACTION_MUTE:
                    mute();
                    break;
                case ACTION_CONFIG:
                    configure((Sounds) msg.obj);
                    break;
                case ACTION_RAW:
                    playRawFile((String) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        am = this.getAssets();
        asyncPlayer = new AsyncPlayer("custom");


        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        createSoundPool();

        return mMessenger.getBinder();

    }

    public boolean onUnbind(Intent i) {

        sounds = null;
        return super.onUnbind(i);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }
    // Random number generator


    private int loadSound(String soundName, String defaultName) {
        int id = 0;
        if (soundName.trim().equals(""))
            soundName = defaultName;
        try {
            if (!soundName.endsWith(".mp3"))
                soundName += ".mp3";
            id = sounds.load(am.openFd("sounds/" + soundName), 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yyy", e.toString());
        }
        Log.d("yyy", "load sound=" + soundName + " default=" + defaultName + " id=" + id);

        return id;

    }

    public void mute() {

        sounds.stop(BACKGROUND_SOUND);
        sounds.stop(FINISH_SOUND);
        sounds.stop(CRASH_ONE_SOUND);
        sounds.stop(CRASH_TWO_SOUND);
        sounds.stop(PRESS_SOUND);


    }


    public void configure(Sounds s) {


        sounds.unload(BACKGROUND_SOUND);
        sounds.unload(PRESS_SOUND);
        sounds.unload(CRASH_TWO_SOUND);
        sounds.unload(CRASH_ONE_SOUND);
        sounds.unload(FINISH_SOUND);
        sounds.unload(COIN_SOUND);


        BACKGROUND_SOUND = loadSound(s.soundBackground, "");
        PRESS_SOUND = loadSound(s.soundPress, DEFAULT_PRESS);
        CRASH_ONE_SOUND = loadSound(s.soundCrash, DEFAULT_CRASH);
        CRASH_TWO_SOUND = loadSound(s.soundCrashTwo, DEFAULT_CRASH);
        FINISH_SOUND = loadSound(s.soundFinish, DEFAULT_FINISH);
        COIN_SOUND = loadSound("", DEFAULT_COIN);


    }


    public void playBackgrondSound() {

        sounds.play(BACKGROUND_SOUND, 1, 1, 0, 0, 1);
        //  mediaPlayerMove.start();
    }

    public void playFinishSound() {

        sounds.play(FINISH_SOUND, 1, 1, 0, 0, 1);
        //  mediaPlayerMove.start();
    }

    public void playMoveSound() {

        sounds.play(PRESS_SOUND, 1, 1, 0, 0, 1);

        //  mediaPlayerMove.start();

    }

    public void playCoinSound() {

        sounds.play(COIN_SOUND, 1, 1, 0, 0, 1);


    }

    public void playCrashSound() {


        if (crashFlag) {
            sounds.play(CRASH_ONE_SOUND, 1, 1, 0, 0, 1);

        } else {
            sounds.play(CRASH_TWO_SOUND, 1, 1, 0, 0, 1);

        }
        crashFlag = !crashFlag;


        //  mediaPlayerCrash.start();
    }
}
