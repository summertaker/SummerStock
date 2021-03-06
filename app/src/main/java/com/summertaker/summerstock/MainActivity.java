package com.summertaker.summerstock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.summertaker.summerstock.common.BaseApplication;
import com.summertaker.summerstock.common.BaseFragment;
import com.summertaker.summerstock.data.Site;
import com.summertaker.summerstock.util.SlidingTabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ItemListFragment.ItemListListener {

    //private Context mContext;
    //private String mTag = this.getClass().getSimpleName();

    private Toolbar mToolbar;

    private View mMenuRefresh;
    private Animation mRotateAnimation;

    private ArrayList<Site> mPagerItems;
    private SectionsPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //mContext = MainActivity.this;

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runFragment("goTop");
            }
        });
        mToolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mMenuRefresh = mToolbar.findViewById(R.id.action_refresh);
                if (mMenuRefresh != null) {
                    mToolbar.removeOnLayoutChangeListener(this);
                    //mMenuRefresh.setOnClickListener(new View.OnClickListener() {
                    //    @Override
                    //    public void onClick(View v) {
                    //        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", v.getRotation() + 180);
                    //        animator.start();
                    //    }
                    //});
                }
            }
        });

        mRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPagerItems = BaseApplication.getInstance().getPagerItems();

        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);

        //-------------------------------------------------------------------------------------------------------
        // 뷰페이저 간 이동 시 프레그먼트 자동으로 새로고침 방지
        // https://stackoverflow.com/questions/28494637/android-how-to-stop-refreshing-fragments-on-tab-change
        //-------------------------------------------------------------------------------------------------------
        mViewPager.setOffscreenPageLimit(mPagerItems.size());

        SlidingTabLayout slidingTabLayout = findViewById(R.id.slidingTabs);
        slidingTabLayout.setViewPager(mViewPager);

        /*
        // 상승
        LinearLayout loRise = findViewById(R.id.loRise);
        loRise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RiseActivity.class);
                startActivity(intent);
            }
        });

        // 수익률
        LinearLayout loReturn = findViewById(R.id.loReturn);
        loReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReturnActivity.class);
                startActivity(intent);
            }
        });

        // 추천수
        LinearLayout loTop = findViewById(R.id.loTop);
        loTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TopActivity.class);
                startActivity(intent);
            }
        });

        // 현재 추천
        LinearLayout loCurrent = findViewById(R.id.loCurrent);
                loCurrent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CurrentActivity.class);
                        startActivity(intent);
            }
        });

        // 실시간 주가
        LinearLayout loRealtime = findViewById(R.id.loRealtime);
        loRealtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RealtimeActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);
                return true;

            case R.id.action_refresh:
                runFragment("refresh"); // 개별 프레그먼트 새로 고침
                //refreshFragment();      // 전체 프레그먼트 새로 고침
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Site site = mPagerItems.get(position);
            return ItemListFragment.newInstance(position, site.getId(), site.getUrl());
        }

        @Override
        public int getCount() {
            return mPagerItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerItems.get(position).getTitle();
        }
    }

    public void runFragment(String command) {
        //--------------------------------------------------------------------------------------------
        // 프레그먼트에 이벤트 전달하기
        // https://stackoverflow.com/questions/34861257/how-can-i-set-a-tag-for-viewpager-fragments
        //--------------------------------------------------------------------------------------------
        int currentItem = mViewPager.getCurrentItem();
        //Site siteData = BaseApplication.getInstance().getPagers().get(currentItem);

        Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + currentItem);

        if (f == null) {
            if ("goBack".equals(command)) {
                super.onBackPressed();
            }
        } else {
            BaseFragment fragment = (ItemListFragment) f;

            switch (command) {
                case "goTop":
                    fragment.goTop();
                    break;
                case "refresh":
                    fragment.refreshFragment();
                    break;
                case "open_in_new":
                    //baseFragment.openInNew();
                    break;
            }
        }
    }

    public void refreshFragment() {
        //--------------------------------------------------------------------------------------------
        // 프레그먼트에 이벤트 전달하기
        // https://stackoverflow.com/questions/34861257/how-can-i-set-a-tag-for-viewpager-fragments
        //--------------------------------------------------------------------------------------------

        // 모든 프레그먼트 새로 고침
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + i);
            ((BaseFragment) f).refreshFragment();
        }

        // 개별 프레그먼트 새로 고침
        //Fragment f = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
        //((BaseFragment) f).refreshFragment();
    }

    @Override
    public void onItemListEvent(String event) {
        switch (event) {
            case "onFavoriteUpdated":
                refreshFragment();
                break;
        }
    }

    private void doCommonFragmentEvent(String event) {
        switch (event) {
            case "onLoadStarted":
                startRefreshAnimation();
                break;
            case "onLoadFinished":
                stopRefreshAnimation();
                break;
        }
    }

    private void startRefreshAnimation() {
        if (mMenuRefresh != null) {
            mMenuRefresh.startAnimation(mRotateAnimation);
        }
    }

    private void stopRefreshAnimation() {
        if (mMenuRefresh != null) {
            mMenuRefresh.clearAnimation();
        }
    }
}
