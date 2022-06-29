package com.ksnk.test


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class MainActivity : AppCompatActivity() {
    private var mViewPagerAdapter: ViewPagerAdapter? = null
    private var viewPager: ViewPager2? = null
    private var addImageButton: ImageButton? = null
    private var delImageButton: ImageButton? = null
    private var count = 1
    private var textViewNumber: TextView? = null
    private var filteredAll: RealmResults<FragmentEntity>? = null
    private var realm: Realm? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initRealmDb()
        initViewPagerAdapter()
        addFragmentsFromDb()
        //if activity closed
        checkActionAndSetCurrentItem(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //if activity active
        checkActionAndSetCurrentItem(intent)
    }

    private fun checkActionAndSetCurrentItem(intent: Intent?) {
        val action = intent?.action
        if (action?.length!! < 3) {
            viewPager?.setCurrentItem(action.toInt() - 1, false)
        }
    }

    private fun init() {
        textViewNumber = findViewById(R.id.tv_number_page)
        viewPager = findViewById(R.id.viewPager2)
        addImageButton = findViewById(R.id.buttonAdd)
        delImageButton = findViewById(R.id.buttonDel)
        viewPager?.registerOnPageChangeCallback(viewPagerOnPageChangeCallback)
        addImageButton?.setOnClickListener { addButtonClick() }
        delImageButton?.setOnClickListener { delButtonClick() }
    }

    private fun initRealmDb() {
        val config = RealmConfiguration.Builder(schema = setOf(FragmentEntity::class))
            .build()
        realm = Realm.open(config)
        val getAllFragmentEntity: RealmQuery<FragmentEntity> = realm!!.query<FragmentEntity>()
        filteredAll = getAllFragmentEntity.find()
        count = filteredAll?.size!!
    }

    private fun addFragmentsFromDb() {
        val positionCount: Int = 0
        if (filteredAll?.isEmpty() == true) {
            addButtonClick()
        } else {
            for (fragmentTest in filteredAll!!) {
                val positionFragment = positionCount + 1
                mViewPagerAdapter?.addFragment(fragmentTest.testFragment, positionFragment)
            }
        }
    }

    private fun initViewPagerAdapter() {
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager?.adapter = mViewPagerAdapter
    }

    private var viewPagerOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val curPosition = position + 1
            textViewNumber?.text = curPosition.toString()
            if (position == 0) {
                delImageButton?.visibility = View.INVISIBLE
            } else {
                delImageButton?.visibility = View.VISIBLE
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val curPosition = position + 1
            textViewNumber?.text = curPosition.toString()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }

    }

    private fun delButtonClick() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(count)
        mViewPagerAdapter?.removeFragment(count)
        viewPager?.setCurrentItem(count - 1, false)
        count -= 1
        CoroutineScope(Dispatchers.IO).async {
            realm?.writeBlocking {
                val writeTransactionTasks = query<FragmentEntity>().find()
                delete(writeTransactionTasks.last())
            }
        }
    }

    private fun addButtonClick() {
        count += 1
        val fr = TestFragment()
        mViewPagerAdapter?.addFragment(fr, count)
        viewPager?.setCurrentItem(count, false)
        val fragmentEntity = FragmentEntity().apply {
            id = count
            testFragment = fr
        }

        CoroutineScope(Dispatchers.IO).async {
            realm?.write {
                copyToRealm(fragmentEntity)
            }
        }
    }

}