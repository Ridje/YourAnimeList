package com.kis.youranimelist.ui.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ItemFragmentBinding
import com.kis.youranimelist.model.app.Anime
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemFragment : Fragment() {

    private var _binding: ItemFragmentBinding? = null
    private val binding get() = _binding!!
    private var anime : Anime? = null
    private val viewModel: ItemViewModel by viewModels()

    companion object {
        const val BUNDLE_EXTRA = "item_value"
        fun newInstance(bundle : Bundle) : ItemFragment {
            val fragment = ItemFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        anime = arguments?.getParcelable(BUNDLE_EXTRA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anime?.let { animeArgument ->
            viewModel.getLiveData().observe(viewLifecycleOwner, { render(it) })
            viewModel.getAnimeInfo(animeArgument)
        }

    }

    private fun render(itemState: ItemState) {
        when (itemState) {
            is ItemState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.itemContainer.visibility = View.VISIBLE
                anime = itemState.item
                renderItem()
            }
            is ItemState.Loading -> {
                binding.itemContainer.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is ItemState.Error -> {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (binding.itemNote.text.toString() != anime?.userNote) {
            anime?.let {
                it.userNote = binding.itemNote.text.toString()
                viewModel.writeAnimeNote(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        anime = null
    }

    private fun renderItem() {
        binding.apply {
            itemMean.text = anime?.mean.toString()
            itemTitle.text = anime?.title
            itemYear.text = anime?.startSeason?.year.toString()
            itemSynopsis.text = anime?.synopsis?.replace("[Written by MAL Rewrite]", "")?.trim() ?: getString(R.string.no_synopsis)
            itemNote.setText(anime?.userNote)
            Picasso.get().load(anime?.mainPicture?.large ?: anime?.mainPicture?.medium).error(R.drawable.default_image).into(posterView)
        }
    }
}
