package com.dydrian.mob22.ui.manageNote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dydrian.mob22.databinding.FragmentManageNoteBinding

abstract class ManageNoteFragment : Fragment() {
    protected lateinit var binding: FragmentManageNoteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageNoteBinding.inflate(inflater, container, false)
        return binding.root
    }
}