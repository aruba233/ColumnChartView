package com.aruba.columnchartview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LruCache;
import android.widget.ImageView;



/**
 * 作者　　: aruba
 * 创建时间:2017/7/27　15:05
 * <p>
 * 功能介绍：
 */

public class ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private static ImageCache imageCache;

    private ImageCache() {
        initCache();
    }

    public synchronized static ImageCache getInstance() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        }
        return imageCache;
    }

    private void initCache() {
        int MaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);// kB
        int cacheSize = MaxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //bitmap.getByteCount() = bitmap.getRowBytes() * bitmap.getHeight(); 
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;// KB
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        ImageCache.getInstance().mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return ImageCache.getInstance().mMemoryCache.get(key);
    }

    public void loadBitmap(String base64Str, ImageView imageView) {
        final String imageKey = base64Str.substring(0, 15) + base64Str.substring(base64Str.length() - 15, base64Str.length());
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(imageKey, base64Str);
        }
    }

    public void loadBitmap(Context context, int resId, ImageView imageView) {
        final String imageKey = resId + "";
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapResTask task = new BitmapResTask(imageView, context.getApplicationContext());
            task.execute(resId);
        }
    }

    class BitmapResTask extends AsyncTask<Integer, Void, Bitmap> {
        private ImageView imageView;
        private Context context;

        public BitmapResTask(ImageView imageView, Context context) {
            this.imageView = imageView;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }

        protected Bitmap doInBackground(Integer... params) {
            Drawable drawable = context.getApplicationContext().getResources().getDrawable(params[0]);
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            }
            return bitmap;
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }

        protected Bitmap doInBackground(String... params) {
            final Bitmap bitmap = stringtoBitmap(params[1]);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        public Bitmap stringtoBitmap(String string) {
            if (string == null || TextUtils.isEmpty(string)) {
                return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
            }
            //将字符串转换成Bitmap类型
            Bitmap bitmap = null;
            try {
                byte[] bitmapArray;
                bitmapArray = Base64.decode(string, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }
    }
}
