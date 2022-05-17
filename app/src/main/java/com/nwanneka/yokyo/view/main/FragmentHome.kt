package com.nwanneka.yokyo.view.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.ktx.getValue
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Highlight
import com.nwanneka.yokyo.databinding.FragmentHomeBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.hide
import com.nwanneka.yokyo.view.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentHome : Fragment() {

    private var binding: FragmentHomeBinding by autoCleared()

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var highlights: ArrayList<Highlight>
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
        setupHomeMenu()
        fetchHighlights()
    }

    private fun fetchHighlights() {
        binding.progressLoader.show()
        highlights = arrayListOf()
        viewModel.fetchHighlights()
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
            val highlightsAdapter = HighlightAdapter(initGlide())
            binding.highlightsRecyclerView.adapter = highlightsAdapter
            highlightsAdapter.submitList(highlights)
            binding.highlightsRecyclerView.setHasFixedSize(true)

            highlightsAdapter.setOnItemClickListener(object : HighlightAdapter.OnItemClickListener {
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

    private fun initGlide(): RequestManager {
        val options = RequestOptions()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
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