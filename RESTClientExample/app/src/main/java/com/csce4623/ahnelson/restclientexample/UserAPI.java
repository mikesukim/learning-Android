package com.csce4623.ahnelson.restclientexample;

/**
 * Created by ahnelson on 11/13/2017.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("users/")
    Call<List<User>> loadAllUsers();

    @GET("users/")
    Call<List<User>> loadUserByUserId(@Query("userId") int userId);

}