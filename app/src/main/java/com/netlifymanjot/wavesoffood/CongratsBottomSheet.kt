package com.netlifymanjot.wavesoffood

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netlifymanjot.wavesoffood.databinding.ActivityMainBinding
import com.netlifymanjot.wavesoffood.databinding.FragmentCongratsBottomSheetBinding

class CongratsBottomSheet : BottomSheetDialogFragment() {
private lateinit var binding : FragmentCongratsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCongratsBottomSheetBinding.inflate(layoutInflater, container, false)
        binding.goHome.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        return (binding.root)
    }

    companion object {
    }
}