package com.medcorp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.model.User;
import com.medcorp.view.UsePicturePopupWindow;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medcorp.R.*;

/**
 * Created by med on 16/4/6.
 */
public class ProfileActivity extends BaseActivity {

    @Bind(id.main_toolbar)
    Toolbar toolbar;
    @Bind(id.profile_activity_select_picture)
    ImageView mImageButton;
    private User user;
    private int viewType;
    private UsePicturePopupWindow usePicturePopupWindow;

    private static final int RESULT_CAMERA_ONLY = 100;
    private static final int RESULT_ALBUM_CROP_PATH = 10086;
    private static final int RESULT_CAMERA_CROP_PATH_RESULT = 301;
    private Uri imageUri;
    private Uri imageCropUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(id.lunar_tool_bar_title);
        title.setText(string.profile_title);

        String path = getSDCardPath();
        String fileName = null;
        if (getModel().getNevoUser().isLogin()) {
            fileName = getModel().getNevoUser().getFirstName();
        } else {
            fileName = "med_corp_app_watch";
        }
        File file = new File(path + "/" + fileName + ".jpg");
        imageUri = Uri.fromFile(file);
        File cropFile = new File(getSDCardPath() + "/" + fileName + ".jpg");
        imageCropUri = Uri.fromFile(cropFile);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageCropUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            mImageButton.setImageDrawable(getResources().getDrawable(R.drawable.profile_header_icon));
        }else{
            mImageButton.setImageBitmap(bitmap);
        }
        user = getModel().getNevoUser();
        initView();
    }


    private void initView() {
        final TextView firstName = (TextView) findViewById(id.profile_fragment_user_first_name_tv);
        final TextView lastName = (TextView) findViewById(id.profile_fragment_user_last_name_tv);
        final TextView userBirthday = (TextView) findViewById(id.profile_fragment_user_birthday_tv);
        final TextView userHeight = (TextView) findViewById(id.profile_fragment_user_height_tv);
        final TextView userWeight = (TextView) findViewById(id.profile_fragment_user_weight_tv);

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        //please strictly refer to our UI design Docs, the date format is dd,MMM,yyyy
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd,MMM,yyyy");
        userBirthday.setText(simpleDateFormat.format(new Date(user.getBirthday())));
        userHeight.setText(user.getHeight() + "cm");
        userWeight.setText(user.getWeight() + "kg");

        lastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(lastName);
            }
        });

        firstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(firstName);
            }
        });

        userBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserBirthday(userBirthday);
            }
        });

        userHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserHeight(userHeight);
            }
        });

        userWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserWeight(userWeight);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void editUserName(final TextView nameText) {

        String content = null;
        String hintName = null;
        if (nameText.getId() == id.profile_fragment_user_first_name_tv) {
            content = getString(string.profile_input_user_first_name_dialog_title);
            hintName = user.getFirstName();
        } else if (nameText.getId() == id.profile_fragment_user_last_name_tv) {
            content = getString(string.profile_fragment_input_user_surname_dialog_title);
            hintName = user.getLastName();
        }

        new MaterialDialog.Builder(this).title(getString(string.edit_profile)).content(content)
                .inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(string.profile_fragment_edit_first_name_edit_hint),
                hintName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().length() > 0) {
                            nameText.setText(input.toString());
                            if (nameText.getId() == id.profile_fragment_user_first_name_tv) {
                                user.setFirstName(input.toString());
                            } else if (nameText.getId() == id.profile_fragment_user_last_name_tv) {
                                user.setLastName(input.toString());
                            }
                        }
                    }
                })
                .negativeText(string.notification_cancel).positiveText(getString(string.notification_ok)).show();

    }


    private void editUserWeight(final TextView userWeight) {
        viewType = 3;
        final DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userWeight.setText(dateDesc + "kg");
                        user.setWeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getWeight() + "")
                .build();

        pickerPopWin3.showPopWin(this);
    }

    private void editUserHeight(final TextView userHeight) {

        viewType = 2;
        final DatePickerPopWin pickerPopWin2 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userHeight.setText(dateDesc + "cm");
                        user.setHeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getHeight() + "")
                .build();

        pickerPopWin2.showPopWin(this);
    }

    private void editUserBirthday(final TextView birthdayText) {
        viewType = 1;
        final DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = dateFormat.parse(dateDesc);
                            birthdayText.setText(new SimpleDateFormat("dd,MMM,yyyy", Locale.US).format(date));
                            user.setBirthday(date.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).viewStyle(viewType)
                .viewTextSize(25) // pick view text size
                .minYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) - 100) //min year in loop
                .maxYear(Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date())) + 1)
                .dateChose(new SimpleDateFormat("yyyy-MM-dd").format(new Date(user.getBirthday()))) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(MainActivity.class);
                finish();
                break;
            case id.done_menu:
                getModel().saveNevoUser(user);
                startActivity(MainActivity.class);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(id.profile_activity_select_picture)
    public void settingPicture() {
        usePicturePopupWindow = new UsePicturePopupWindow(this, itemsOnClick);
        usePicturePopupWindow.showAtLocation(this.findViewById(id.profile_activity_select_picture),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usePicturePopupWindow.dismiss();
            switch (v.getId()) {
                case id.user_select_library:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, RESULT_ALBUM_CROP_PATH);
                    break;
                case id.user_select_camera:
                    takeCameraCropUri();
                    break;
                case id.user_select_cancel:
                    usePicturePopupWindow.dismiss();
                    break;
            }

        }
    };


    private void takeCameraCropUri() {
        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, RESULT_CAMERA_ONLY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_CAMERA_ONLY: {
                cropImg(imageUri);
            }
            break;
            case RESULT_CAMERA_CROP_PATH_RESULT: {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver().openInputStream(imageCropUri));
                        mImageButton.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case RESULT_ALBUM_CROP_PATH:
                String picPath = parsePicturePath(ProfileActivity.this, data.getData());
                File file = new File(picPath);
                Uri uri = Uri.fromFile(file);
                cropImg(uri);

                break;
        }

    }

    //剪切
    public void cropImg(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 700);
        intent.putExtra("outputY", 700);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, RESULT_CAMERA_CROP_PATH_RESULT);
    }


    //获取到sd卡的文件路劲
    public static String getSDCardPath() {
        String cmd = "cat /proc/mounts";
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
                        String result = strArray[1].replace("/.android_secure",
                                "");
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

    // 解析获取图片库图片Uri物理路径
    @SuppressLint("NewApi")
    public static String parsePicturePath(Context context, Uri uri) {

        if (null == context || uri == null)
            return null;

        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentUri
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageDocumentsUri
            if (isExternalStorageDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] splits = docId.split(":");
                String type = splits[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + File.separator + splits[1];
                }
            }
            // DownloadsDocumentsUri
            else if (isDownloadsDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaDocumentsUri
            else if (isMediaDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosContentUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                Log.e("harvic", e.getMessage());
            }
        }
        return null;

    }

    private static boolean isExternalStorageDocumentsUri(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocumentsUri(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocumentsUri(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosContentUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}