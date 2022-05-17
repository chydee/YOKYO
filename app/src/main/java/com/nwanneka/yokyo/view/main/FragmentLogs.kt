package com.nwanneka.yokyo.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.FragmentLogsBinding
import com.nwanneka.yokyo.view.map.MapActivity
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.hide
import com.nwanneka.yokyo.view.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLogs : Fragment() {

    private var _binding: FragmentLogsBinding by autoCleared()
    private val binding
        get() = _binding

    private lateinit var logs: ArrayList<Logg>

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLogsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabCreateLog.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }
    }

    private fun configureRecyclerView() {
        if (logs.isNotEmpty()) {
            binding.listGroup.show()
            val logsAdapter = LogsAdapter()
            binding.logRecyclerView.adapter = logsAdapter
            logsAdapter.submitList(logs)
            binding.logRecyclerView.setHasFixedSize(true)

            logsAdapter.setOnItemClickListener(object : LogsAdapter.OnItemClickListener {

                override fun onLogItemClicked(logg: Logg) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            binding.emptyState.show()
            binding.listGroup.hide()
        }

    }

    private fun deleteLog() {

    }

    private fun shareLogInfo(content: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Yokyo 2022 Highlight")
        startActivity(shareIntent)
    }
}