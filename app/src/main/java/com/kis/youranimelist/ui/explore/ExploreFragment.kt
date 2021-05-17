package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentBinding
import com.kis.youranimelist.model.Anime

class ExploreFragment : Fragment() {

    private var _binding: ExploreFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ExploreFragment()
    }

    private lateinit var viewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExploreFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { render(it)})
        viewModel.getAnimeListByGroup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun render(exploreState: ExploreState) {
        when (exploreState) {
            is ExploreState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.explore.adapter = ExploreAdapter(exploreState.animeData)
                binding.explore.setHasFixedSize(true);
            }
            is ExploreState.Loading -> { binding.progressBar.visibility = View.VISIBLE }
            is ExploreState.Error -> {
                binding.progressBar.visibility = View.GONE
                Snackbar
                .make(binding.root, "Error", Snackbar.LENGTH_INDEFINITE)
                .setAction("Reload") { viewModel.getAnimeListByGroup() }
                .show()
            }

        }
    }
}