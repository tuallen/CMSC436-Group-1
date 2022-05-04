package com.example.myapplication

import android.R
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.First.FirstFragment
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var nvDrawer: NavigationView? = null

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private var drawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.myapplication.R.layout.activity_main)

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById<View>(com.example.myapplication.R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        // Find our drawer view
        mDrawer = findViewById<DrawerLayout>(com.example.myapplication.R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle?.isDrawerIndicatorEnabled = true;
        drawerToggle?.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer!!.addDrawerListener(drawerToggle!!);

        // Setup drawer view
        nvDrawer?.let { setupDrawerContent(it) };



    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*>
        fragmentClass = when (menuItem.itemId) {
            com.example.myapplication.R.id.nav_first_fragment -> FirstFragment::class.java
            com.example.myapplication.R.id.nav_second_fragment -> SecondFragment::class.java
            com.example.myapplication.R.id.nav_third_fragment -> ThirdFragment::class.java
            else -> FirstFragment::class.java
        }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Insert the fragment by replacing any existing fragment
        val fragmentManager: FragmentManager = supportFragmentManager
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(com.example.myapplication.R.id.flContent, fragment).commit()
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.isChecked = true
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        mDrawer!!.closeDrawers()
    }


    private fun setupDrawerToggle(): ActionBarDrawerToggle? {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return ActionBarDrawerToggle(
            this,
            mDrawer,
            toolbar,
            com.example.myapplication.R.string.drawer_open,
            com.example.myapplication.R.string.drawer_close
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        when (item.getItemId()) {
            R.id.home -> {
                mDrawer!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}