package pl.slapps.dot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.model.Stage;

/**
 * Created by piotr on 20/03/16.
 */
public class GoogleInvite {

    private MainActivity context;
    private String TAG = GoogleInvite.class.getName();

    public GoogleInvite(MainActivity context) {
        this.context = context;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == 123) {
            if (resultCode == context.RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG,"inds "+ids.toString());
            } else {
                // Sending failed or it was canceled, show failure message to the user
                //showMessage(getString(R.string.send_failed));
                Log.d(TAG,"status not ok ");

            }
        }
    }

    public void invite(String stageId) {
        Intent intent = new AppInviteInvitation.IntentBuilder("title")
                .setMessage("message")
                .setDeepLink(Uri.parse(stageId))
                        //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText("elo")
                .build();
        context.startActivityForResult(intent, 123);
    }

    public void receive() {

// Create an auto-managed GoogleApiClient with acccess to App Invites.
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(AppInvite.API)
                .enableAutoManage(context, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("retert", "failed");
                    }
                })
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = false;

        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, context, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {

                                if (result.getInvitationIntent() == null) {
                                    Log.d(TAG, "Invitation data null");
                                    return;
                                }


                                Bundle data = result.getInvitationIntent().getBundleExtra("com.google.android.gms.appinvite.REFERRAL_BUNDLE");
                                if (data == null)
                                    Log.d(TAG, "Invitation data null");
                                else {
                                    String deepLink = data.getString("com.google.android.gms.appinvite.DEEP_LINK");
                                    Log.d(TAG, "Deep link grabbed " + deepLink);


                                    DAO.getStage( new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {

                                            try {
                                                Log.d(TAG, "loading stage ... ");

                                                    JSONObject object = new JSONObject(response.toString());
                                                    object = object.has("api")?object.getJSONObject("api"):object;
                                                    object = object.has("doc")?object.getJSONObject("doc"):object;
                                                Stage stage = Stage.valueOf(object);
                                                context.loadStage(stage);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    },deepLink);

                                }


                                /*
                                for (String key : result.getInvitationIntent().getBundleExtra("com.google.android.gms.appinvite.REFERRAL_BUNDLE").keySet()) {
                                    Object value = result.getInvitationIntent().getBundleExtra("com.google.android.gms.appinvite.REFERRAL_BUNDLE").get(key);
                                    Log.d(TAG,"getInvitation "+ String.format("%s %s (%s)", key,
                                            value.toString(), value.getClass().getName()));
                                }
*/
                                // Because autoLaunchDeepLink = true we don't have to do anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                            }
                        });

    }
}
