package com.dydrian.mob22.ui.manageNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dydrian.mob22.R
import com.dydrian.mob22.core.showToast
import com.dydrian.mob22.databinding.FragmentManageNoteBinding

abstract class ManageNoteFragment : Fragment() {
    protected lateinit var binding: FragmentManageNoteBinding

    // Holds current selected color box and its tick view
    private var selectedColorBox: View? = null
    private var selectedTickView: ImageView? = null

    abstract fun onSubmit(title: String, description: String, color: Int)
    abstract fun observeViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorSelection()
        observeViewModel()
        setupSubmitButton()

        binding.btnBackToHome.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Handles submission of edit/add + validations
    private fun setupSubmitButton() {
        binding.btnManageAddOrEdit.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDesc.text.toString()
            val color = getSelectedColor()

            if (title.isEmpty()) {
                showToast(requireContext(), getString(R.string.empty_title))
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                showToast(requireContext(), getString(R.string.empty_desc))
                return@setOnClickListener
            }

            onSubmit(title, description, color)
        }
    }

    // Initializes color box selection logic
    private fun setupColorSelection() {
        // Each Triple represents (color box view, tick view, color resource)
        val colorMappings = listOf(
            Triple(binding.colorBox1, binding.colorBox1Selected, R.color.lightGreen),
            Triple(binding.colorBox2, binding.colorBox2Selected, R.color.cyan),
            Triple(binding.colorBox3, binding.colorBox3Selected, R.color.lightRed),
            Triple(binding.colorBox4, binding.colorBox4Selected, R.color.lightPurple),
            Triple(binding.colorBox5, binding.colorBox5Selected, R.color.lightYellow)
        )

        // Set a click listener on each color box to update selection
        colorMappings.forEach { (colorBox, tickView, colorRes) ->
            colorBox.setOnClickListener {
                resetSelectionState()
                tickView.visibility = View.VISIBLE
                selectedColorBox = colorBox
                selectedTickView = tickView
                setSelectedColor(ContextCompat.getColor(requireContext(), colorRes))
            }
        }
    }

    // Suppose to be overridden to store selected color in ViewModel
    protected open fun setSelectedColor(color: Int) {}

    // Suppose to be overridden to retrieve selected color from ViewModel
    protected open fun getSelectedColor(): Int = 0

    private fun resetSelectionState() {
        selectedTickView?.visibility = View.GONE
        selectedTickView = null
        selectedColorBox = null
    }

    // Programmatically selects the color box that matches the given color
    protected fun preselectColor(color: Int) {
        val colorMappings = listOf(
            Triple(binding.colorBox1, binding.colorBox1Selected, R.color.lightGreen),
            Triple(binding.colorBox2, binding.colorBox2Selected, R.color.cyan),
            Triple(binding.colorBox3, binding.colorBox3Selected, R.color.lightRed),
            Triple(binding.colorBox4, binding.colorBox4Selected, R.color.lightPurple),
            Triple(binding.colorBox5, binding.colorBox5Selected, R.color.lightYellow)
        )

        // Find the matching color and activate its tick
        val matched = colorMappings.firstOrNull {
            ContextCompat.getColor(requireContext(), it.third) == color
        }

        matched?.let { (colorBox, tickView, colorRes) ->
            resetSelectionState()
            tickView.visibility = View.VISIBLE
            selectedColorBox = colorBox
            selectedTickView = tickView
            setSelectedColor(ContextCompat.getColor(requireContext(), colorRes))
        }
    }
}