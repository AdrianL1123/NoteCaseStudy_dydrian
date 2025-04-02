package com.dydrian.mob22.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dydrian.mob22.R
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
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.homeToAddNote())
        }

        Glide.with(this)
            .load(viewModel.getProgileUrl())
            .placeholder(R.drawable.note_icon)
            .error(R.drawable.note_icon)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(32)))
            .into(binding.ivProfile);
        // using bringToFront() to ensure that the ImageView appears above other views
        // (like the RecyclerView or FloatingActionButton), so it can receive touch events and be clickable.
        binding.ivProfile.bringToFront()
        binding.ivProfile.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun observerState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.setNotes(state.notes)
                if (state.notes.isEmpty()) {
                    binding.rvNotes.visibility = View.GONE
                    binding.emptyStateContainer.visibility = View.VISIBLE
                } else {
                    binding.rvNotes.visibility = View.VISIBLE
                    binding.emptyStateContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = NoteAdapter(emptyList())
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
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


    /**
     * Logout dialog
     */
    private fun showLogoutDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.profile_alert_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvLogoutMessage = dialog.findViewById<TextView>(R.id.tvLogoutMessage)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)

        val user = viewModel.getUserInfo()
        tvLogoutMessage.text = "Are you sure you want to log out?\n${user?.email ?: ""}"

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(HomeFragmentDirections.actionLoginFragment())
            dialog.dismiss()
        }
        dialog.show()
    }
}