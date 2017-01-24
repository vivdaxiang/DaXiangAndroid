package com.daxiang.taojin.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daxiang.android.view.CustomToast;
import com.daxiang.taojin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PicturesActivity extends AppCompatActivity {

    private static final String TAG = PicturesActivity.class.getSimpleName();

    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private int mScreenWidth;
    private ArrayList<Integer> mHeights;
    private ArrayList<Integer> mWidths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        ButterKnife.bind(this);
        mContentResolver = getContentResolver();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mHeights = new ArrayList<>();
        mWidths = new ArrayList<>();
        requestReadExternalPermission();
    }

    private ArrayList<Uri> uriArray = new ArrayList();//存放图片URI
    private ArrayList<Long> origIdArray = new ArrayList();//存放图片ID
    private ContentResolver mContentResolver = null;
    private AsyncTask mAsyncTask = new AsyncTask() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {

            String[] projection = {
                    MediaStore.Images.Media._ID
            };
            Uri ext_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String where = MediaStore.Images.Media.SIZE + ">=?";

             /*这个查询操作完成图片大小大于100K的图片的ID查询。
             大家可能疑惑为什么不查询得到图片DATA呢？
             这样是为了节省内存。通过图片的ID可以查询得到指定的图片
             如果这里就把图片数据查询得到，手机中的图片大量的情况下
             内存消耗严重。那么，什么时候查询图片呢？应该是在Adapter
             中完成指定的ID的图片的查询，并不一次性加载全部图片数据*/

            Cursor c = MediaStore.Images.Media.query(
                    mContentResolver,
                    ext_uri,
                    projection,
                    where,
                    new String[]{4 * 1024 * 1024 + ""},//4M
                    MediaStore.Images.Media.DATE_ADDED + " desc");

            int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            int i = 0;
            while (c.moveToNext() && i < c.getCount()) {   //移到指定的位置，遍历数据库
                long origId = c.getLong(columnIndex);
                uriArray.add(
                        Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                origId + "")
                );

                origIdArray.add(origId);
                c.moveToPosition(i);
                i++;
            }
            c.close();//关闭数据库

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            });
            //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mAdapter = new MyAdapter(PicturesActivity.this, uriArray);
            mRecyclerView.setAdapter(mAdapter);
        }
    };

    private void requestReadExternalPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "READ permission IS NOT granted...");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                showMessageOKCancel("You need to allow READ_EXTERNAL_STORAGE permission",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PicturesActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        0);
                            }
                        });
                return;
            } else {
                // 0 是自己定义的请求coude
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {
            Log.i(TAG, "READ permission is granted...");
            mAsyncTask.execute();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PicturesActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(PicturesActivity.this, "你已经拒绝了授权", Toast.LENGTH_SHORT).show();
                        CustomToast.makeText(PicturesActivity.this, "你已经拒绝了授权", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(TAG, "requestCode=" + requestCode + "; --->" + permissions.toString()
                + "; grantResult=" + grantResults.toString());
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // request successfully, handle you transactions

                    mAsyncTask.execute();
                } else {

                    // permission denied
                    // request failed
                    //Toast.makeText(PicturesActivity.this, "你已经拒绝了授权", Toast.LENGTH_SHORT).show();
                    CustomToast.makeText(PicturesActivity.this, "你已经拒绝了授权", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @OnClick(R.id.btn_back)
    public void onBackClick() {
        finish();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Uri> mDatas;
        private Context mContext;
        private BitmapFactory.Options options;

        public MyAdapter(Context mContext, List<Uri> mDatas) {
            this.mContext = mContext;
            this.mDatas = mDatas;
            options = new BitmapFactory.Options();
            options.inSampleSize = 8;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictures_list, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
//            MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),mDatas.get(position),
//                    MediaStore.Images.Thumbnails.MICRO_KIND,options);
            /*// 随机高度, 模拟瀑布效果.
            if (mHeights.size() <= position) {
                mHeights.add((int) (200 + Math.random() * 300));
            }
            if (mWidths.size() <= position) {
                mWidths.add((int) (400 + Math.random() * 300));
            }

            ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
            lp.height = mHeights.get(position);
            lp.width = mWidths.get(position);
            holder.imageView.setLayoutParams(lp);*/

            Glide.with(mContext).loadFromMediaStore(mDatas.get(position)).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }
}
