package com.example.myapplication.ui.profile;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


import com.example.myapplication.R;

import java.util.Objects;

import static android.content.Context.ACTIVITY_SERVICE;

public class ProfileFragment extends PreferenceFragmentCompat  {
    ListPreference list;
    Preference dialogPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);



        dialogPreference = (Preference) findPreference("reset_preference");
        assert dialogPreference != null;
        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(requireContext());
                mBuilder.setTitle("RESET");
                mBuilder.setMessage("Are you sure you want to do this? This action cannot be undone. You'll have to start all over again");
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // clearing app data
                            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                                ((ActivityManager) Objects.requireNonNull(requireActivity().getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                            } else {
                                String packageName = requireActivity().getApplicationContext().getPackageName();
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec("pm clear "+packageName);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDividerHeight(16);
    }


}
