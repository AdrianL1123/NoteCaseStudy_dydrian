package com.dydrian.mob22.ui.manageNote.editNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dydrian.mob22.R
import com.dydrian.mob22.core.showToast
import com.dydrian.mob22.databinding.FragmentManageNoteBinding
import com.dydrian.mob22.ui.manageNote.ManageNoteFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditNoteFragment : ManageNoteFragment() {
    private val viewModel: EditNoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.manageNoteTitle.text = getString(R.string.edit_note)
        binding.btnManageAddOrEdit.text = getString(R.string.btnEdit)
        loadNoteFromFB()
    }

    override fun onSubmit(title: String, description: String, color: Int) {
        viewModel.handleIntent(EditNoteIntent.Edit(viewModel.noteId, title, description, color))
    }

    override fun setSelectedColor(color: Int) {
        viewModel.setSelectedColor(color)
    }

    override fun getSelectedColor(): Int {
        return viewModel.selectedColor.value
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is EditNoteState.Success -> {
                        showToast(requireContext(), getString(R.string.success_update))
                        findNavController().popBackStack()
                    }

                    is EditNoteState.Error -> showToast(
                        requireContext(),
                        getString(R.string.error)
                    )

                    else -> {}
                }
            }
        }
    }

    private fun loadNoteFromFB() {
        lifecycleScope.launch {
            viewModel.note.collect { note ->
                binding.etTitle.setText(note?.title)
                binding.etDesc.setText(note?.desc)
                note?.color?.let { color ->
                    preselectColor(color)
                }
            }
        }
    }
}