package com.dydrian.mob22.ui.details

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dydrian.mob22.R
import com.dydrian.mob22.databinding.FragmentDetailsBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noteId = args.noteId

        viewModel.handleIntent(DetailIntent.GetNoteById(noteId))
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.contentLayout.visibility =
                    if (state.note != null) View.VISIBLE else View.GONE

                if (state.isDeleted) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.delete_successfully), Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack() // Navigate back after deletion
                }

                state.note?.let { note ->
                    binding.tvTitle.text = note.title
                    binding.tvDesc.text = note.desc
                    binding.cardBg.setBackgroundColor(note.color)
                }
                binding.btnDetailsToHome.setOnClickListener {
                    findNavController().popBackStack()
                }
                binding.btnDelete.setOnClickListener {
                    showDeleteDialog(noteId)
                }
                binding.btnEdit.setOnClickListener {
                    findNavController().navigate(
                        DetailsFragmentDirections
                            .actionDetailsFragmentToEditNoteFragment(noteId)
                    )
                }
            }
        }
    }

    private fun showDeleteDialog(noteId: String) {
        val dialogView = layoutInflater.inflate(R.layout.delete_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            viewModel.handleIntent(DetailIntent.DeleteNote(noteId))
            dialog.dismiss()
        }

        dialog.show()
    }

}
