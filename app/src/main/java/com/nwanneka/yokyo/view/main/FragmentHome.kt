package com.nwanneka.yokyo.view.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.getValue
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Highlight
import com.nwanneka.yokyo.databinding.FragmentHomeBinding
import com.nwanneka.yokyo.view.DebouncingQueryTextListener
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.hide
import com.nwanneka.yokyo.view.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentHome : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()

    private val viewModel: MainViewModel by viewModels()

    private lateinit var highlights: ArrayList<Highlight>
    private lateinit var highlightAdapter: HighlightAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        highlights = arrayListOf()
        highlightAdapter = HighlightAdapter()
        configureSearchView()
        setupHomeMenu()
        fetchHighlights()
    }

    private fun configureSearchView() {
        binding.searchView.setOnQueryTextListener(
            DebouncingQueryTextListener(
                this.lifecycle
            ) { newText ->
                newText?.let {
                    if (it.isNotEmpty()) {
                        // Do the search here
                    }
                }
            })
    }

    private fun fetchHighlights() {
        binding.progressLoader.show()
        viewModel.fetchHighlights()
        highlights.clear()
        highlightAdapter.currentList.clear()
        viewModel.highlightSnapshot.observe(viewLifecycleOwner) { snapshot ->
            binding.progressLoader.hide()
            snapshot?.children?.forEach {
                highlights.add(
                    Highlight(
                        id = it.child("id").getValue<Int>() as Int,
                        thumbnail = it.child("thumbnail").getValue<String>() as String,
                        title = it.child("title").getValue<String>() as String,
                        videoID = it.child("videoID").getValue<String>() as String
                    )
                )
            }
            configureAdapter()
        }
    }


    private fun setupHomeMenu() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.fragmentAbout -> {
                        findNavController().navigate(R.id.to_fragmentAbout)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureAdapter() {
        if (highlights.isNotEmpty()) {
            binding.highlightsRecyclerView.adapter = highlightAdapter
            highlightAdapter.submitList(highlights)
            binding.highlightsRecyclerView.setHasFixedSize(true)

            highlightAdapter.setOnItemClickListener(object : HighlightAdapter.OnItemClickListener {
                override fun onHighlightClick(highlight: Highlight) {
                    openYoutubeLink(highlight.videoID)
                }

                override fun onShareClicked(content: String) {
                    share(content)
                }
            })
        } else {
            binding.emptyState.show()
            binding.highlightsRecyclerView.hide()
        }

    }

    private fun openYoutubeLink(youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$youtubeID"))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youtubeID"))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }

    }

    private fun share(content: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Yokyo 2022 Highlight")
        startActivity(shareIntent)
    }

}