package com.kis.youranimelist.ui.explore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentBinding
import com.kis.youranimelist.model.Anime
import com.kis.youranimelist.model.ranking_response.AnimeRankingItem
import com.kis.youranimelist.showSnackBar
import com.kis.youranimelist.ui.item.ItemFragment

class ExploreFragment : Fragment() {

    private var _binding: ExploreFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ExploreFragment()
    }

    private val viewModel: ExploreViewModel by lazy {
        ViewModelProvider(this).get(ExploreViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExploreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        override fun onItemClickListener(anime : Anime) {
                            val bundle = Bundle()
                            bundle.putParcelable(ItemFragment.BUNDLE_EXTRA, anime)
                            (requireActivity() as MainActivity).navivageTo(ItemFragment.newInstance(bundle))
                        }
                    })
                binding.explore.setHasFixedSize(true);
            }
            is ExploreState.Loading -> { binding.progressBar.visibility = View.VISIBLE }
            is ExploreState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.root.showSnackBar(getString(R.string.error), getString(R.string.reload),  { viewModel.getAnimeListByGroup() })
            }
        }
    }
}