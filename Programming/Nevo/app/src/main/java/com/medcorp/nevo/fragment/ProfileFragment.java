package com.medcorp.nevo.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
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
import com.medcorp.ApplicationFlage;
import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.model.User;

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
        if (ApplicationFlage.FLAGE == ApplicationFlage.Flage.NEVO) {
            addPreferencesFromResource(R.xml.fragment_profile);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = getModel().getNevoUser();
        if (ApplicationFlage.FLAGE == ApplicationFlage.Flage.NEVO) {
            EditTextPreference preference = (EditTextPreference) findPreference("fragment_edit_profile_height");
            preference.setTitle(getModel().getNevoUser().getHeight() + "");
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String value = o.toString();
                    if (value.isEmpty() || !value.matches("[0-9]+")) {
                        return false;
                    }
                    preference.setTitle(value);
                    getModel().getNevoUser().setHeight(Integer.parseInt(value));
                    return true;
                }
            });

            preference = (EditTextPreference) findPreference("fragment_edit_profile_weight");
            preference.setTitle(getModel().getNevoUser().getWeight() + "");
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String value = o.toString();
                    if (value.isEmpty() || !value.matches("[0-9]+")) {
                        return false;
                    }
                    preference.setTitle(value);
                    getModel().getNevoUser().setWeight(Integer.parseInt(value));
                    return true;
                }
            });

            preference = (EditTextPreference) findPreference("fragment_edit_profile_age");
            preference.setTitle(getModel().getNevoUser().getAge() + "");
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String value = o.toString();
                    if (value.isEmpty() || !value.matches("[0-9]+")) {
                        return false;
                    }
                    preference.setTitle(value);
                    getModel().getNevoUser().setAge(Integer.parseInt(value));
                    return true;
                }
            });

            final String genderArray[] = getContext().getResources().getStringArray(R.array.profile_gender);
            Preference preferenceList = findPreference("fragment_edit_profile_gender");
            preferenceList.setTitle(genderArray[getModel().getNevoUser().getSex()]);
            preferenceList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String value = o.toString();
                    if (value.isEmpty() || !value.matches("[0-9]+")) {
                        return false;
                    }
                    preference.setTitle(genderArray[Integer.parseInt(value)]);
                    getModel().getNevoUser().setSex(Integer.parseInt(value));
                    return true;
                }
            });

            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);
            final TextView firstName = (TextView) view.findViewById(R.id.profile_fragment_user_first_name_tv);
            final TextView lastName = (TextView) view.findViewById(R.id.profile_fragment_user_last_name_tv);
            final TextView userBirthday = (TextView) view.findViewById(R.id.profile_fragment_user_birthday_tv);
            final TextView userHeight = (TextView) view.findViewById(R.id.profile_fragment_user_height_tv);
            final TextView userWeight = (TextView) view.findViewById(R.id.profile_fragment_user_weight_tv);
            User user = getModel().getNevoUser();
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            userBirthday.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date(user.getBirthday())));
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
        } else if (nameText.getId() == R.id.profile_fragment_user_last_name_tv) {
            editName.setHint(getString(R.string.profile_fragment_input_surname_edit_hint));
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
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose("60")
                .build();
        pickerPopWin3.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                upDataUserData(userWeight);

            }
        });
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
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose("170")
                .build();
        pickerPopWin2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                upDataUserData(userHeight);
            }
        });
        pickerPopWin2.showPopWin(ProfileFragment.this.getActivity());
    }

    private void editUserBirthday(final TextView birthdayText) {
        viewType = 1;
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formatDate = format.format(date);
        final DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(ProfileFragment.this.getActivity(),
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                        try {
                            Date date = dateFormat.parse(dateDesc);
                            birthdayText.setText(new SimpleDateFormat("MMM", Locale.US).format(date) + "-" + day + "-" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).viewStyle(viewType)
                .viewTextSize(25) // pick view text size
                .minYear(Integer.valueOf(formatDate.split("-")[0]) - 100) //min year in loop
                .maxYear(Integer.valueOf(formatDate.split("-")[0])) // max year in loop
                .dateChose((Integer.valueOf(formatDate.split("-")[0]) - 30)
                        + "-" + formatDate.split("-")[1] + "-" + formatDate.split("-")[2]) // date chose when init popwindow
                .build();
        pickerPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                upDataUserData(birthdayText);

            }
        });
        pickerPopWin.showPopWin(ProfileFragment.this.getActivity());
    }

    /**
     * @param userWeight
     */
    private void upDataUserData(TextView userWeight) {
        switch (userWeight.getId()) {
            case R.id.profile_fragment_user_birthday_tv:
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                try {
                    Date date = sdf.parse(userWeight.getText().toString());
                    user.setBirthday(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.profile_fragment_user_height_tv:
                String height = userWeight.getText().toString();
                if (height.contains("cm")) {
                    user.setHeight(new Integer(height.replace("cm", "")).intValue());
                }
                break;
            case R.id.profile_fragment_user_weight_tv:
                String weight = userWeight.getText().toString();
                if (weight.contains("kg")) {
                    //TODO未确定类型
                    //                    user.setWeight(new Integer(weight.replace("kg","")).intValue());
                    user.setWeight((int) Double.parseDouble(weight.replace("kg", "")));
                }
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getModel().saveNevoUser(getModel().getNevoUser());
                if (user != null) {
                    getModel().saveNevoUser(user);
                }
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
