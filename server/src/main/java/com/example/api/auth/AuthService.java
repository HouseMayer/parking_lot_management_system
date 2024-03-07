package com.example.api.auth;

import com.example.properties.BaiduProperties;
import org.json.JSONObject;
import okhttp3.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;




@Component
public class AuthService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private BaiduProperties baiduProperties;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public String getAuth() throws IOException {

        ValueOperations valueOperations = redisTemplate.opsForValue();

        String accessToken = (String) valueOperations.get("access_token");
        if ( !(accessToken == null) ){
            return accessToken;
        }

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=" + baiduProperties.getApikey()
                        +"&client_secret=" + baiduProperties.getSecretKey() + "&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        accessToken = jsonObject.getString("access_token");


        valueOperations.set("access_token", accessToken);

        return accessToken;
    }
}
