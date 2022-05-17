package com.nwanneka.yokyo.view.main.modals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.SheetCreateLogBinding
import com.nwanneka.yokyo.view.main.MainViewModel
import com.nwanneka.yokyo.view.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateLogModal(private val delegate: CreateLogModalDelegate) : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: SheetCreateLogBinding by autoCleared()
    private val binding
        get() = _binding

    private lateinit var mContext: Context

    private var latitude: Long? = null
    private var longitude: Long? = null
    private var location: String? = null

    private var date: String = ""
    private var time: String = ""

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

        if (arguments != null) {
            location = requireArguments().getString("EXTRA_LOCATION")
            longitude = requireArguments().getLong("EXTRA_LONG")
            latitude = requireArguments().getLong("EXTRA_LAT")
        }

        val uid = viewModel.auth?.currentUser?.uid

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener { it ->
            it.isEnabled = false
            getDateAndTime()
            if (uid != null) {
                val log = Logg(
                    uid = uid,
                    location = location ?: "",
                    long = longitude ?: 0L,
                    lat = latitude ?: 0L,
                    date = date,
                    time = time
                )
                viewModel.createLogg(log)
                viewModel.logLiveData.observe(this) { doc ->
                    if (doc != null) {
                        delegate.onSave(log)
                        dismiss()
                    }
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun getDateAndTime() {
        val currentTime: Date = Calendar.getInstance().time
        val simpleDateFormatForDate = SimpleDateFormat("dd LLL, yyyy")
        val simpleDateFormatForTime = SimpleDateFormat("HH:mmaaa z")
        date = simpleDateFormatForDate.format(currentTime).toString()
        time = simpleDateFormatForTime.format(currentTime).toString()
        Log.d("DateNow", "You were at Beijing City on $date at $time")
    }
}

interface CreateLogModalDelegate {
    fun onCancel()
    fun onSave(log: Logg)
}