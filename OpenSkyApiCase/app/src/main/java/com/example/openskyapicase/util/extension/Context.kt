package com.example.openskyapicase.util.extension

import android.content.Context
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

//Map marker da uçak drawable ını bitmap e çevirmek  için kullanıldı
fun Context.vectorToBitmap(@DrawableRes id: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(this, id) ?: return BitmapDescriptorFactory.defaultMarker()
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
