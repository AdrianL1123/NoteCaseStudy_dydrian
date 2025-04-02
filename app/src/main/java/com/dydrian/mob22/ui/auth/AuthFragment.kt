package com.dydrian.mob22.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dydrian.mob22.databinding.FragmentAuthBinding
import com.dydrian.mob22.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : BaseFragment() {
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUiComponents(view: View) {
        super.setupUiComponents(view)
        binding.btnGoogleSignIn.setOnClickListener {
            viewModel.loginWithGoogle(requireContext())
        }
    }


    override fun setupViewModelObserver(view: View) {
        super.setupViewModelObserver(view)
        lifecycleScope.launch {
            viewModel.success.collect {
                val action = AuthFragmentDirections.actionHomeFragment()
                findNavController().navigate(action)
            }
        }
    }
}