package com.cs407.badgermate.ui.bus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.cs407.badgermate.databinding.FragmentBusBinding

class BusFragment : Fragment() {

    private var _binding: FragmentBusBinding? = null
    private val binding get() = _binding!!

    private lateinit var busViewModel: BusViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        busViewModel = ViewModelProvider(this).get(BusViewModel::class.java)
        
        _binding = FragmentBusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.busCompose.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MaterialTheme {
                    BusScreen(viewModel = busViewModel)
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
