package com.dydrian.mob22.ui.addNote

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dydrian.mob22.R
import com.dydrian.mob22.databinding.FragmentAddNoteBinding
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private val viewModel: AddNoteViewModel by viewModels()

    private var selectedColorBox: View? = null
    private val defaultBgColors = mutableMapOf<View, Int>()
    private val defaultBgBorders = mutableMapOf<View, Drawable?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorSelection()
        observerHandler()

        binding.btnAdd.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDesc.text.toString()
            val color = viewModel.selectedColor.value

            viewModel.handleIntent(AddNoteIntent.Add(title, description, color))
        }
    }

    private fun setupColorSelection() {
        binding.btnGreen.setOnClickListener { colorSelectionHandler(it, R.color.green) }
        binding.btnBlue.setOnClickListener { colorSelectionHandler(it, R.color.cyan) }
        binding.btnRed.setOnClickListener { colorSelectionHandler(it, R.color.red) }
        binding.btnPurple.setOnClickListener { colorSelectionHandler(it, R.color.magenta) }
        binding.btnOrange.setOnClickListener { colorSelectionHandler(it, R.color.lightOrange) }
    }

    private fun colorSelectionHandler(view: View, colorId: Int) {
        // Get the color from the resources
        val color = ContextCompat.getColor(requireContext(), colorId)

        resetSelectionState()
        saveDefaultState(view)

        val drawableBorder = GradientDrawable().apply {
            setColor(color)
            setStroke(6, ContextCompat.getColor(requireContext(), R.color.black))
            cornerRadius = 8f
        }
        view.background = drawableBorder // applies the border to the selected color box
        viewModel.setSelectedColor(color)

        // If the view contains a card view, update its background color
        val cardView = view.findViewById<MaterialCardView>(R.id.mcvNote)
        cardView?.setCardBackgroundColor(color)

        // checks which color box is being selected
        selectedColorBox = view
    }

    private fun saveDefaultState(view: View) {

        // saves bg color as default if not saved yet.
        if (!defaultBgColors.contains(view)) {
            // saves bg color selected from view's color as default, transparent if not set.
            defaultBgColors[view] =
                (view.background as? GradientDrawable)?.color?.defaultColor ?: Color.TRANSPARENT
        }
        if (!defaultBgBorders.contains(view)) {
            defaultBgBorders[view] = view.background
        }
    }

    private fun resetSelectionState() {
        // check if the selected color box exist
        selectedColorBox?.let { view ->
            defaultBgColors[view]?.let { defaultColor ->
                // Restore its original background color
                view.setBackgroundColor(defaultColor)
            }
            defaultBgBorders[view]?.let { defaultBorder ->
                // Restore its original background drawable (right now it's only a border)
                view.background = defaultBorder
            }
        }
        // Clear the reference to the previously selected color box
        selectedColorBox = null
    }

    private fun observerHandler() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AddNoteState.Success -> {
                        Toast.makeText(
                            requireContext(), "Note added successfully!", Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }

                    is AddNoteState.Error -> {
                        Toast.makeText(
                            requireContext(), state.msg, Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }
}
