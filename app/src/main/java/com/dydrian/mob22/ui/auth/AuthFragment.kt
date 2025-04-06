package com.dydrian.mob22.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            viewModel.handleIntent(AuthIntent.LoginWithGoogle, requireContext())
        }
    }


    override fun setupViewModelObserver(view: View) {
        super.setupViewModelObserver(view)
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AuthState.Success -> {
                        findNavController().navigate(AuthFragmentDirections.actionHomeFragment())
                    }

                    is AuthState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    AuthState.Idle -> {}
                }
            }
        }
    }
}