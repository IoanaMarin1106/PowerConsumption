package com.example.powerconsumptionapp.cpuinfo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.example.powerconsumptionapp.R
import org.w3c.dom.Text

class InfoDialogFragment(dialogTitle: String, dialogText: String) : DialogFragment() {
    private val _dialogText: String = dialogText
    private val _dialogTitle: String = dialogTitle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_info_dialog, container, false)

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }

        rootView.findViewById<TextView>(R.id.dialogInformation).text = _dialogText
        rootView.findViewById<TextView>(R.id.dialogTitle).text = _dialogTitle
        rootView.findViewById<AppCompatButton>(R.id.okButton).setOnClickListener {
            dismiss()
        }

        return rootView
    }

}