package com.example.testkolsa.presentation.common

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.testkolsa.R
import com.google.android.material.navigation.NavigationBarView
import jakarta.inject.Inject

class RouterImpl @Inject constructor(@IdRes private val controller: Int) : Router {
    private var navController: NavController? = null

    override fun init(
        fragment: Fragment?,
//        fragmentManager: FragmentManager?,
//        tabElementView: NavigationBarView?,
    ) {
        navController = fragment?.findNavController()
//        val navHostFragment = fragmentManager?.findFragmentById(controller) as? NavHostFragment
//        navController = navHostFragment?.navController ?: fragment?.findNavController()
//
//        navController?.let { controller ->
//            tabElementView?.let { NavigationUI.setupWithNavController(it, controller) }
//        }
    }

    override fun clear() {
        navController = null
    }

    override fun navigateTo(
        screen: Screens,
        bundle: Bundle?,
    ) {
        when (screen) {
            Screens.Training -> navController?.navigate(Screens.Training)
            is Screens.TrainingDetail -> {
                navController?.navigate(
                    R.id.action_list_training_to_current_fragment_training,
                    bundle
                )
            }
        }

    }

    override fun back() {
        navController?.popBackStack()
    }
}