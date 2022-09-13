package com.laioffer.tinnews.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.TinNewsDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsApi;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository
{

    private final NewsApi newsApi;
    private final TinNewsDatabase database;

    public NewsRepository() {

        newsApi = RetrofitClient.newInstance().create(NewsApi.class);
        database = TinNewsApplication.getDatabase();
    }
    public LiveData<NewsResponse> getTopHeadlines(String country) {//using retrofit
        //LiveData is a class like List
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();
        newsApi.getTopHeadlines(country)
                .enqueue(new Callback<NewsResponse>() {
      //enqueue传进去一个callback，内容是request拿到后要执行的代码
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        //execute in background
                        if (response.isSuccessful()) {//change data in LiveData
                            topHeadlinesLiveData.setValue(response.body());
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });
        return topHeadlinesLiveData;
    }
    public LiveData<NewsResponse> searchNews(String query) {//use retrofit
        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                everyThingLiveData.setValue(null);
                            }
                        });
        return everyThingLiveData;
    }
    public LiveData<Boolean> favoriteArticle(Article article) {//use room
        //every public method is an API, this is called in HomeViewModel line32
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        new FavoriteAsyncTask(database, resultLiveData).execute(article);
        return resultLiveData;
    }

    public LiveData<List<Article>> getAllSavedArticles() {//use room
        return database.articleDao().getAllArticles();
        //LiveData is a data holder class that can be observed within a given lifecycle
        //为什么getAll不doInBackground？
        // 因为在LiveData的使用中，如果你想让UI observe一些data，有两个方法：setValue和postValue，
        // 前者只能放在UI thread(main thread)里，后者只能放在background里。
        //LiveData这么好用，为什么delete不用这个实现？因为LiveData只支持getAll
    }

    public void deleteSavedArticle(Article article) {
        AsyncTask.execute(() -> database.articleDao().deleteArticle(article));
    }

    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {
        //如何定义自己的AsyncTask呢？这里能Override的method不止这三个，重点是尖括号
        //里的参数和method的输入输出参数类型有关，具体每个函数是如何对应的，需要看源码。
        //比如，这里尖括号里第一个参数对应doInBackground的输入，第三个对应返回值
/*Database query accessing the disk storage can be very slow sometimes.
We do not want it to run on the default main UI thread.
So we use an AsyncTask to start a new THREAD to dispatch the query work on background*/
        private final TinNewsDatabase database;
        private final MutableLiveData<Boolean> liveData;

        private FavoriteAsyncTask(TinNewsDatabase database, MutableLiveData<Boolean> liveData) {

            this.database = database;
            this.liveData = liveData;
        }

        @Override
        protected Boolean doInBackground(Article... articles) {
            //Everything inside doInBackground would be executed on a separate background thread.
            //so line 78 execute after line 105
            Article article = articles[0];
            try {
                database.articleDao().saveArticle(article);
            } catch (Exception e) {//if 'like' the same news, it will occur NPE
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //After doInBackground finishes, onPostExecute would be executed back on the
            // main UI thread.
            liveData.setValue(success);
        }
    }
}
