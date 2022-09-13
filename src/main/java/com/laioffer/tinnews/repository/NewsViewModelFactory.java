package com.laioffer.tinnews.repository;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.laioffer.tinnews.ui.home.HomeViewModel;
import com.laioffer.tinnews.ui.save.SaveViewModel;
import com.laioffer.tinnews.ui.search.SearchViewModel;

public class NewsViewModelFactory implements ViewModelProvider.Factory {//use factory model

    private final NewsRepository repository;

    public NewsViewModelFactory(NewsRepository repository) {
        this.repository = repository;
    }
//initialize using previous object
    //工厂模型，造view model，安卓不知道你的constructor，所以用factory才能让安卓去maintain相同的viewModel
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(repository);
        } else if (modelClass.isAssignableFrom(SaveViewModel.class)) {
            return (T) new SaveViewModel(repository);
        }else {
            throw new IllegalStateException("Unknown ViewModel");
        }
    }
}

