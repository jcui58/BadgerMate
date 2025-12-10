package com.cs407.badgermate.ui.dining

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.badgermate.databinding.FragmentDiningBinding
import com.cs407.badgermate.ui.profile.ProfileViewModel

class DiningFragment : Fragment() {

    private var _binding: FragmentDiningBinding? = null
    private val binding get() = _binding!!

    private lateinit var diningViewModel: DiningViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        diningViewModel = ViewModelProvider(this).get(DiningViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        
        _binding = FragmentDiningBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.diningCompose.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MaterialTheme {
                    DiningScreen(
                        viewModel = diningViewModel,
                        profileViewModel = profileViewModel
                    )
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
