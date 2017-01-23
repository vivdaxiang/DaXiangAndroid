package com.daxiang.taojin.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.PopupMenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daxiang.taojin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private FrameLayout mEndDrawer;
    private LinearLayout mStartDrawer;
    @BindView(R.id.start_drawer_list)
    public ListView mStartDrawerList;
    @BindView(R.id.btn_more)
    public ImageButton btn_more;
    private PopupMenu mPopupMenu;
    @BindView(R.id.titleBar)
    public RelativeLayout titleBar;

    private static final String[] StartMenuStr = new String[]{"我的相册", "收藏夹", "我的钱包", "我的足迹"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // 设置状态栏透明

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);

            contentLayout.setFitsSystemWindows(true);

            contentLayout.setClipToPadding(true);

            // 设置抽屉布局属性

            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);

            vg.setFitsSystemWindows(false);

            // 设置 DrawerLayout 属性

            drawerLayout.setFitsSystemWindows(false);

        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mEndDrawer = (FrameLayout) findViewById(R.id.end_drawer);
        mStartDrawer = (LinearLayout) findViewById(R.id.start_drawer);

        mStartDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_drawer_menu_item, StartMenuStr));
        mStartDrawerList.setDivider(null);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_end, GravityCompat.END);
//        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

    }

    @Override
    public void onBackPressed() {
        boolean hadOpenDrawer = false;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            hadOpenDrawer = true;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            hadOpenDrawer = true;
        }
        if (hadOpenDrawer) return;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @OnItemClick(R.id.start_drawer_list)
    public void onStartDrawerListItemClick(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch(position){
            case 0:
                startActivity(new Intent(this,PicturesActivity.class));
                break;
            case 1:
                break;
        }
    }

    @OnClick(R.id.drawer_toogle)
    public void toggleDrawerLayout() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @OnClick(R.id.btn_more)
    public void showPopupMenu() {
        mPopupMenu = new PopupMenu(this, titleBar, Gravity.RIGHT);
        MenuInflater inflater = mPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                //Toast.makeText(MainActivity.this, "Dismiss", Toast.LENGTH_SHORT).show();
            }
        });
        mPopupMenu.show();
    }
}
