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
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.model.User;
import com.medcorp.util.PublicUtils;
import com.medcorp.view.UsePicturePopupWindow;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by med on 16/4/6.
 */
public class ProfileActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.profile_activity_select_picture)
    ImageView mImageButton;
    @Bind(R.id.profile_activity_edit_first_name)
    LinearLayout editFirstNameL;
    @Bind(R.id.profile_activity_edit_last_name)
    LinearLayout editLastName;
    @Bind(R.id.edit_user_birthday_pop)
    LinearLayout editUserBirthday;
    @Bind(R.id.edit_user_height_pop)
    LinearLayout editUserHeightL;
    @Bind(R.id.edit_user_weight_pop)
    LinearLayout editUserWeightL;

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
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.profile_title);

        String path = PublicUtils.getSDCardPath();
        String fileName = null;
        if (getModel().getNevoUser().isLogin()) {
            fileName = getModel().getNevoUser().getNevoUserEmail();
        } else {
            fileName = "med_corp_app_watch";
        }
        File file = new File(path + "/" + fileName + ".jpg");
        File cropFile = new File(PublicUtils.getSDCardPath() + "/" + fileName + ".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            imageCropUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", cropFile);
        } else {
            imageUri = Uri.fromFile(file);
            imageCropUri = Uri.fromFile(cropFile);
        }
        //        Bitmap bitmap = null;
        //        try {
        //            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageCropUri));
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
        Bitmap bitmap = PublicUtils.getProfileIcon(this, getModel().getNevoUser());
        if (bitmap == null) {
            mImageButton.setImageDrawable(getResources().getDrawable(R.drawable.user));
        } else {
            mImageButton.setImageBitmap(bitmap);
        }

        user = getModel().getNevoUser();
        initView();
    }


    private void initView() {
        final TextView firstName = (TextView) findViewById(R.id.profile_fragment_user_first_name_tv);
        final TextView lastName = (TextView) findViewById(R.id.profile_fragment_user_last_name_tv);
        final TextView userBirthday = (TextView) findViewById(R.id.profile_fragment_user_birthday_tv);
        final TextView userHeight = (TextView) findViewById(R.id.profile_fragment_user_height_tv);
        final TextView userWeight = (TextView) findViewById(R.id.profile_fragment_user_weight_tv);

        firstName.setText(TextUtils.isEmpty(user.getFirstName())? getString(R.string.edit_user_first_name) :user.getFirstName());
        lastName.setText(TextUtils.isEmpty(user.getLastName())?getString(R.string.edit_user_last_name):user.getLastName());
        //please strictly refer to our UI design Docs, the date format is dd,MMM,yyyy
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        userBirthday.setText(simpleDateFormat.format(new Date(user.getBirthday())));
        userHeight.setText(user.getHeight() + " cm");
        userWeight.setText(user.getWeight() + " kg");

        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(lastName);
            }
        });

        editFirstNameL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName(firstName);
            }
        });

        editUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserBirthday(userBirthday);
            }
        });

        editUserHeightL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserHeight(userHeight);
            }
        });

        editUserWeightL.setOnClickListener(new View.OnClickListener() {
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
        if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
            content = getString(R.string.profile_input_user_first_name_dialog_title);
            hintName = user.getFirstName();
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            content = getString(R.string.profile_fragment_input_user_surname_dialog_title);
            hintName = user.getLastName();
        }

        new MaterialDialog.Builder(this).title(getString(R.string.edit_profile)).content(content)
                .inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(R.string.profile_fragment_edit_first_name_edit_hint),
                hintName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().length() > 0) {
                            nameText.setText(input.toString());
                            if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
                                user.setFirstName(input.toString());
                            } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
                                user.setLastName(input.toString());
                            }
                        }
                    }
                })
                .negativeText(R.string.notification_cancel).positiveText(getString(R.string.notification_ok)).show();

    }


    private void editUserWeight(final TextView userWeight) {
        viewType = 3;
        final DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userWeight.setText(dateDesc + " kg");
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
                        userHeight.setText(dateDesc + " cm");
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
                            birthdayText.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(date));
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
                overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
                break;
            case R.id.done_menu:
                getModel().saveNevoUser(user);
                startActivity(MainActivity.class);
                finish();
                overridePendingTransition(R.anim.anim_left_in, R.anim.push_left_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.profile_activity_select_picture)
    public void settingPicture() {
        usePicturePopupWindow = new UsePicturePopupWindow(this, itemsOnClick);
        usePicturePopupWindow.showAtLocation(this.findViewById(R.id.profile_activity_select_picture),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usePicturePopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.user_select_library:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, RESULT_ALBUM_CROP_PATH);
                    break;
                case R.id.user_select_camera:
                    takeCameraCropUri();
                    break;
                case R.id.user_select_cancel:
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
                        Bitmap afterBitmap = PublicUtils.drawCircleView(bitmap);
                        if (bitmap == null) {
                            mImageButton.setImageResource(R.drawable.user);
                        } else {
                            mImageButton.setImageBitmap(afterBitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case RESULT_ALBUM_CROP_PATH:
                String picPath = parsePicturePath(ProfileActivity.this, data.getData());
                File file = new File(picPath);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                cropImg(uri);

                break;
        }

    }

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

    // 解析获取图片库图片Uri物理路径
    @SuppressLint("NewApi")
    public String parsePicturePath(Context context, Uri uri) {

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

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

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
                Log.e("jason", e.getMessage());
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