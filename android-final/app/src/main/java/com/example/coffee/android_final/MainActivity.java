package com.example.coffee.android_final;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AddAccountFragment addAccountFragment;
    private RecordFragment recordFragment;
    private AnalysisFragment analysisFragment;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActBarDrawerToggle;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAccountFragment = new AddAccountFragment();
        recordFragment = new RecordFragment();
        analysisFragment = new AnalysisFragment();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // 設定 view pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(tabSelectedListener);

        // 設定側開式選單。
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeButtonEnabled(true);
        mActBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mActBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mActBarDrawerToggle);

        ListView listView = (ListView) findViewById(R.id.lstDrawer);
        listView.setAdapter(ArrayAdapter.createFromResource(this, R.array.drawerItem, android.R.layout.simple_list_item_1));
        listView.setOnItemClickListener(lstOnItemClick);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 要先把選單的項目傳給 ActionBarDrawerToggle 處理。
        // 如果它回傳 true，表示處理完成，不需要再繼續往下處理。

        if (mActBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemModify:
                // 將資料傳進 AddNewContact 中 並切換頁面
                addAccountFragment.setAccountDataByIndex(recordFragment.getLongClickedItemIndex());
                mViewPager.setCurrentItem(0);
                Toast.makeText(MainActivity.this, "請重新提交資料", Toast.LENGTH_LONG).show();
                break;
            case R.id.menuItemDelete:
                addAccountFragment.deleteAccountDataByIndex(recordFragment.getLongClickedItemIndex());
                recordFragment.createList(addAccountFragment.getAccountDataList());
                Toast.makeText(MainActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private AdapterView.OnItemClickListener lstOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mViewPager.setCurrentItem(position);
            mDrawerLayout.closeDrawers();
        }
    };

    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            // Close keyboard when user click TabLayout
            InputMethodManager inputMethodManager = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
            if(inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(MainActivity.this.getCurrentFocus()).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
            switch (tab.getPosition()) {
                case 0:
                    break;
                case 1:
                    recordFragment.createList(addAccountFragment.getAccountDataList());
                    break;
                case 2:
                    try {
                        analysisFragment.analysis(addAccountFragment.getAccountDataList());
                    }
                    catch (IllegalStateException e) {
                        Toast.makeText(MainActivity.this, "請先點擊 「帳目紀錄」 再點擊 「帳目分析」", Toast.LENGTH_LONG).show();
                    }
                    catch (NullPointerException e) {

                    }

                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return addAccountFragment;
                case 1:
                    return recordFragment;
                case 2:
                    return analysisFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.addAccount);
                case 1:
                    return getString(R.string.accountRecord);
                case 2:
                    return getString(R.string.analysisAccount);
                default:
                    return null;
            }
        }
    }
}