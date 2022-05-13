package com.nwanneka.yokyo.view.main

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.ktx.getValue
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Highlight
import com.nwanneka.yokyo.databinding.FragmentHomeBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import com.nwanneka.yokyo.view.utils.showToastMessage
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
        highlights = arrayListOf()
        viewModel.fetchHighlights()
        viewModel.highlightSnapshot.observe(viewLifecycleOwner) { snapshot ->
            snapshot?.children?.forEach {
                highlights.add(
                    Highlight(
                        id = it.child("id").getValue<Int>() as Int,
                        title = it.child("title").getValue<String>() as String,
                        url = it.child("url").getValue<String>() as String
                    )
                )
            }

            if (highlights.isNotEmpty()) {
                showToastMessage(highlights.size.toString())
            }
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

}