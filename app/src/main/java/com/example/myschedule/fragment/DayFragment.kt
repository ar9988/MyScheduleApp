package com.example.myschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myschedule.R
import com.example.myschedule.customView.TimePiece
import com.example.myschedule.databinding.DayLayoutBinding

class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DayLayoutBinding.inflate(inflater)
        val testView : TimePiece? = context?.let { TimePiece(it,attrs = null,-30F,60F,R.color.rainbow7,binding) }
        val frame: FrameLayout = binding.watchCenter
        frame.addView(testView)
        return binding.root
    }
}