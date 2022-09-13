package com.laioffer.tinnews.ui.search;

        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.widget.SearchView;
        import androidx.fragment.app.Fragment;
        import androidx.lifecycle.ViewModelProvider;
        import androidx.navigation.fragment.NavHostFragment;
        import androidx.recyclerview.widget.GridLayoutManager;

        import com.laioffer.tinnews.databinding.FragmentSearchBinding;
        import com.laioffer.tinnews.repository.NewsRepository;
        import com.laioffer.tinnews.repository.NewsViewModelFactory;

public class SearchFragment extends Fragment {
    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //corresponding xml file: fragment_search
//        return inflater.inflate(R.layout.fragment_search, container, false);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        //Not binding: //return inflater.inflate(R.layout.fragment_search, container, false);
        //not attach fragment with activity, should let navigation controller manage it
        return binding.getRoot();
     /*Now we have a reference to the binding. We do not need to use findViewById for each view.
        Any views with an @+id tag will have a binding automatically*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//OnQueryTextListener to the search view. We set the search input in the onQueryTextSubmit callback
        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();//new an Adapter
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        //set how many cols in a row

        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);
        binding.newsResultsRecyclerView.setAdapter(newsAdapter);

        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.setSearchInput(query);
                }
                binding.newsSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        NewsRepository newsRepository = new NewsRepository();
//        viewModel = new SearchViewModel(newsRepository); // NOT use this, use factory model
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(newsRepository))
                .get(SearchViewModel.class);

        viewModel.searchNews().observe(getViewLifecycleOwner(), newsResponse -> {
            if (newsResponse !=  null) {
                Log.d("SearchFragment", newsResponse.toString());
                newsAdapter.setArticles(newsResponse.articles);//show articles on screen
            }
        });
        newsAdapter.setItemCallback(article -> {
            SearchFragmentDirections.ActionNavigationSearchToNavigationDetails direction =
                    SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
            //get the navigation direction, provide the required argument article, and navigate to the direction
            NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
        });
    }
}
