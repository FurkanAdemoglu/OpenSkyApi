package com.example.openskyapicase.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.openskyapicase.R
import com.example.openskyapicase.base.BaseFragment
import com.example.openskyapicase.databinding.FragmentSplashBinding
import com.example.openskyapicase.util.extension.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //İnternet olup olmaması kontrolüne göre akış ilerliyor
        if (requireContext().isInternetAvailable()) {
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        } else {
            showAppDialog(
                title = getString(R.string.connection_error),
                message = getString(R.string.internet_connection_not_found),
                positiveText = getString(R.string.try_again),
                negativeText = getString(R.string.exit),
                cancelable = false,
                onPositive = { findNavController().navigate(R.id.splashFragment) },
                onNegative = { requireActivity().finish() }
            )
        }
    }
}