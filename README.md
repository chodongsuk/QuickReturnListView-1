QuickReturnListView
===================

<img src="https://raw.githubusercontent.com/emmasuzuki/QuickReturnListView/master/demo.gif" width="200">

QuickReturn became one of the favorite UX approaches for scrollable contents to show some menus on demand and hide it when it is not necessary.

Original ideas are taken from<br/>
<a href="https://plus.google.com/+RomanNurik/posts/1Sb549FvpJt">QuickReturnScrollView by Roman Nurik</a><br/>
<a href="https://github.com/LarsWerkman/QuickReturnListView">QuickReturnListView by LarsWerkman</a><br/>

##Disclaimer
LarsWerkman's implementation computes scroll position from list row heights so Quick return view nicely follows the ListView scrolling. 
I took a different approach as I get scroll position from touch event so it might not look as good as the one which follows the ListView scrolling.  However, my approach does not loop through list rows to get scroll position so it may or may not work consistently when your list grows. 

##QuickReturnListView
I made 2 implementations for API level 9+ and API level 14+, depending on your target requirement, please check the QuickReturnListView9 or QuickReturnListView14, accordingly.  
It works with orientation change to keep quick return view position.

Tested on 4.1.2, 4.4.4.  
(Sorry, I have no available older devices and GenyMotion did not work.  It could be GenyMotions bug or my code does not work with older devices.  Please let me know if you have older devices and did not work.)

##How to use
QuickReturnListView requires exactly 1 Quick return view and 1 AbsListView as its children.
Quick return view can be any types of view.

sample.xml
```
<com.emmasuzuki.quickreturnlistview.view.QuickReturnListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/quick_return_listview"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/quick_return_bg"
        android:paddingTop="18dp"
        android:paddingBottom="18dp"
        android:gravity="center"
        android:text="@string/quick_return"/>

    <ListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</com.emmasuzuki.quickreturnlistview.view.QuickReturnListView>
```

MyActivity.java
```
public class MyActivity extends Activity {

    private QuickReturnListView mQuickReturnListView;

    @Override
    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.quick_return_list);

        mQuickReturnListView = (QuickReturnListView) findViewById(R.id.quick_return_listview);

        mQuickReturnListView.setAdapter(new YourAdapter());
    }
}
```

##QuickReturnListView API
`getAbsListView()`, `getQuickReturnView()`: You can access to Quick return view and AbsListView if you need any run time operation on those views.

`setAdapter(ListAdapter adapter)`: Set list adapter for AbsListView

`setQuickReturnEnabled(boolean quickReturnEnabled)`: true to enable, false to disable in real time (You can enable/disable on specific time)

`setFlingEnabled(boolean flingEnabled)`: True to allow fling action (You can enable/disable on specific time)

`setSettleAnimationDuration(int duration)`: Set duration for settling animation. Animation is for quick return view to make it slide up/down to settle to on-screen/off-screen position when it detects UP touch event.

##Questions?
Please contact me at emma11suzuki@gmail.com
