package com.projects.trending.chatify.activity;

import com.projects.trending.chatify.notification.MyResponse;
import com.projects.trending.chatify.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAivqs2TI:APA91bFnD0WVuuQy9cirrs5lHopxlFvdRM8FBSHP2an7fGGTbpQXzm7_0gZpEfzlmQMK0AitnAIwbfNld0yknMk0MtHpQJMk75UXE2_et4SFXv3cqzK7SQdMky8NlhcM5YQXZM5UuwRY"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}