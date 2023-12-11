package com.android.aviro.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.aviro.R
import com.android.aviro.databinding.FragmentSignCompleteBinding
import com.android.aviro.presentation.Home.Home

class SignCompleteFragment : Fragment() {

    private val sharedViewModel: SignViewModel by activityViewModels()

    private var _binding: FragmentSignCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignCompleteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.startBtn.setOnClickListener {
            val next_activity = Intent(requireContext(), Home::class.java)
            startActivity(next_activity)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}