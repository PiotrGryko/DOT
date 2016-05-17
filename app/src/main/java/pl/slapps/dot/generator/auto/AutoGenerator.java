package pl.slapps.dot.generator.auto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 27/03/16.
 */
public class AutoGenerator {

    private final static String TAG = AutoGenerator.class.getName();

    private Generator generator;
    private Random random;
    public DepthFirstSearch firstSearch;

    private boolean cancelGeneration = false;


    public AutoGenerator(Generator generator) {
        this.generator = generator;
        this.random = new Random();
        this.firstSearch = new DepthFirstSearch(generator);
    }


    private AlertDialog dialog;

    private void initDialog() {
        dialog = new AlertDialog.Builder(generator.view.context).create();
        dialog.setTitle("Generating stages");

        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelGeneration = true;


            }
        });

        dialog.show();


    }

    private void finishSetGeneration() {


        generator.view.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });


        // generator.view.setDrawing(true);
        // Log.d(TAG,"finish set ");
        /*
        Log.d(TAG,"finish generation set");

        final boolean flag = generator.getPreview();

        generator.view.context.getHandler().post(new Runnable() {
            @Override
            public void run() {
                generator.view.setDrawing(true);
                generator.view.setRunnig(true);

                if (flag)
                    generator.stopPreview();
                generator.refreashMaze();
                generator.getLayout().refreshControlls();

                if (dialog.isShowing())
                    dialog.dismiss();

                // if (flag)
               //     generator.startPreview();
            }
        });
*/

    }


    private void generateStagesSetInternal(final int index, final int count, final int widht, final int height, final float percent) {

        generator.view.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.setTitle("generating stage " + index + "/" + count);
                    Log.d(TAG, "generating stage " + index + "/" + count + " world " + generator.getLayout().getCurrentWorld().id);
                }
            }
        });

        generator.saveMaze(new Generator.OnSaveListener() {
            @Override
            public void onSaved() {

                if (index >= count) {
                    finishSetGeneration();

                    return;
                } else if (!cancelGeneration) {
                    generateMaze(true, index + 1, widht, height, percent, new Generator.OnSaveListener() {
                        @Override
                        public void onSaved() {
                            generateStagesSetInternal(index + 1, count, widht, height, percent);
/*
                            generator.view.context.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    generateStagesSetInternal(index + 1, count,widht,height,percent);

                                }
                            });
*/
                        }

                        @Override
                        public void onFailed() {

                        }

                        @Override
                        public JSONObject onDumped(JSONObject data) {
                            return null;
                        }
                    });

                } else {
                    finishSetGeneration();

                    /*
                    generator.view.context.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            finishSetGeneration();

                        }
                    });
*/
                }


            }

            @Override
            public void onFailed() {

            }

            @Override
            public JSONObject onDumped(JSONObject data) {
                Log.d(TAG, "on dumped " + data.toString());
                boolean coin = random.nextFloat() > 0.5;

                if (coin) {
                    JSONArray route = null;
                    try {
                        route = data.has("route") ? data.getJSONArray("route") : new JSONArray();

                        int size = route.length();
                        int index = 1 + random.nextInt(size - 2);
                        JSONObject step = route.getJSONObject(index);
                        String type = step.has("type") ? step.getString("type") : "";
                        if (type.equals(Route.Type.ROUTE.name())) {
                            step.put("draw_coin", true);
                            Log.d(TAG, "GGGGGGGGGGg draw coin true! " + index);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return data;
            }
        });
    }

    public void generateStagesSet(final int stagesCount, final int widht, final int height, final float percent) {
        cancelGeneration = false;

        if (generator.getLayout().getCurrentWorld() == null) {
            Toast.makeText(generator.view.context, "Select world first!", Toast.LENGTH_LONG).show();
            return;
        }

        generator._id = null;

        final int index = 0;
        initDialog();

//        generator.view.setDrawing(false);

        generateMaze(true, index, widht, height, percent, new Generator.OnSaveListener() {
            @Override
            public void onSaved() {

                generator.refreashMaze();

                generateStagesSetInternal(index, stagesCount, widht, height, percent);

                /*
                generator.view.context.getHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        generateStagesSetInternal(index, stagesCount,widht,height,percent);

                    }
                });
*/
            }

            @Override
            public void onFailed() {

            }

            @Override
            public JSONObject onDumped(JSONObject data) {


                return null;

            }
        });


    }

    private String randomColor(int alpha) {
        int color = Color.argb(alpha, random.nextInt(255), random.nextInt(255), random.nextInt(255));

        return "#" + Integer.toHexString(color);
    }

    public void generateRandomStage() {
        generator._id = null;

        generateMaze(false, 0, 1 + random.nextInt(30), 1 + random.nextInt(30), 0.8f, null);
    }

    private String getRandomSound(String dir) {
        ArrayList<String> sounds = generator.view.context.getActivityLoader().listSoundsFromAssets(dir);
        return "sounds" + dir + "/" + sounds.get(random.nextInt(sounds.size()));

    }


    private String getRandomBackground() {
        ArrayList<String> bkgs = generator.view.context.getActivityLoader().listBackgroundsFromAssets();
        return bkgs.get(random.nextInt(bkgs.size()));

    }

    private void generateMaze(final boolean set, float speedRatio, int widht, int height, float percent, final Generator.OnSaveListener onSaveListener) {

        if (firstSearch.isRunning)
            return;

        int x = widht;
        int y = height;


        String routeColor = randomColor(200+random.nextInt(55));
        String dotColor = randomColor(255);

        if (Util.isColorDark(routeColor) && Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, true);
        } else if (!Util.isColorDark(routeColor) && !Util.isColorDark(dotColor)) {
            dotColor = Util.changeColorBrightness(dotColor, false);

        }

        float dotShinning = 1.25f;//1.25f + 1.25f*random.nextFloat();
        float dotDistance = 0.25f;

        generator.getConfig().colors.colorBackground = randomColor(random.nextInt(255));
        generator.getConfig().colors.colorExplosionEnd = randomColor(255);
        generator.getConfig().colors.colorExplosionStart = randomColor(255);
        generator.getConfig().colors.colorShip = dotColor;
        generator.getConfig().colors.colorFence = randomColor(255);
        generator.getConfig().colors.colorFilter = randomColor(120);

        generator.getConfig().colors.colorRoute = routeColor;
        generator.getConfig().settings.explosionOneLightDistance = random.nextFloat();
        generator.getConfig().settings.explosionOneLightShinning = 1 + 1.5f * random.nextFloat();
        generator.getConfig().settings.dotLightDistance = dotDistance;
        generator.getConfig().settings.dotLightShinning = dotShinning;


        generator.getConfig().sounds.soundCrash = getRandomSound("/crash");
        generator.getConfig().sounds.soundCrashTwo = getRandomSound("/crash");
        generator.getConfig().sounds.soundPress = getRandomSound("/press");
        generator.getConfig().sounds.soundFinish = getRandomSound("/finish");


        boolean useBackground = random.nextFloat()>0.80f;
        if(useBackground)
            generator.getConfig().settings.backgroundFile = getRandomBackground();
        else
            generator.getConfig().settings.backgroundFile = "";



        // if(!set)
        generator.refreashMaze();


        final boolean flag = generator.getPreview();
        generator.stopPreview();

        firstSearch.generateMaze(x, y, percent, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {


                if (!set) {


                    generator.view.context.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (flag)
                                generator.stopPreview();
                            generator.refreashMaze();
                            generator.getLayout().refreshControlls();

                            if (flag)
                                generator.startPreview();
                        }
                    });


                }

                if (onSaveListener != null)
                    onSaveListener.onSaved();

                return false;
            }
        }, !set);


    }


}
