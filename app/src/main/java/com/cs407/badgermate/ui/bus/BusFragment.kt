package com.cs407.badgermate.ui.bus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cs407.badgermate.databinding.FragmentBusBinding

class BusFragment : Fragment() {

    companion object {
        private const val TAG = "BusFragment"
        // Fallback API key - will be used if manifest reading fails
        private const val FALLBACK_API_KEY = "AIzaSyCfQmDUfAy6CiEJJ2M4do-xg90Gstu5Ym4"
    }
    // ViewBinding reference for this fragment's layout
    private var _binding: FragmentBusBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BusViewModel by viewModels()

    // Location permission request
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                Log.d(TAG, "Precise location access granted")
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                Log.d(TAG, "Approximate location access granted")
            }
            else -> {
                Log.w(TAG, "No location access granted")
            }
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentBusBinding.inflate(inflater, container, false)

        var apiKey = ""
        try {
            apiKey = requireContext()
                .packageManager
                .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
                .metaData
                ?.getString("com.google.android.geo.API_KEY") ?: ""

            Log.d(TAG, "API Key from Manifest: ${if (apiKey.isNotEmpty()) "${apiKey.length} chars" else "NOT FOUND"}")
        } catch (e: Exception) {
            Log.e(TAG, "Error reading API Key from Manifest", e)
        }

        if (apiKey.isEmpty() || apiKey == "null") {
            Log.w(TAG, "Using fallback API key")
            apiKey = FALLBACK_API_KEY
        }

        Log.d(TAG, "Final API Key loaded: ${apiKey.length} chars")
        Log.d(TAG, "API Key starts with: ${apiKey.take(10)}...")

        // Pass API key to ViewModel
        viewModel.setApiKey(apiKey)

        binding.busCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                BusScreen(
                    origin = viewModel.origin,
                    destination = viewModel.destination,
                    pathPoints = viewModel.pathPoints,
                    onOriginChange = { viewModel.origin = it },
                    onDestinationChange = { viewModel.destination = it },
                    onGetRoute = { viewModel.loadRoute() },
                )
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "BusFragment view created")

        // Request location permissions at runtime
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}