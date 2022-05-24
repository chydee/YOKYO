package com.nwanneka.yokyo.view

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * class [DebouncingQueryTextListener]
 * Listen to SearchView onQueryTextChange and also handle debounce
 * @constructor lifecycle is application's lifecycle
 * @constructor onDebouncingQueryTextChange takes an expression [Unit]
 * @returns [SearchView.OnQueryTextListener]
 * ... and implements [LifecycleObserver]
 */

open class DebouncingQueryTextListener(
    lifecycle: Lifecycle,
    private val onDebouncingQueryTextChange: (String?) -> Unit
) : SearchView.OnQueryTextListener, LifecycleObserver {
    private var debouncePeriod: Long = 1000L
    private val coroutineScope: CoroutineScope = lifecycle.coroutineScope

    private var searchJob: Job? = null

    override fun onQueryTextChange(newText: String?): Boolean {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                onDebouncingQueryTextChange(it)
            }
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            query?.let {
                onDebouncingQueryTextChange(it)
            }
        }
        return false
    }

}