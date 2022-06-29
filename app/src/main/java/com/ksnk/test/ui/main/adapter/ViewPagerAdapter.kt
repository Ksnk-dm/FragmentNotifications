package com.ksnk.test.ui.main.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ksnk.test.utils.Contains
import com.ksnk.test.ui.testFragment.TestFragment
import kotlin.collections.ArrayList


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val arrayListTestFragments: ArrayList<TestFragment> = ArrayList<TestFragment>()


    override fun createFragment(position: Int): Fragment {
        val testFragment = arrayListTestFragments[position]
        val bundle = Bundle()
        bundle.putInt(Contains().argumentId, position + 1)
        testFragment.arguments = bundle
        return testFragment
    }

    override fun getItemCount(): Int {
        return arrayListTestFragments.size
    }

    fun addFragment(testFragment: TestFragment, position: Int) {
        val bundle = Bundle()
        bundle.putInt(Contains().argumentId, position)
        arrayListTestFragments.add(testFragment)
        notifyDataSetChanged()
    }

    fun removeFragment(position: Int) {
        arrayListTestFragments.removeLast()
        notifyItemRangeChanged(position, arrayListTestFragments.size)
        notifyDataSetChanged()
    }

}