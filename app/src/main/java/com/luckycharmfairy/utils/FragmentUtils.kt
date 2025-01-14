package com.luckycharmfairy.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.luckycharmfairy.luckycharmfairy.R

object FragmentUtils {

    fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int = R.id.main_frame,
        fragmentTag: String? = null,
        addToBackStack: Boolean = true
    ) {
        fragmentManager.beginTransaction().apply {
            add(containerId, fragment, fragmentTag)
            if (addToBackStack) addToBackStack(fragmentTag)
            commit()
        }
    }

    fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        containerId: Int = R.id.main_frame,
        fragmentTag: String? = null,
        addToBackStack: Boolean = true
    ) {
        fragmentManager.beginTransaction().apply {
            replace(containerId, fragment, fragmentTag)
            if (addToBackStack) addToBackStack(fragmentTag)
            commit()
        }
    }

    fun hideAndShowFragment(
        fragmentManager: FragmentManager,
        fragmentToHide: Fragment,
        fragmentToShow: Fragment,
        fragmentTag: String? = null,
        addToBackStack: Boolean = true
    ) {
        fragmentManager.beginTransaction().apply {
            hide(fragmentToHide)
            if (!fragmentToShow.isAdded) {
                add(fragmentToHide.id, fragmentToShow, fragmentTag)
            } else {
                show(fragmentToShow)
            }
            if (addToBackStack) addToBackStack(fragmentTag)
            commit()
        }
    }

    fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            remove(fragment)
            commit()
        }
    }


}
