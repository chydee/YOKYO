package com.nwanneka.yokyo.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.toObject
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.FragmentLogsBinding
import com.nwanneka.yokyo.view.main.modals.LogDetailsModal
import com.nwanneka.yokyo.view.main.modals.LogDetailsModalDelegate
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.hide
import com.nwanneka.yokyo.view.utils.show
import com.nwanneka.yokyo.view.utils.showToastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLogs : Fragment(), LogDetailsModalDelegate {


    private var _binding: FragmentLogsBinding by autoCleared()
    private val binding
        get() = _binding

    private lateinit var logs: ArrayList<Logg>

    private val viewModel: MainViewModel by viewModels()
    private lateinit var logsAdapter: LogsAdapter

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
        logs = arrayListOf()
        configureRecyclerView()
        val uid = viewModel.auth?.currentUser?.uid
        fetchMyLogs(uid)


        binding.fabCreateLog.setOnClickListener {
            findNavController().navigate(R.id.to_mapFragment)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showToastMessage(it)
        }
    }

    private fun configureRecyclerView() {
        logsAdapter = LogsAdapter()
        binding.logRecyclerView.adapter = logsAdapter
        logsAdapter.setOnItemClickListener(object : LogsAdapter.OnItemClickListener {

            override fun onLogItemClicked(logg: Logg) {
                val bundle: Bundle = Bundle()
                bundle.putParcelable("EXTRA_LOG", logg)
                val logDetailsModal = LogDetailsModal(this@FragmentLogs)
                logDetailsModal.arguments = bundle
                logDetailsModal.isCancelable = true
                logDetailsModal.show(childFragmentManager, logDetailsModal.tag)
            }
        })
    }

    private fun fetchMyLogs(uid: String?) {
        binding.progressLoader.show()
        uid?.let { viewModel.fetchAllMyLog(it) }
        viewModel.myLogs.observe(viewLifecycleOwner) { snapshot ->
            binding.progressLoader.hide()
            snapshot?.documents?.forEach {
                val log = it.toObject<Logg>()
                if (log != null) {
                    logs.add(log)
                }
            }
            if (logs.isNotEmpty()) {
                binding.listGroup.show()
                binding.emptyState.hide()
                logsAdapter.submitList(logs)
            } else {
                binding.listGroup.hide()
                binding.emptyState.show()
            }
        }
    }

    private fun deleteLog(logg: Logg) {
        viewModel.deleteLogg(logg)
        viewModel.isDelete.observe(viewLifecycleOwner) {
            if (it) {
                showToastMessage("Log Removed Successfully")
            }
        }
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

    override fun onRemove(logg: Logg) {
        deleteLog(logg)
        logs.remove(logg)
        logsAdapter.notifyDataSetChanged()
    }

    override fun onShare(content: String) {
        shareLogInfo(content)
    }
}