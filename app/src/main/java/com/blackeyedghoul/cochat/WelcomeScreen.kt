package com.blackeyedghoul.cochat

import android.content.res.Configuration
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.blackeyedghoul.cochat.adapters.ViewPagerAdapter
import me.relex.circleindicator.CircleIndicator3

class WelcomeScreen : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: CircleIndicator3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        init()

        val images = listOf(R.drawable.welcome_pic_1, R.drawable.welcome_pic_2, R.drawable.welcome_pic_3)
        val adapter = ViewPagerAdapter(images)
        viewPager2.adapter = adapter

        indicator.setViewPager(viewPager2)

        // Show and hide keyboard
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener{
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)

            val height =window.decorView.height
            if(height - r.bottom>height*0.1399){
                viewPager2.alpha = 0.0f // Visible
                indicator.alpha = 0.0f
            }else{
                viewPager2.alpha = 1.0f // Invisible
                indicator.alpha = 1.0f
            }
        }
    }

    private fun init() {
        viewPager2 = findViewById(R.id.wl_view_pager)
        indicator = findViewById(R.id.indicator)
    }
}