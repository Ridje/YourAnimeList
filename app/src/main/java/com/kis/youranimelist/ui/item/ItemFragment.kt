package com.kis.youranimelist.ui.item

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentBinding
import com.kis.youranimelist.databinding.ItemFragmentBinding
import com.kis.youranimelist.ui.explore.ExploreAdapter
import com.kis.youranimelist.ui.explore.ExploreItemsAdapter
import com.kis.youranimelist.ui.explore.ExploreState
import com.kis.youranimelist.ui.explore.ExploreViewModel

class ItemFragment : Fragment() {

    private var _binding: ItemFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ItemFragment()
    }

    private lateinit var viewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, { render(it) })
        viewModel.getAnimeInfo()
    }

    fun render(itemState: ItemState) {
        when (itemState) {
            is ItemState.Success -> {
                binding.progressBar.visibility = View.GONE
            }
            is ItemState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is ItemState.Error -> {
                binding.progressBar.visibility = View.GONE
                Snackbar
                    .make(binding.root, "Error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload") { viewModel.getAnimeInfo() }
                    .show()
            }
        }
    }
}