package com.cs407.badgermate.ui.dining

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cs407.badgermate.databinding.FragmentDiningBinding

class DiningFragment : Fragment() {

    private var _binding: FragmentDiningBinding? = null
    private val binding get() = _binding!!

    private val diningViewModel: DiningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.diningCompose.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MaterialTheme {
                    DiningScreen(viewModel = diningViewModel)
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
