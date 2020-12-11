package com.robocon.onvif.demo;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class AppController  {


    private static AppController mInstance;
    private Context mCtx;
    private RequestQueue mRequestQueue;

    public AppController() {
    }


    private AppController(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized AppController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppController(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        try {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    250000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //
            req.setShouldCache(false);
            getRequestQueue().add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
