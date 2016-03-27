package pl.slapps.dot;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import pl.slapps.dot.billing.util.IabHelper;
import pl.slapps.dot.billing.util.IabResult;
import pl.slapps.dot.billing.util.Purchase;

/**
 * Created by piotr on 20/03/16.
 */
public class GoogleBilling {

    private MainActivity context;
    private IabHelper mHelper;

    public GoogleBilling(MainActivity context)
    {
        this.context=context;
    }

    public void handleActivityResult(int requestCode,int resultCode,Intent data)
    {
     mHelper.handleActivityResult(requestCode,resultCode,data);
    }

    public void buyGap()
    {
        mHelper.launchPurchaseFlow(context, "skip_stage", 23, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.d("RRR", "purchased " + result.getMessage());


/*
                mHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        Log.d("RRR","consumed "+result.getResponse());

                    }
                });
*/
            }
        });

    }

    public void disableAds()
    {
        mHelper.launchPurchaseFlow(context, "disable_ads", 23, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.d("RRR","purchased "+result.getMessage());





            }
        });

    }

    public void setupBilling()
    {

        String base64EncodedPublicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhWXsH9Riwur2+I01TxfCZZhU0Zss1JE6LyEJjHq4Xfimm2YyLuoicPtOXGJWrVoXOaH54lCra8+f+XB5KYHDmF2GmQ0dlVnWYwZNjBuezbHN8DoVzfDnWmZxpdYtik3nI9sARoX6pz65GxQz253eEX9ceSIvKQoEAgIYNjLD7JFRGs3+j2kw/7LweSJrGSIVnjUpdUlbHg77xLxbQ48oq7a8O+Xia1N5Yu0G1FT0+PiHHqOToo/4iow8iVORmynE9b7DKw9Io2YCoyv4CWX04/Q3TOl3qWeV0YNBToGsP+PPzIrnJy5g6KV8vkDUxsbec281jEwxXb/AsuWPVQx5pwIDAQAB";

        mHelper = new IabHelper(context, base64EncodedPublicKey);

        try {
            mHelper.startSetup(new
                                       IabHelper.OnIabSetupFinishedListener() {
                                           public void onIabSetupFinished(IabResult result) {
                                               if (!result.isSuccess()) {
                                                   Log.d("RRR", "In-app Billing setup failed: " +
                                                           result);
                                               } else {
                                                   Log.d("RRR", "In-app Billing is set up OK");


                                                   ArrayList<String> skuList = new ArrayList<String>();
                                                   skuList.add("skip_stage");
                                                   //   skuList.add("android.test.purchased");



/*
                                               mHelper.launchPurchaseFlow(MainActivity.this, "skip_stage", 23, new IabHelper.OnIabPurchaseFinishedListener() {
                                                   @Override
                                                   public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                                       Log.d("RRR","purchased "+result.getMessage());



                                                       mHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                                                           @Override
                                                           public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                               Log.d("RRR","consumed "+result.getResponse());

                                                           }
                                                       });

                                                   }
                                               });

*/
/*
                                               mHelper.queryInventoryAsync(true,skuList, new IabHelper.QueryInventoryFinishedListener() {
                                                   @Override
                                                   public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                                                       inv.logSkus();
                                                       Log.d("RRR","query result "+result.isSuccess() + " "+result.getMessage() +" "+result.getResponse());
                                                       Log.d("RRR","query inventory "+inv.hasPurchase("skip_stage")+" "+inv.hasDetails("skip_stage"));

                                                   }
                                               });
*/
                                               }
                                           }
                                       });

        }catch (Throwable t)
        {t.printStackTrace();}

        /*
        new Thread(){
            public void run()
            {
                ArrayList<String> skuList = new ArrayList<String>();
                skuList.add("skip_stage");

                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                Log.d("RRR","query builded "+querySkus.toString());
                try {
                    Bundle skuDetails = mService.getSkuDetails(3,
                            getPackageName(), "inapp", querySkus);
                    Log.d("RRR", "details fetched " + skuDetails.getInt("RESPONSE_CODE"));

                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList
                                = skuDetails.getStringArrayList("DETAILS_LIST");
                        Log.d("RRR", "response list size  " + responseList.size());

                        for (String thisResponse : responseList) {

                            JSONObject object = new JSONObject(thisResponse);

                            Log.d("RRR",object.toString());

                            String sku = object.getString("productId");
                            String price = object.getString("price");
                           // if (sku.equals("premiumUpgrade")) mPremiumUpgradePrice = price;
                           // else if (sku.equals("gas")) mGasPrice = price;
                        }
                    }


                } catch (Throwable e) {
                    e.printStackTrace();

                    Log.d("RRR","error "+e.toString());
                }
            }
        }.start();
*/
    }
    /*
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            Log.d("RRR","service conected ");
            setupBilling();
        }
    };

*/
}
