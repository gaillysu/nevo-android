package com.medcorp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.medcorp.model.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Created by Jason on 2016/11/7.
 */

public class PublicUtils {

    private static Uri imageUri;
    private static Uri imageCropUri;

    public static Bitmap getProfileIcon(Context context, User user) {
        String path = PublicUtils.getSDCardPath();
        String fileName = null;
        if (user.isLogin()) {
            fileName = user.getNevoUserEmail();
        } else {
            fileName = "med_corp_app_watch";
        }
        File file = new File(path + "/" + fileName + ".jpg");
        File cropFile = new File(PublicUtils.getSDCardPath() + "/" + fileName + ".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            //imageCropUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", cropFile);
            imageCropUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", cropFile);
        } else {
            imageUri = Uri.fromFile(file);
            imageCropUri = Uri.fromFile(cropFile);
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageCropUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap == null ? null : drawCircleView(bitmap);
    }

    public static Bitmap drawCircleView(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这里需要先画出一个圆
        canvas.drawCircle(100, 100, 100, paint);
        //圆画好之后将画笔重置一下
        paint.reset();
        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }

    //获取到sd卡的文件路劲
    public static String getSDCardPath() {
        String cmd = "cat/proc/mounts";
        Runtime run = Runtime.getRuntime();
        try {
            Process p = run.exec(cmd);
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                if (lineStr.contains("sdcard")
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure", "");
                        return result;
                    }
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                }
            }
            inBr.close();
            in.close();
        } catch (Exception e) {
            return Environment.getExternalStorageDirectory().getPath();
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

}
