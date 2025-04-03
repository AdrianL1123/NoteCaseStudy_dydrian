package com.dydrian.mob22.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dydrian.mob22.R
import com.dydrian.mob22.databinding.FragmentBtmSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetDialogFragment(
    private val id: String,
) : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentBtmSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBtmSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edit.setOnClickListener {
            TODO("Waiting for edit note fragment to be implemented")
        }

        binding.delete.setOnClickListener {
            showDeleteDialogBox(id)
        }
    }

    private fun showDeleteDialogBox(id: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnDelete = dialog.findViewById<Button>(R.id.btnDelete)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnDelete.setOnClickListener {
            viewModel.deleteNote(id) // Calls ViewModel function to delete the note
            dialog.dismiss() // Closes the dialog
            this.dismiss() // Also dismisses the BottomSheetDialogFragment (if called from one)
        }
        dialog.show()
    }
}