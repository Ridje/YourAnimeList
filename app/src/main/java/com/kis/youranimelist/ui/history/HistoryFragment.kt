package com.kis.youranimelist.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kis.youranimelist.R
import com.kis.youranimelist.databinding.HistoryFragmentBinding
import com.kis.youranimelist.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private val viewModel: HistoryViewModel by viewModels()

    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner) {
            renderHistory(it)
        }
    }

    fun renderHistory(state: HistoryState) {
        when (state) {
            is HistoryState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.root.showSnackBar(
                    getString(R.string.error_during_download),
                    getString(R.string.reload),
                    { viewModel.getViewHistory() })
            }

            is HistoryState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.explore.visibility = View.GONE
            }

            is HistoryState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.explore.visibility = View.VISIBLE
                binding.explore.adapter = HistoryAdapter(state.history)
            }
        }
    }
}