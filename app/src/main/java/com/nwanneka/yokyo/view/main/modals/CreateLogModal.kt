package com.nwanneka.yokyo.view.main.modals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.SheetCreateLogBinding
import com.nwanneka.yokyo.view.utils.autoCleared
import java.text.SimpleDateFormat
import java.util.*

class CreateLogModal(private val delegate: CreateLogModalDelegate) : BottomSheetDialogFragment() {

    private var _binding: SheetCreateLogBinding by autoCleared()
    private val binding
        get() = _binding

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetCreateLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            getDateAndTime()
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun getDateAndTime() {
        val currentTime: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("dd LLL, yyyy HH:mm aaa z", Locale.ROOT)
        val dateTime = simpleDateFormat.format(currentTime).toString()

        Log.d("DateNow", dateTime)
    }
}

interface CreateLogModalDelegate {
    fun onCancel(log: Logg)
    fun onSave(log: Logg)
}