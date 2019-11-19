package cordova.plugin.jsando;

import android.content.Context;
import android.app.Activity;
import java.lang.Runnable;
import java.util.Timer;
import java.util.TimerTask;

import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kidoz.sdk.api.KidozInterstitial;
import com.kidoz.sdk.api.KidozSDK;
import com.kidoz.sdk.api.interfaces.SDKEventListener;
import com.kidoz.sdk.api.ui_views.interstitial.BaseInterstitial;
import com.kidoz.sdk.api.ui_views.kidoz_banner.KidozBannerListener;
import com.kidoz.sdk.api.ui_views.new_kidoz_banner.BANNER_POSITION;
import com.kidoz.sdk.api.ui_views.new_kidoz_banner.KidozBannerView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class jsando extends CordovaPlugin {
    Timer timer;
    Context ctx;
    Activity activity;
    boolean isInterstitialLoading = false;
    boolean isNoBannerOffer = false, isNoInterstitialOffer = false, isNoRewardOffer = false;
    boolean isRewardLoading = false;
    String nextAdType = "";
	//testing purpose
     private String PUBLISHER_ID = "5";
     private String SECURITY_TOKEN = "i0tnrdwdtq0dm36cqcpg6uyuwupkj76s";
    private KidozInterstitial mKidozInterstitial;
    private KidozBannerView mKidozBannerView;
    private KidozInterstitial mKidozRewarded;
    public CallbackContext interstitialCallBack;
    public CallbackContext rewardCallBack;



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        activity = this.cordova.getActivity();
        ctx = this.cordova.getContext();
       /*Toast.makeText(ctx, "Action: "+action, Toast.LENGTH_SHORT).show(); */

        if(action.equalsIgnoreCase("initialize")) {
            	cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        try{
                                String message = args.getString(0);
                                initialize(message, callbackContext);
                        }
                        catch(JSONException e){
                             PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "JSON Exception");
                            pluginResult.setKeepCallback(false); //do not  Keep callback
                            callbackContext.sendPluginResult(pluginResult);
                           // callbackContext.error("bad json exception");
                        }
                    }
                });
        }
        else if(action.equalsIgnoreCase("loadAd")){

            Log.d("Success: ", action+":"+args.getString(0));
        //    Toast.makeText(ctx, action+": "+args.getString(0), Toast.LENGTH_SHORT).show();
                try {

                    String message = args.getString(0);
                    cordova.getThreadPool().execute((Runnable) () -> {

                        Log.d("Success: ", message);

                        loadAd(message, callbackContext);


                        // return true;

                    });
                }
                catch(Exception e){
                //                            Toast.makeText(ctx, action+": FAILED "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERR Ad: ", e.getMessage());
                    PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, e.getMessage());
                    pluginResult.setKeepCallback(false); //do not  Keep callback
                    callbackContext.sendPluginResult(pluginResult);
                }
      //  return true;

        }

        else if(action.equalsIgnoreCase("showAd")){
            	cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        try{
                            String message = args.getString(0);
                            showAd(message, callbackContext);
                        }
                        catch(Exception e){
                             PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "JSON Exception");
                            pluginResult.setKeepCallback(false); //do not  Keep callback
                            callbackContext.sendPluginResult(pluginResult);
                        }
                    // return true;

                    }
                });

          //  return true;
        }
        else if(action.equalsIgnoreCase("toast")){
        try{
                    String message = args.getString(0);
                Toast.makeText(this.cordova.getContext(), message, Toast.LENGTH_SHORT).show();
                    PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "toast done");
                pluginResult.setKeepCallback(false); //do not  Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }
            catch(Exception e){
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "bad json");
                pluginResult.setKeepCallback(false); //do not  Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }
 
                 
           // return true;
        }
        PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true); // Keep callback
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }
    private void initialize(String message, CallbackContext callbackContext) {
        /*Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show(); */
        if (message != null && message.length() > 0) {
            isInterstitialLoading = false;
            isRewardLoading = false;
            if(message.equalsIgnoreCase("initkidozSDK"))
                initKidozSDK(callbackContext);
            if(message.equalsIgnoreCase("initBannerAd")){
              initBanner();
              callbackContext.success("initBanner");
            }
            if(message.equalsIgnoreCase("initInterstitialAd")){
                    initInterstitial();
                    callbackContext.success("initInterstitial");
            }
            if(message.equalsIgnoreCase("initRewardAd")){
                    initRewarded();
                    callbackContext.success("initReward");
            }            
        }
        else{
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR,"null type");
                pluginResult.setKeepCallback(false); // do not Keep callback
                callbackContext.sendPluginResult(pluginResult);
        }
       PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true); // do not Keep callback
        callbackContext.sendPluginResult(pluginResult);
    }
    
    private void showAd(String message, CallbackContext callbackContext){
        if (message != null && message.length() > 0) {
            if (message.equalsIgnoreCase("interstitial")) {
               showInterstitialAd(callbackContext);
            }
            // else if (message.equalsIgnoreCase("banner")) {
            //    toggleBannerAd();
            //    callbackContext.success("banner added");
            // }
            else if(message.equalsIgnoreCase("reward")) {
                showRewardAd(callbackContext);
            }
            else{
                 PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "invalid adtype");
                pluginResult.setKeepCallback(false); // do not Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }
        }
        else{
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "null adtype");
            pluginResult.setKeepCallback(false); // do not Keep callback
            callbackContext.sendPluginResult(pluginResult);
        }
     }
    private void loadAd(String message, CallbackContext callbackContext){
         
        if (message != null && message.length() > 0) {
            if (message.equalsIgnoreCase("interstitial")) {
               loadInterstialAd(callbackContext);
            }
            else if (message.equalsIgnoreCase("reward")) {
               //  Toast.makeText(ctx, "rewardad called", Toast.LENGTH_SHORT).show();
               loadRewardAd(callbackContext);
            }
            // else if (message.equalsIgnoreCase("banner")) {
            //    //loadBannerAd(callbackContext);
            // }
            else{
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "invalid adtype");
                pluginResult.setKeepCallback(false); // do not Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }
        }
        else{
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "null adtype");
            pluginResult.setKeepCallback(false); // do not Keep callback
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    private void initKidozSDK(CallbackContext callbackContext)  {
        KidozSDK.setSDKListener(new SDKEventListener()
        {
            @Override
            public void onInitSuccess()
            {
                //SDK Init | Success().
                //
                initInterstitial();
                initRewarded();
                initBanner();
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "sdk initialized");
                pluginResult.setKeepCallback(false); // Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onInitError(String error)
            {
                //SDK Init | Error
                //callbackContext.error("Init SDK Errror");
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "Init SDK Errror");
                pluginResult.setKeepCallback(false); // Keep callback
                callbackContext.sendPluginResult(pluginResult);
            }
        });

        KidozSDK.initialize(this.cordova.getActivity(), PUBLISHER_ID, SECURITY_TOKEN);
    }
    private void initInterstitial() {

        mKidozInterstitial = new KidozInterstitial(this.cordova.getActivity(), KidozInterstitial.AD_TYPE.INTERSTITIAL);
        mKidozInterstitial.setOnInterstitialEventListener(new BaseInterstitial.IOnInterstitialEventListener()
        {
            @Override
            public void onClosed()
            {
               
                /*Toast.makeText(ctx, "Interstitial Closed",Toast.LENGTH_SHORT).show(); */
               // toggleBannerAd();
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "closed");
                pluginResult.setKeepCallback(false); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }

            @Override
            public void onOpened()
            {
               /*Toast.makeText(ctx, "Interstitial Opened",
                        Toast.LENGTH_SHORT).show(); */
               // interstitialCallBack.success("opened");
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "opened");
                pluginResult.setKeepCallback(true); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }

            @Override
            public void onReady()
            {
                isNoInterstitialOffer = false;
                /*Toast.makeText(ctx, "Interstitial ready",
                        Toast.LENGTH_SHORT).show(); */
                isInterstitialLoading = false;
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "ready");
                pluginResult.setKeepCallback(false); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }

            @Override
            public void onLoadFailed()
            {
                
                isNoInterstitialOffer = false;
                 isInterstitialLoading = false;
                /*Toast.makeText(ctx, "Interstitial Failed to load",
                        Toast.LENGTH_SHORT).show(); */
               
                //toggleBannerAd();
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "failed");
                pluginResult.setKeepCallback(false); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }

            @Override
            public void onNoOffers()
            {
                
                isNoInterstitialOffer = true;
                /*Toast.makeText(ctx, "Interstitial No Offers",
                        Toast.LENGTH_SHORT).show(); */
                isInterstitialLoading = false;
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "no offers");
                pluginResult.setKeepCallback(false); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }

        });
    }
    private void initBanner()   {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mKidozBannerView = KidozSDK.getKidozBanner(activity);
                mKidozBannerView.setBannerPosition(BANNER_POSITION.TOP_LEFT);
                mKidozBannerView.setKidozBannerListener(new KidozBannerListener()
                {
                    @Override
                    public void onBannerViewAdded()
                    {
                        /*Toast.makeText(ctx, "onBannerViewAdded", Toast.LENGTH_SHORT).show(); */
                    }

                    @Override
                    public void onBannerReady()
                    {
                        Log.d("sample", "onBannerReady()");
                        /*Toast.makeText(ctx, "onBannerReady", Toast.LENGTH_SHORT).show(); */
                        isNoBannerOffer = false;
                        mKidozBannerView.show();

                    }

                    @Override
                    public void onBannerError(String errorMsg)
                    {

                        Log.d("sample", "onBannerError(" + errorMsg + ")");
                        /*Toast.makeText(ctx, "onBannerError(" + errorMsg, Toast.LENGTH_SHORT).show(); */

                    }

                    @Override
                    public void onBannerClose()
                    {
                        Log.d("sample", "onBannerClose()");
                        mKidozBannerView.load();
                        //initBanner();
                    }

                    @Override
                    public void onBannerNoOffers()
                    {
                        isNoBannerOffer = true;
                        Log.d("sample", "onBannerNoOffers()");
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                TimerMethod("banner");
                            }

                        }, 0, 10000);
                    }
                });
                mKidozBannerView.load();
            }
        });
    }
    private void initRewarded()   {
        mKidozRewarded = new KidozInterstitial(this.cordova.getActivity(), KidozInterstitial.AD_TYPE.REWARDED_VIDEO);
        mKidozRewarded.setOnInterstitialEventListener(new BaseInterstitial.IOnInterstitialEventListener()
        {
            @Override
            public void onClosed()
            {

                /*Toast.makeText(ctx, "Rewarded Closed",Toast.LENGTH_SHORT).show(); */
                //loadNextAd();
                // toggleBannerAd();
                 rewardCallBack.success("closed");
            }

            @Override
            public void onOpened()
            {

                /*Toast.makeText(ctx, "Rewarded Opened",
                        Toast.LENGTH_SHORT).show(); */
            }

            @Override
            public void onReady()
            {
                isNoRewardOffer = false;
                // Toast.makeText(ctx, "Rewarded ready",
                //         Toast.LENGTH_SHORT).show(); 
                isRewardLoading = false;
                rewardCallBack.success("ready");
            }

            @Override
            public void onLoadFailed()
            {
                isNoRewardOffer = false;
                Toast.makeText(ctx, "Rewarded Failed to load",
                        Toast.LENGTH_SHORT).show(); 
                isRewardLoading = false;
               // toggleBannerAd();
              
                rewardCallBack.error("failed");
            }

            @Override
            public void onNoOffers()
            {
                /*Toast.makeText(ctx, "Rewarded No Offers",
                        Toast.LENGTH_SHORT).show(); */
                isRewardLoading = false;
				//To add a timer for reward ad loading
				isNoRewardOffer  = true;
                //Timer can be implemented in javascript instead on reward 
                rewardCallBack.error("no offers");
            }
        });

        /**
         * Events that invoked for Rewarded  Video Interstitial
         */
        mKidozRewarded.setOnInterstitialRewardedEventListener(new BaseInterstitial.IOnInterstitialRewardedEventListener()
        {
            @Override
            public void onRewardReceived()
            {
                /*Toast.makeText(ctx, "Reward Received",
                        Toast.LENGTH_SHORT).show(); */
            }

            @Override
            public void onRewardedStarted()
            {

            }
        });

    }
    private void toggleBannerAd(){
        if(mKidozBannerView!=null) {
                if (!mKidozBannerView.isShowing()) {
                    mKidozBannerView.show();
                }
                else if(mKidozBannerView.isShowing()){
                    mKidozBannerView.hide();
                }
        }
        else if(mKidozBannerView==null){
            initBanner();
        }
    }
    private void  loadInterstialAd(CallbackContext callbackContext){
     if(mKidozInterstitial!=null){
          if (!mKidozInterstitial.isLoaded() && !isInterstitialLoading) {
                interstitialCallBack = callbackContext;
                mKidozInterstitial.loadAd();
              PluginResult resultB = new PluginResult(PluginResult.Status.OK, "interstitialad loading");
              resultB.setKeepCallback(true);
              interstitialCallBack.sendPluginResult(resultB);
            //   /*Toast.makeText(ctx, "interstitial ad is loading", Toast.LENGTH_SHORT).show(); */
            //   PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
            //   pluginResult.setKeepCallback(true); // Keep callback
                isInterstitialLoading = true;
            }
            else{
                    //callbackContext.error("busy thread");
              PluginResult resultB = new PluginResult(PluginResult.Status.ERROR, "not initialised");
              resultB.setKeepCallback(false);
              callbackContext.sendPluginResult(resultB);
            }
        }
        else{
             //callbackContext.error("not initialised");
         PluginResult resultB = new PluginResult(PluginResult.Status.ERROR, "not initialised");
         resultB.setKeepCallback(false);
         callbackContext.sendPluginResult(resultB);
        }
    }
    private void  loadRewardAd(CallbackContext callbackContext){
        if(mKidozRewarded!=null){
            if (!mKidozRewarded.isLoaded() && !isRewardLoading) {
                rewardCallBack = callbackContext;
                mKidozRewarded.loadAd();
                PluginResult resultB = new PluginResult(PluginResult.Status.OK, "rewardad loading");
                resultB.setKeepCallback(true);
                rewardCallBack.sendPluginResult(resultB);
                /*Toast.makeText(ctx, "rewardad is loading", Toast.LENGTH_SHORT).show(); */
                isRewardLoading = true;
            }
            else{
                    //callbackContext.error("busy thread");
                PluginResult resultB = new PluginResult(PluginResult.Status.ERROR, "busy thread");
                resultB.setKeepCallback(false);
                callbackContext.sendPluginResult(resultB);
                }
            }
        else{
            //callbackContext.error("not initialised");
            PluginResult resultB = new PluginResult(PluginResult.Status.ERROR, "not initialised");
            resultB.setKeepCallback(false);
            callbackContext.sendPluginResult(resultB);
        }
    }

    private void showInterstitialAd(CallbackContext callbackContext){
         if(mKidozInterstitial!=null){
            interstitialCallBack = callbackContext;
        //    String showCalBackId = interstitialCallBack.getCallbackId();
            //  /*Toast.makeText(ctx, message+mKidozInterstitial.isLoaded(), Toast.LENGTH_SHORT).show(); */
            if (mKidozInterstitial.isLoaded()) {
               mKidozInterstitial.show();
                //toggleBannerAd();
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "showing");
                pluginResult.setKeepCallback(true); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);

            } else {
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "not loaded");
                pluginResult.setKeepCallback(false); // Keep callback
                interstitialCallBack.sendPluginResult(pluginResult);
            }
        }
        else{
            //initInterstitial();
            //callbackContext.error("not initialised");
             PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "not initialised");
             pluginResult.setKeepCallback(false); // Keep callback
             interstitialCallBack.sendPluginResult(pluginResult);
        }
    }
    private void showRewardAd(CallbackContext callbackContext){
        /*Toast.makeText(ctx,"showRewardAd",   Toast.LENGTH_SHORT).show(); */
        if (mKidozRewarded != null) {
            rewardCallBack = callbackContext;
            if (mKidozRewarded.isLoaded()) {
               // toggleBannerAd();
                mKidozRewarded.show();
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, "showing");
                pluginResult.setKeepCallback(true); // Keep callback
                rewardCallBack.sendPluginResult(pluginResult);
            }
            else {
                PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "not loaded");
                pluginResult.setKeepCallback(false); // Keep callback
                rewardCallBack.sendPluginResult(pluginResult);
            }
        }
        else{
             //callbackContext.error("not initialised");
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.ERROR, "not initialised");
            pluginResult.setKeepCallback(false); // Keep callback
            rewardCallBack.sendPluginResult(pluginResult);
            }
    }

    public void TimerMethod(String type){
        if(isNoBannerOffer && type.equalsIgnoreCase("banner"))
            mKidozBannerView.load();
    }

}
