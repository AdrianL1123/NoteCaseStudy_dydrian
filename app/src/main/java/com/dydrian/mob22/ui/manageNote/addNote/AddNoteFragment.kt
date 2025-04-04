package com.dydrian.mob22.ui.manageNote.addNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    private var selectedColorBox: View? = null
    private var selectedTickView: ImageView? = null

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

        setupColorSelection()
        observeViewModel()
        setupAdd()
    }

    private fun setupAdd() {
        binding.btnManageAddOrEdit.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDesc.text.toString()
            val color = viewModel.selectedColor.value

            if (title.isEmpty()) {
                showToast(requireContext(), getString(R.string.empty_title))
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                showToast(requireContext(), getString(R.string.empty_desc))
                return@setOnClickListener
            }

            viewModel.handleIntent(AddNoteIntent.Add(title, description, color))
        }
    }

    private fun setupColorSelection() {
        // Create a list of color boxes with their tick icons and color resource IDs
        val colorMappings = listOf(
            Pair(binding.colorBox1, binding.colorBox1Selected to R.color.green),
            Pair(binding.colorBox2, binding.colorBox2Selected to R.color.cyan),
            Pair(binding.colorBox3, binding.colorBox3Selected to R.color.red),
            Pair(binding.colorBox4, binding.colorBox4Selected to R.color.purple),
            Pair(binding.colorBox5, binding.colorBox5Selected to R.color.yellow)
        )

        // Add click listeners to each color box
        colorMappings.forEach { (colorBox, tickPair) ->
            val (tickView, colorRes) = tickPair
            colorBox.setOnClickListener {
                colorSelectionHandler(colorBox, tickView, colorRes)
            }
        }
    }

    private fun colorSelectionHandler(selectedView: View, tickView: ImageView, colorRes: Int) {
        resetSelectionState()
        tickView.visibility = View.VISIBLE
        viewModel.setSelectedColor(ContextCompat.getColor(requireContext(), colorRes))
        selectedColorBox = selectedView
        selectedTickView = tickView
    }

    private fun resetSelectionState() {
        selectedTickView?.visibility = View.GONE
        selectedTickView = null
        selectedColorBox = null
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AddNoteState.Success -> {
                        showToast(requireContext(), getString(R.string.success_add))
                        findNavController().popBackStack()
                    }

                    is AddNoteState.Error -> {
                        showToast(requireContext(), getString(R.string.error))
                    }

                    else -> {}
                }
            }
        }
    }
}