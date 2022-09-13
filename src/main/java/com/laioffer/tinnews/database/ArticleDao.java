package com.laioffer.tinnews.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.laioffer.tinnews.model.Article;

import java.util.List;
//CRUD
@Dao
public interface ArticleDao {
    @Insert
    void saveArticle(Article article);

    @Query("SELECT * FROM Article")
    LiveData<List<Article>> getAllArticles();
    //LiveData is a data holder class that can be observed within a given lifecycle

    @Delete
    void deleteArticle(Article article);
}
