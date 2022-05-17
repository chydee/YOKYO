package com.nwanneka.yokyo.view.main.modals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.SheetLogDetailsBinding
import com.nwanneka.yokyo.view.main.MainViewModel
import com.nwanneka.yokyo.view.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogDetailsModal constructor(private val delegate: LogDetailsModalDelegate) : BottomSheetDialogFragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: SheetLogDetailsBinding by autoCleared()
    private val binding
        get() = _binding

    private lateinit var mContext: Context

    private var myLogg: Logg? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetLogDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            myLogg = requireArguments().getParcelable("EXTRA_LOG")
            myLogg?.let { populateUi(it) }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    private fun populateUi(logg: Logg) {
        binding.logDetailsText.text = """
            You were at ${logg.location} on ${logg.date} at ${logg.time}
        """.trimIndent()

        val shareable = """
            I was at ${logg.location} on ${logg.date} at ${logg.time}
        """.trimIndent()

        binding.btnRemove.setOnClickListener {
            delegate.onRemove(logg)
            dismiss()
        }

        binding.btnShare.setOnClickListener {
            delegate.onShare(shareable)
            dismiss()
        }
    }
}

interface LogDetailsModalDelegate {
    fun onRemove(logg: Logg)
    fun onShare(content: String)
}