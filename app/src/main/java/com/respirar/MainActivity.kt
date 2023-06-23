package com.respirar

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout);

        val buttonMenu = findViewById<ImageView>(R.id.imageMenu);

        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu
        val menuItem: MenuItem = menu.findItem(R.id.itemRegistro)

        val spannableString = SpannableString(menuItem.title)
        spannableString.setSpan(RelativeSizeSpan(2.5f), 0, spannableString.length, 0)


        spannableString.apply{
            val font = resources.getFont(R.font.gotham_black)
            setSpan(
                CustomTypefaceSpan(font),
                0,
                length,
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        menuItem.title = spannableString

        buttonMenu.setOnClickListener(){
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }

    class CustomTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {

        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeface(ds, typeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeface(paint, typeface)
        }

        private fun applyCustomTypeface(paint: Paint, typeface: Typeface) {
            val oldStyle: Int
            val oldTypeface = paint.typeface
            oldStyle = oldTypeface?.style ?: 0

            val fake = oldStyle and typeface.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = typeface
        }
    }
}