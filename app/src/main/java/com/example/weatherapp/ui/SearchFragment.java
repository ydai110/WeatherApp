package com.example.weatherapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherapp.R;
import com.example.weatherapp.data.CitySuggestionsModel;
import com.example.weatherapp.databinding.FragmentSearchBinding;
import com.example.weatherapp.ui.adapter.SuggestionsAdapter;
import com.example.weatherapp.util.Resource;
import com.example.weatherapp.util.UserLocation;
import com.example.weatherapp.viewmodel.LocationViewModel;
import com.example.weatherapp.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SearchFragment extends Fragment {

    private final String TAG = "SearchFragment";
    private FragmentSearchBinding binding;

    @Inject
    public LocationViewModel locationViewModel;

    private SearchViewModel searchViewModel;

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private SuggestionsAdapter suggestionsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) requireActivity()).locationComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initViewModel();
        initRecyclerView();
    }

    private void initUI() {
        binding.searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    searchViewModel.handleQuery(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProgressBar progressBar = requireActivity().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        suggestionsAdapter = new SuggestionsAdapter(new ArrayList<>(), onSuggestionItemClickListener);

        searchViewModel.getCitySuggestion().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource instanceof Resource.Success) {
                suggestionsAdapter.setData(
                        ((Resource.Success<List<CitySuggestionsModel>>) listResource)
                                .getValue()
                );
            }
        });

        binding.searchSuggestion.setAdapter(suggestionsAdapter);
        binding.searchSuggestion.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void initViewModel() {
        locationViewModel = new ViewModelProvider(
                requireActivity(),
                viewModelFactory
        ).get(LocationViewModel.class);

        searchViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(SearchViewModel.class);
    }

    private final SuggestionsAdapter.OnSuggestionItemClickListener onSuggestionItemClickListener
            = new SuggestionsAdapter.OnSuggestionItemClickListener() {
        @Override
        public void onClick(@NonNull UserLocation userLocation) {
            Log.d(TAG, "onClick: location is chosen");
            locationViewModel.setCurrentLocation(userLocation);
            ProgressBar progressBar = requireActivity().findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            NavHostFragment.findNavController(SearchFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        }
    };
}
