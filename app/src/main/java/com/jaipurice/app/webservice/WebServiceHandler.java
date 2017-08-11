package com.jaipurice.app.webservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.jaipurice.app.application.MyApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author       :   Mukesh Kumawat
 * Designation  :   Android Developer
 * E-mail       :   mukeshkmtskr@gmail.com
 * Date         :   18/02/2016
 * Company      :   Parasme Softwares & Technology
 * Purpose      :   Class to Handle All Web Services call in App
 * Description  :   Description.
 */
public class WebServiceHandler {
    private OkHttpClient okHttpClient;
    private OkHttpClient client = new OkHttpClient();
    private RequestBody requestBody;
    private Request request;
    private Context context;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private ProgressDialog progressDialog;
    public WebServiceListener serviceListener;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public WebServiceHandler(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void post(String url, FormBody.Builder builder) throws IOException {
        progressDialog.show();
        if (MyApplication.isConnectingToInternet()) {
            Log.e("POSTURL", url);
            RequestBody requestbody = builder.build();
            request = new Request.Builder()
                    .url(url)
                    .post(requestbody)
                    .build();


            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (progressDialog!=null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    serviceListener.onResponse(response.body().string());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("ERROR", e.toString());
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                }


            });
        }else{
            MyApplication.showInternetUnavailableDialog((Activity) context);
        }
    }

    public void get(String url) throws IOException {
        progressDialog.show();
        Log.e("GETURL",url);
        if (MyApplication.isConnectingToInternet()) {
            request =  new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    serviceListener.onResponse(response.body().string());

                }

                @Override
                public void onFailure(Call call, IOException e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });
        }else{
            MyApplication.showInternetUnavailableDialog((Activity) context);
        }
    }

    public static FormBody.Builder createBuilder(String [] paramsName, String [] paramsValue){
        FormBody.Builder builder=new FormBody.Builder();
        Log.e("kuch", "createBuilder: "+paramsName.toString() );
        Log.e("kuch", "createBuilder: "+paramsValue.toString() );
        for(int i=0;i<paramsName.length;i++){
            Log.e("values", "createBuilder: "+paramsValue[i] );
            builder.add(paramsName[i],paramsValue[i]);
        }

        Log.e("builder", "createBuilder: "+builder.toString() );
        return builder;
    }


}



