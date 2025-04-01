package com.dydrian.mob22.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dydrian.mob22.data.model.Note
import com.dydrian.mob22.databinding.FragmentHomeBinding
import com.dydrian.mob22.ui.adapter.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        observerState()

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.setNotes(notes = state.notes)
            }
        }
    }

    private fun observerState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.setNotes(state.notes)
                binding.tvEmpty.isVisible = state.notes.isEmpty()
            }
        }
    }

    private fun setupAdapter() {
        adapter = NoteAdapter(emptyList())
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager =
            StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
            )
        adapter.listener = object : NoteAdapter.Listener {
            override fun onClickItem(item: Note) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                        item.id!!
                    )
                )
            }

        }
    }
}