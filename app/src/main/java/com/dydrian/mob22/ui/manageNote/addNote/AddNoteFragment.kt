package com.dydrian.mob22.ui.manageNote.addNote

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
class AddNoteFragment : ManageNoteFragment() {
    private val viewModel: AddNoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.manageNoteTitle.text = getString(R.string.add_note)
        binding.btnManageAddOrEdit.text = getString(R.string.add_btn)
    }

    override fun onSubmit(title: String, description: String, color: Int) {
        viewModel.handleIntent(AddNoteIntent.Add(title, description, color))
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
                    is AddNoteState.Success -> {
                        showToast(requireContext(), getString(R.string.success_add))
                        findNavController().popBackStack()
                    }

                    is AddNoteState.Error -> showToast(
                        requireContext(),
                        getString(R.string.error)
                    )

                    else -> {}
                }
            }
        }
    }
}