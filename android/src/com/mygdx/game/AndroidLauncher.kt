package com.mygdx.game


import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication

class AndroidLauncher : FragmentActivity(), AndroidFragmentApplication.Callbacks {
    var curId=0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout)

        // Create libgdx fragment
        val libgdxFragment = GameFragment(curId.toString())

        // Put it inside the framelayout (which is defined in the layout.xml file).
        supportFragmentManager.beginTransaction().add(R.id.content_framelayout, libgdxFragment).commit()

        val tv_new_seed=findViewById(R.id.tv_new_seed)
        tv_new_seed.setOnClickListener {
            var newId=(++curId).toString()
            supportFragmentManager.beginTransaction().replace(R.id.content_framelayout, GameFragment(newId)).commit()
        }
    }

    override fun exit() {

    }


}
