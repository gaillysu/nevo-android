package com.medcorp.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.R;
import com.medcorp.application.ApplicationModel;
import com.medcorp.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by med on 16/4/6.
 */
public class ProfileFragment extends PreferenceFragmentCompat {

    private int viewType;
    private User user;

    private ApplicationModel getModel() {
        return (ApplicationModel) getActivity().getApplication();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = getModel().getNevoUser();

        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);
        final TextView firstName = (TextView) view.findViewById(R.id.profile_fragment_user_first_name_tv);
        final TextView lastName = (TextView) view.findViewById(R.id.profile_fragment_user_last_name_tv);
        final TextView userBirthday = (TextView) view.findViewById(R.id.profile_fragment_user_birthday_tv);
        final TextView userHeight = (TextView) view.findViewById(R.id.profile_fragment_user_height_tv);
        final TextView userWeight = (TextView) view.findViewById(R.id.profile_fragment_user_weight_tv);

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
        return view;
    }

    /**
     * Lunar code
     *
     * @param nameText
     */
    private void editUserName(final TextView nameText) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileFragment.this.getActivity());

        if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
            dialog.setTitle(getString(R.string.profile_input_user_first_name_dialog_title));
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            dialog.setTitle(getString(R.string.profile_fragment_input_user_surname_dialog_title));
        }

        LayoutInflater inflater = ProfileFragment.this.getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_fragment_input_dialog_layout, null);
        final EditText editName = (EditText) dialogView.findViewById(R.id.profile_fragment_input_dialog_edit_text);
        if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
            editName.setHint(getString(R.string.profile_fragment_edit_first_name_edit_hint));
            editName.setText(user.getFirstName());
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            editName.setHint(getString(R.string.profile_fragment_input_surname_edit_hint));
            editName.setText(user.getLastName());
        }

        dialog.setView(dialogView);
        dialog.setPositiveButton(getString(R.string.notification_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String editAfterFirstName = editName.getText().toString();
                if (!TextUtils.isEmpty(editAfterFirstName)) {
                    nameText.setText(editAfterFirstName);

                    if (nameText.getId() == R.id.profile_fragment_user_first_name_tv) {
                        user.setFirstName(editAfterFirstName);
                    } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
                        user.setLastName(editAfterFirstName);
                    }
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.notification_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void editUserWeight(final TextView userWeight) {
        viewType = 3;
        final DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(ProfileFragment.this.getActivity(),
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userWeight.setText(dateDesc + "kg");
                        user.setWeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getWeight()+"")
                .build();

        pickerPopWin3.showPopWin(ProfileFragment.this.getActivity());
    }

    private void editUserHeight(final TextView userHeight) {

        viewType = 2;
        final DatePickerPopWin pickerPopWin2 = new DatePickerPopWin.Builder(ProfileFragment.this.getActivity(),
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        userHeight.setText(dateDesc + "cm");
                        user.setHeight(new Integer(dateDesc).intValue());
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose(user.getHeight()+"")
                .build();

        pickerPopWin2.showPopWin(ProfileFragment.this.getActivity());
    }

    private void editUserBirthday(final TextView birthdayText) {
        viewType = 1;
        final DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(ProfileFragment.this.getActivity(),
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
        pickerPopWin.showPopWin(ProfileFragment.this.getActivity());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getModel().saveNevoUser(user);
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
