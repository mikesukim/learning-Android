package com.csce4623.ahnelson.restclientexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//implements Callback<List<Post>>
public class MainActivity extends Activity {

    ArrayList<Post> myPostList;
    ArrayList<User> myUserList;
    Hashtable<Integer, User> myUserDict;
    ListView lvPostVList;
    PostAdapter myPostAdapter;

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.csce4623.ahnelson.restclientexample.sharedprefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        lvPostVList = (ListView)findViewById(R.id.lvPostList);
        lvPostVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(parent, view, position,id);
            }
        });

        startQueryForUser();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    void itemClicked(AdapterView<?> parent, View view, int position, long id){

        Intent myIntent = new Intent(this,PostView.class);
        myIntent.putExtra("postId",myPostList.get(position).getId());
        myIntent.putExtra("postTitle",myPostList.get(position).getTitle());
        myIntent.putExtra("postBody",myPostList.get(position).getBody());
        User user = myUserDict.get(myPostList.get(position).getUserId());
        myIntent.putExtra("postUser", user);

        startActivity(myIntent);
    }

    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public void startQueryForUser() {

        Debug.startMethodTracing("test");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        //request users
        UserAPI userAPI = retrofit.create(UserAPI.class);
        Call<List<User>> callUser = userAPI.loadAllUsers();
        callUser.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()) {

                    myUserDict = new Hashtable<>();
                    myUserList = new ArrayList<User>(response.body());
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();

                    for (User user:myUserList) {
                        Log.d("MainActivity","NAME: " + user.getName());
                        myUserDict.put(user.getId(),user);

//                        save at Shared Preferences
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        preferencesEditor.putString(Integer.toString(user.getId()), json);
                        preferencesEditor.apply();
                    }
                    startQueryForPosts();
                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void startQueryForPosts() {

        Debug.startMethodTracing("test");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //request posts
        PostAPI postAPI = retrofit.create(PostAPI.class);
        Call<List<Post>> call = postAPI.loadPosts();
        call.enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()) {
                    myPostList = new ArrayList<Post>(response.body());
                    myPostAdapter = new PostAdapter(getApplicationContext(),myPostList);
                    lvPostVList.setAdapter(myPostAdapter);
                    for (Post post:myPostList) {
                        Log.d("MainActivity","ID: " + post.getId());
                    }
                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    protected class PostAdapter extends ArrayAdapter<Post> {
        public PostAdapter(Context context, ArrayList<Post> posts) {
            super(context, 0, posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Post post = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
            TextView tvUserName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            // Populate the data into the template view using the data object
            tvTitle.setText(post.getTitle());
            tvId.setText(Integer.toString(post.getId()));
            User user = myUserDict.get(post.getUserId());
            tvUserName.setText(user.getUsername());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
