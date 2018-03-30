package com.example.a9.myapplication2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<String> listTitle = new ArrayList<String>();
    private List<Fragment> listFragment = new ArrayList<Fragment>();
    private MyAdapter adapter;
    private TabLayoutView tabIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tabIndicatorView = (TabLayoutView) findViewById(R.id.tabIndicatorView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0)
                listTitle.add("标12888888883题" + i);
            else listTitle.add("标题" + i);
            TestViewFragment testFragment = new TestViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("str", (listTitle.get(i)));
            testFragment.setArguments(bundle);
            listFragment.add(testFragment);
        }

        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabIndicatorView.setupWithViewPager(viewPager);
        //切换到第5个title
        tabIndicatorView.setCurrent(5);


    }

    /**
     * 适配器
     */
    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitle.get(position);
        }
    }

}
