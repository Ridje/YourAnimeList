package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentBinding
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.ui.item.ItemFragment

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
        _binding = ExploreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                binding.explore.adapter = ExploreAdapter(exploreState.animeData,
                    object : ExploreItemsAdapter.OnItemClickListener {
                        override fun onItemClickListener() {
                            (requireActivity() as MainActivity).navivageTo(ItemFragment.newInstance(), true)
                        }
                    })
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