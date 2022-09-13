package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;

public class HomeFragment extends Fragment implements CardStackListener {
    //homefragment既是fragment，也是view model owner，
    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 1. new task -> main
        // 2. jump to -> background thread
        // 3. on background thread, do task
        // 4. on background thread, task done
        // 5. back to main thread

        // futher work
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
        // return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.homeLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeCard(Direction.Right);
            }
        });
        binding.homeUnlikeButton.setOnClickListener(v -> {
            swipeCard(Direction.Left);
        });

        // Setup CardStackView
        CardSwipeAdapter cardSwipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        layoutManager.setStackFrom(StackFrom.Top);
        binding.homeCardStackView.setAdapter(cardSwipeAdapter);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        //in order to make cardSwipeView, get articles from notifyDataChanged() to notify viewModel
        //at line 74


        NewsRepository repository = new NewsRepository();
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(HomeViewModel.class);
//这是多线程的固定写法，当屏幕旋转时，会destroy所有previous的，re-create所有新建的，但如何知道谁是谁呢，这就需要一个owner
//这个viewModel就是个owner。每次re-create的时候销毁其他的，保存viewModel的旧版。可以在call stack里看变量id，无论怎么转，
//这个的id永远不变
//newed by factory model, initialized with previous value, to retain previous state when rotate screen
//news was initialized to null, now go to another thread to fetch news, in the background,
//the main thread here is observing data, when the news changes, will trigger onChange on main thread

        viewModel.getTopHeadlines().observe(getViewLifecycleOwner(), new Observer<NewsResponse>() {
            @Override
            public void onChanged(NewsResponse newsResponse) {
                if (newsResponse != null) {
                    Log.d("HomeFragment", newsResponse.toString());
                    articles = newsResponse.articles;
                    cardSwipeAdapter.setArticles(articles);
                }
            }
        });
        viewModel.setCountryInput("us");//if this changes, re-render
    }

    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration((Duration.Normal.duration))
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }

    @Override
    public void onCardDragging(Direction direction, float v) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked " + layoutManager.getTopPosition());
            Article article = articles.get(layoutManager.getTopPosition() - 1);
            viewModel.setFavoriteArticleInput(article);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int i) {

    }

    @Override
    public void onCardDisappeared(View view, int i) {

    }
}