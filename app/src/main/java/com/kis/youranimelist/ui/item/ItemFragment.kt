package com.kis.youranimelist.ui.item

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ItemFragmentBinding
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.showSnackBar

class ItemFragment : Fragment() {

    private var _binding: ItemFragmentBinding? = null
    private val binding get() = _binding!!
    private val anime : Anime? by lazy {
        arguments?.getParcelable(BUNDLE_EXTRA)
    }

    companion object {
        const val BUNDLE_EXTRA = "item_value"
        fun newInstance(bundle : Bundle) : ItemFragment {
            val fragment = ItemFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel: ItemViewModel by lazy {
        ViewModelProvider(this).get(ItemViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener { (requireActivity() as MainActivity).navigateBack() }

        anime?.let { animeArgument ->
            viewModel.getLiveData().observe(viewLifecycleOwner, { render(it) })
            viewModel.getAnimeInfo(animeArgument)
        } ?: (requireActivity() as MainActivity).navigateBack()

    }

    private fun render(itemState: ItemState) {
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
                binding.root.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { anime?.let { viewModel.getAnimeInfo(it) } })
            }
        }
    }

    private fun renderItem(item : Anime) {
        binding.apply {
            itemMean.text = item.mean.toString()
            itemTitle.text = item.title
            itemYear.text = item.year.toString()
            itemSynopsys.text = item.synopsys ?: getString(R.string.no_synopsys)
        }
    }
}