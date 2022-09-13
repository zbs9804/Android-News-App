package com.laioffer.tinnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);//get view by id
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        //所有的fragment都是host fragment下面的，所以我如果想跳转到sibling，需要先走到LCA，再走到sibling
        // click on tab on BottomNavView can navigate by id
        NavigationUI.setupWithNavController(navView, navController);
        // can display label on action bar
        NavigationUI.setupActionBarWithNavController(this, navController);
        //all settings are about getting navController

        //let retrofit implement NewsApi interface
        NewsApi api = RetrofitClient.newInstance().create(NewsApi.class);

        //固定写法：主线程只做UI，以保证UI不卡，所以让IO等请求放在后台，这样UI才不卡，enqueue是让looper上的worker处理queue里人请求
        api.getTopHeadlines("us").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {//request successful
                if (response.isSuccessful()) {
                    Log.d("getTopHeadlines", response.body().toString());
                } else {
                    Log.d("getTopHeadlines", response.toString());
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {//request fail
                Log.d("getTopHeadlines", t.toString());//d is for debug, e is for error
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        // can click back
        return navController.navigateUp();
    }
}
