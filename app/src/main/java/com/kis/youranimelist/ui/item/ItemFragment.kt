package com.kis.youranimelist.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ItemFragmentBinding
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.navigateBack
import com.squareup.picasso.Picasso
import com.kis.youranimelist.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemFragment : Fragment() {

    private var _binding: ItemFragmentBinding? = null
    private val binding get() = _binding!!
    private val anime : Anime? by lazy {
        arguments?.getParcelable(BUNDLE_EXTRA)
    }
    private val viewModel: ItemViewModel by viewModels()

    companion object {
        const val BUNDLE_EXTRA = "item_value"
        fun newInstance(bundle : Bundle) : ItemFragment {
            val fragment = ItemFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemFragmentBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setVisibilityBottomNavigationMenu(View.GONE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener { requireActivity().navigateBack()}

        anime?.let { animeArgument ->
            viewModel.getLiveData().observe(viewLifecycleOwner, { render(it) })
            viewModel.getAnimeInfo(animeArgument)
        } ?: requireActivity().navigateBack()

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
                    getString(R.string.error_during_download),
                    getString(R.string.reload),
                    { anime?.let { viewModel.getAnimeInfo(it) } })
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (binding.itemNote.text.toString() != anime?.userNotes) {
            //save later to db
            anime?.userNotes = binding.itemNote.text.toString()
        }
    }

    private fun renderItem(item : Anime) {
        binding.apply {
            itemMean.text = item.mean.toString()
            itemTitle.text = item.title
            itemYear.text = item.startSeason?.year.toString()
            itemSynopsis.text = item.synopsis?.replace("[Written by MAL Rewrite]", "")?.trim() ?: getString(R.string.no_synopsis)
            Picasso.get().load(item.mainPicture?.large ?: item.mainPicture?.medium).error(R.drawable.default_image).into(posterView)
        }
    }
}