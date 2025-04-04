package com.dydrian.mob22.ui.manageNote.editNote

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
class EditNoteFragment : ManageNoteFragment() {
    private val viewModel: EditNoteViewModel by viewModels()

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

        binding.manageNoteTitle.text = getString(R.string.edit_note)
        binding.btnManageAddOrEdit.text = getString(R.string.btnEdit)

        setupColorSelection()
        observeViewModel()
        loadNoteFromFB()
        setupEdit()
    }

    private fun loadNoteFromFB() {
        lifecycleScope.launch {
            viewModel.note.collect { note ->
                binding.etTitle.setText(note?.title)
                binding.etDesc.setText(note?.desc)

                note?.color?.let { color ->
                    getColorView(color)?.let { (colorBox, tickIcon) ->
                        colorSelectionHandler(colorBox, tickIcon, color)
                    }
                }
            }
        }
    }

    private fun setupEdit() {
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

            viewModel.handleIntent(EditNoteIntent.Edit(viewModel.noteId, title, description, color))
        }
    }

    private fun setupColorSelection() {
        // Create a map of color boxes and their tick icons
        val colorBoxes = mapOf(
            binding.colorBox1 to binding.colorBox1Selected,
            binding.colorBox2 to binding.colorBox2Selected,
            binding.colorBox3 to binding.colorBox3Selected,
            binding.colorBox4 to binding.colorBox4Selected,
            binding.colorBox5 to binding.colorBox5Selected
        )

        // Add click listeners to each color box
        colorBoxes.forEach { (colorBox, tickIcon) ->
            colorBox.setOnClickListener {
                colorSelectionHandler(
                    colorBox,
                    tickIcon,
                    getColorFromView(colorBox)
                )
            }
        }
    }

    // Handle color selection
    private fun colorSelectionHandler(view: View, tickIcon: ImageView?, colorId: Int) {
        resetSelectionState()

        tickIcon?.visibility = View.VISIBLE
        viewModel.setSelectedColor(colorId)

        // Remember which color is selected
        selectedColorBox = view
        selectedTickView = tickIcon
    }

    private fun resetSelectionState() {
        selectedTickView?.visibility = View.GONE
        selectedTickView = null
        selectedColorBox = null
    }

    // Get the view for a specific color
    private fun getColorView(color: Int): Pair<View, ImageView?>? {
        return when (color) {
            ContextCompat.getColor(requireContext(), R.color.green) ->
                Pair(binding.colorBox1, binding.colorBox1Selected)

            ContextCompat.getColor(requireContext(), R.color.cyan) ->
                Pair(binding.colorBox2, binding.colorBox2Selected)

            ContextCompat.getColor(requireContext(), R.color.red) ->
                Pair(binding.colorBox3, binding.colorBox3Selected)

            ContextCompat.getColor(requireContext(), R.color.magenta) ->
                Pair(binding.colorBox4, binding.colorBox4Selected)

            ContextCompat.getColor(requireContext(), R.color.lightOrange) ->
                Pair(binding.colorBox5, binding.colorBox5Selected)

            else -> null
        }
    }

    // Get the color from a view
    private fun getColorFromView(view: View): Int {
        return when (view) {
            binding.colorBox1 -> ContextCompat.getColor(requireContext(), R.color.green)
            binding.colorBox2 -> ContextCompat.getColor(requireContext(), R.color.cyan)
            binding.colorBox3 -> ContextCompat.getColor(requireContext(), R.color.red)
            binding.colorBox4 -> ContextCompat.getColor(requireContext(), R.color.magenta)
            binding.colorBox5 -> ContextCompat.getColor(requireContext(), R.color.lightOrange)
            else -> ContextCompat.getColor(requireContext(), 0)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is EditNoteState.Success -> {
                        showToast(requireContext(), getString(R.string.success_update))
                        findNavController().popBackStack()
                    }

                    is EditNoteState.Error -> {
                        showToast(requireContext(), getString(R.string.error))
                    }

                    else -> {}
                }
            }
        }
    }
}