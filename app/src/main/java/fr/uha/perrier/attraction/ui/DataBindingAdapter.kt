package fr.uha.perrier.attraction.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import fr.uha.perrier.android.helper.Picture
import fr.uha.perrier.attraction.R
import fr.uha.perrier.attraction.model.Gender.Gender
import java.text.SimpleDateFormat
import java.util.Date

class DataBindingAdapter {

    companion object {
        @BindingAdapter("android:background")
        @JvmStatic
        fun setBackground (v : View, gender : Gender?) {
            var colorId : Int = if (gender == null) R.color.error
            else {
                when (gender) {
                    Gender.NO -> R.color.no
                    Gender.FEMALE -> R.color.girl
                    Gender.MALE -> R.color.boy
                }
            }
            val color = v.resources.getColor(colorId, null)
            v.setBackgroundColor(color)
        }

        @BindingAdapter("picture")
        @JvmStatic
        fun setPicture(view: ImageView, picture: String?) {
            val w = view.getWidth()
            val h = view.getHeight()
            var done : Boolean = false;
            val bm: Bitmap? = Picture.getBitmapFromUri(view.getContext(), picture, w, h)
            if (!done && bm != null) {
                view.setImageBitmap(bm)
                done = true;
            }
            if (!done) {
                view.setImageResource(R.drawable.ic_baseline_person_24)
            }
        }

        var sdf : SimpleDateFormat? = null

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText (v : TextView, date : Date?) {
            var text : String?
            if (date == null) {
                text = v.resources.getString(R.string.no_date)
            } else {
                if (sdf == null) sdf = SimpleDateFormat("dd MMMM yyyy")
                text = sdf!!.format(date)
            }
            v.text = text
        }

        @BindingAdapter("android:text")
        @JvmStatic
        fun setText (v : TextView, value : Int) {
            var todo = true
            if (! v.text.toString().isEmpty()) {
                try {
                    val inView = v.text.toString().toInt()
                    if (inView == value) todo = false
                } catch (e : java.lang.NumberFormatException) {
                }
            }
            if (todo) {
                v.text = value.toString()
            }
        }

        @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
        @JvmStatic
        fun getText (v : TextView) : Int {
            if (v.text.toString().isEmpty()) return 0
            try {
                return v.text.toString().toInt()
            } catch (e : java.lang.NumberFormatException) {
                return 0
            }
        }

    }
}