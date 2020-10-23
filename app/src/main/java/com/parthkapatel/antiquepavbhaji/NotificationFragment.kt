package com.parthkapatel.antiquepavbhaji

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class NotificationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Hide Cart Image And CartBadge
        var cartimage = activity?.findViewById<ImageView>(R.id.cartImage)
        var cartimagebadge = activity?.findViewById<TextView>(R.id.counterbadgeCart)
        if (cartimage != null) {
            cartimage.visibility = View.INVISIBLE
            if (cartimagebadge != null) {
                cartimagebadge.visibility = View.INVISIBLE
            }
        }
    }

}