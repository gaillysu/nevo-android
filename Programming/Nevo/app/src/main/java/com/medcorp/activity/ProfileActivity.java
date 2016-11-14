package com.medcorp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


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
    private Bitmap head;
    private static String path = "/sdcard/myHead/";
    private String userEmail;
    private static final int REQUEST_IMAGE = 100;

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
        user = getModel().getNevoUser();
        if (user.isLogin()) {
            userEmail = user.getNevoUserEmail();
        } else {
            userEmail = "watch_med_profile";
        }
        Bitmap bt = BitmapFactory.decodeFile(path + userEmail + ".jpg");//从Sd中找头像，转换成Bitmap

        if (bt != null) {
            mImageButton.setImageBitmap(PublicUtils.drawCircleView(bt));
        } else {
            mImageButton.setImageResource(R.drawable.user);
        }
        initView();
    }


    private void initView() {
        final TextView firstName = (TextView) findViewById(R.id.profile_fragment_user_first_name_tv);
        final TextView lastName = (TextView) findViewById(R.id.profile_fragment_user_last_name_tv);
        final TextView userBirthday = (TextView) findViewById(R.id.profile_fragment_user_birthday_tv);
        final TextView userHeight = (TextView) findViewById(R.id.profile_fragment_user_height_tv);
        final TextView userWeight = (TextView) findViewById(R.id.profile_fragment_user_weight_tv);

        firstName.setText(TextUtils.isEmpty(user.getFirstName()) ? getString(R.string.edit_user_first_name) : user.getFirstName());
        lastName.setText(TextUtils.isEmpty(user.getLastName()) ? getString(R.string.edit_user_last_name) : user.getLastName());
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
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE:
                if (data != null) {
                    List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (path.size() > 0) {
                        String imagePath = path.get(0);
                        if (imagePath != null) {
                            Bitmap head = null;
                            try {
                                head = BitmapFactory.decodeStream(new FileInputStream(imagePath));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            setPicToView(head);//保存在SD卡中
                            mImageButton.setImageBitmap(PublicUtils.drawCircleView(head));//用ImageView显示出来
                        }
                    }
                }
                break;
            default:
                break;

        }
    }


    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream bot = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + userEmail + ".jpg";//图片名字
        try {
            bot = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bot);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bot != null) {
                    //关闭流
                    bot.flush();
                    bot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}