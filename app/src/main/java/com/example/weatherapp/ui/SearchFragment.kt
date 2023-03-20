package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.ui.adapter.SuggestionsAdapter
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.UserLocation
import com.example.weatherapp.viewmodel.LocationViewModel
import com.example.weatherapp.viewmodel.SearchViewModel
import javax.inject.Inject

private const val TAG = "SearchFragment"

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var locationViewModel: LocationViewModel

//    private lateinit var weatherRepository: WeatherRepository

    //    @Inject
    lateinit var searchViewModel: SearchViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var suggestionsAdapter: SuggestionsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).locationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initViewModel()
        initRecyclerView()
    }

    private fun initUI() {
        binding.searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $query")
                query?.let {
                    searchViewModel.handleQuery(query)
                }
                return true
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val progressBar = requireActivity().findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.isVisible = false
    }

    private fun initRecyclerView() {
        suggestionsAdapter = SuggestionsAdapter(mutableListOf(), onSuggestionItemClickListener)

        searchViewModel.citySuggestion.observe(viewLifecycleOwner, Observer {
            if (it is Resource.Success) {
                suggestionsAdapter.setData(it.value)
            }
        })

        binding.searchSuggestion.apply {
            adapter = suggestionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun initViewModel() {
        locationViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[LocationViewModel::class.java]

        searchViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[SearchViewModel::class.java]
    }

    private val onSuggestionItemClickListener =
        object : SuggestionsAdapter.OnSuggestionItemClickListener {
            override fun onClick(userLocation: UserLocation) {
                Log.d(TAG, "onClick: location is chosen")
                locationViewModel.setCurrentLocation(userLocation)
                val progressBar = requireActivity().findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.isVisible = true
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
