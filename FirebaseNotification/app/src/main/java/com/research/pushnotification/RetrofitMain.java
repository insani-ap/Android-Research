package com.research.pushnotification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class RetrofitMain {
    private static String serverKey = "key=AAAAcd1vkt4:APA91bEfLe6yOIq15PQ4gY1L-W9yNIG1VoVef1Q5tortpKK4OBV7RoxMo-BnMc4tTX2QRGQrekGKnGtjOjWRN2O6d2KZyEVeZVFUFFfo_mZI2jJAEvfNwvDXVlz81syR6sr1XaWIg4GX";

    private static Retrofit getInstance(String url) {
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    public static void sendPushNotifVersion1(MainCallback mainCallback, String url, String token) {
        Retrofit retrofit = getInstance(url);
        MasterService req = retrofit.create(MasterService.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", serverKey);
        headers.put("Content-Type", "application/json");

        Map<String, Object> notifBody = new HashMap<>();
        notifBody.put("title", "Example Push Notification!");
        notifBody.put("body", "What's up bro? You're fine? Here the Gift!");
        notifBody.put("image", "https://thumbs.dreamstime.com/z/new-modern-frameless-smartphone-mockup-white-screen-isolated-background-black-blank-based-high-quality-studio-shot-design-99882006.jpg");
        notifBody.put("sound", "default");
        notifBody.put("tag", 9);
        notifBody.put("click_action", "Notification");

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("value", "Amazing!!! V1");
        dataBody.put("version", 1);

        Map<String, Object> body = new HashMap<>();
        body.put("to", token);
        body.put("notification", notifBody);
        body.put("data", dataBody);

        Call<ResponseBody> res = req.doSendPushNotificationLegacy(headers, body);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    mainCallback.resultOk(null);
                else
                    mainCallback.resultFail(new Throwable());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainCallback.resultFail(t);
            }
        });
    }

    public static void sendPushNotifVersion2(MainCallback mainCallback, String url, String token) {
        Retrofit retrofit = getInstance(url);
        MasterService req = retrofit.create(MasterService.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", serverKey);
        headers.put("Content-Type", "application/json");

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("title", "Example Push Notification!");
        dataBody.put("body", "What's up bro? You're fine? Here the Gift!");
        dataBody.put("image", "https://thumbs.dreamstime.com/z/new-modern-frameless-smartphone-mockup-white-screen-isolated-background-black-blank-based-high-quality-studio-shot-design-99882006.jpg");
        dataBody.put("tag", 9);
        dataBody.put("value", "Amazing!!! V2");
        dataBody.put("version", 2);

        Map<String, Object> body = new HashMap<>();
        body.put("to", token);
        body.put("data", dataBody);

        Call<ResponseBody> res = req.doSendPushNotificationLegacy(headers, body);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    mainCallback.resultOk(null);
                else
                    mainCallback.resultFail(new Throwable());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainCallback.resultFail(t);
            }
        });
    }

    public static void sendPushNotifVersion3(MainCallback mainCallback, String url, String topic) {
        Retrofit retrofit = getInstance(url);
        MasterService req = retrofit.create(MasterService.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", serverKey);
        headers.put("Content-Type", "application/json");

        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("title", "FCM Message!");
        dataBody.put("body", "This is a Firebase Cloud Messaging Topic Message!");
        dataBody.put("tag", 9);
        dataBody.put("value", "Amazing!!! V3 Custom");
        dataBody.put("version", 3);

        Map<String, Object> body = new HashMap<>();
        body.put("to", "/topics/" + topic);
        body.put("data", dataBody);

        Call<ResponseBody> res = req.doSendPushNotificationLegacy(headers, body);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    mainCallback.resultOk(null);
                else
                    mainCallback.resultFail(new Throwable());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainCallback.resultFail(t);
            }
        });
    }

    public static void sendPushNotifVersion5(MainCallback mainCallback, String url, String token) throws IOException {
        String notificationId = "pushnotification-85df1";

        Retrofit retrofit = getInstance(url);
        MasterService req = retrofit.create(MasterService.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getTokenForNewEndpoint());
        headers.put("Content-Type", "application/json");

        Map<String, String> dataBody = new HashMap<>();
        dataBody.put("title", "EX Messaging");
        dataBody.put("body", "Example Messaging from New Endpoint!");
        dataBody.put("tag", "9");
        dataBody.put("image", "https://initiate.alphacoders.com/download/wallpaper/528166/images5/jpg/688231163492874");
        dataBody.put("value", "V5 New Endpoint");
        dataBody.put("version", "5");

        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("token", token);
        messageBody.put("data", dataBody);

        Map<String, Object> body = new HashMap<>();
        body.put("message", messageBody);

        Call<ResponseBody> res = req.doSendPushNotification(headers, body, notificationId);
        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    mainCallback.resultOk(null);
                else
                    mainCallback.resultFail(new Throwable());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainCallback.resultFail(t);
            }
        });
    }

    private static String getTokenForNewEndpoint() throws IOException {
        Retrofit retrofit = getInstance("http://10.10.1.220:8080");
        MasterService req = retrofit.create(MasterService.class);
        Call<HashMap<String, String>> res = req.getTokenFromServer();
        Response<HashMap<String, String>> response = res.execute();
        if (response.isSuccessful()) {
            return response.body().get("token");
        } else {
            return null;
        }
    }
}

interface MasterService {
    @POST("/fcm/send")
    Call<ResponseBody> doSendPushNotificationLegacy(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body);

    @POST("/v1/projects/{notificationId}/messages:send")
    Call<ResponseBody> doSendPushNotification(@HeaderMap Map<String, String> headers, @Body Map<String, Object> body, @Path(value = "notificationId") String notificationId);

    @GET("/firebase_token")
    Call<HashMap<String, String>> getTokenFromServer();
}

interface MainCallback {
    void resultOk(Object o);
    void resultFail(Throwable t);
}
