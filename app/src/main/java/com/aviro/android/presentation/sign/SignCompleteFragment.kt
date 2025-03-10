package com.aviro.android.presentation.sign

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aviro.android.common.AmplitudeUtils
import com.aviro.android.databinding.FragmentSignCompleteBinding
import com.aviro.android.presentation.BaseFragment
import com.aviro.android.presentation.home.Home

class SignCompleteFragment : BaseFragment() {

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {} // 뒤로가기 막기

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}