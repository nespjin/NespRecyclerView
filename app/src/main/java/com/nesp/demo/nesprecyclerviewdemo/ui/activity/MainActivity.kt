package com.nesp.demo.nesprecyclerviewdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import com.nesp.demo.nesprecyclerviewdemo.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-1 下午4:45
 * @project NespRecyclerViewDemo
 **/
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        isMainActivity = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun initView() {
        activity_main_btn_demo_java.setOnClickListener {
            startActivity(Intent(context, DemoForJavaActivity::class.java))
        }
    }

}
