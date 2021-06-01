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
import com.kis.youranimelist.databinding.ItemFragmentBinding
import com.kis.youranimelist.model.Anime
import com.squareup.picasso.Picasso

class ItemFragment : Fragment() {

    private var _binding: ItemFragmentBinding? = null
    private val binding get() = _binding!!
    private var anime : Anime? = null

    companion object {
        const val BUNDLE_EXTRA = "item_value"
        fun newInstance(bundle : Bundle) : ItemFragment {
            val fragment = ItemFragment()
            fragment.arguments = bundle
            return fragment
        }
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
        binding.buttonBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (requireActivity() as MainActivity).navigateBack()
            }
        })

        anime = arguments?.getParcelable<Anime>(BUNDLE_EXTRA)
        if (anime != null) {
            viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
            viewModel.getLiveData().observe(viewLifecycleOwner, { render(it) })
            viewModel.getAnimeInfo(anime!!)
        } else {
            (requireActivity() as MainActivity).navigateBack()
        }

    }

    fun render(itemState: ItemState) {
        when (itemState) {
            is ItemState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.itemContainer.visibility = View.VISIBLE
                renderItem(itemState.item)
            }
            is ItemState.Loading -> {
                binding.itemContainer.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is ItemState.Error -> {
                binding.progressBar.visibility = View.GONE
                Snackbar
                    .make(binding.root, "Error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload") { viewModel.getAnimeInfo(anime!!) }
                    .show()
            }
        }
    }

    private fun renderItem(item : Anime) {
        binding.itemMean.text = item.mean.toString()
        binding.itemTitle.text = item.title
        binding.itemYear.text = item.startSeason?.year.toString()
        binding.itemSynopsis.text = item.synopsis ?: "No synopsis"
        Picasso.get().load(item.mainPicture?.large ?: item.mainPicture?.medium).error(R.drawable.default_image).into(binding.posterView)
    }
}