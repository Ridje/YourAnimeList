package com.kis.youranimelist.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kis.youranimelist.MainActivity
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.ExploreFragmentBinding
import com.kis.youranimelist.model.app.Anime
import com.kis.youranimelist.showSnackBar
import com.kis.youranimelist.ui.item.ItemFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: ExploreFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ExploreFragment()
    }

    private val viewModel: ExploreViewModel by viewModels()

    private val clickListener = { anime : Anime -> val
        bundle = Bundle()
        bundle.putParcelable(ItemFragment.BUNDLE_EXTRA, anime)
        (requireActivity() as MainActivity).navigateTo(ItemFragment.newInstance(bundle))
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
        binding.explore.adapter = ExploreAdapter(viewModel.results, clickListener)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun render(exploreState: ExploreState) {
        when (exploreState) {
            is ExploreState.LoadingResult -> {
                (binding.explore.adapter as? ExploreAdapter)?.notifyDataSetChanged()
            }
            is ExploreState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.root.showSnackBar(getString(R.string.error_during_download), getString(R.string.reload),  { viewModel.getAnimeListByGroup() })
            }
        }
    }
}