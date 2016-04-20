# Project18
Android mobile application for college course.

For the menu drawer we can use this:
http://developer.android.com/training/implementing-navigation/nav-drawer.html

It also shows how to switch fragments.

<FrameLayout
        android:id="@+id/content_frame"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
Fragment fragment = new Fragment2();
// Insert the fragment by replacing any existing fragment
FragmentManager fragmentManager = getFragmentManager();
fragmentManager.beginTransaction()
               .replace(R.id.content_frame, fragment)
               .commit();
