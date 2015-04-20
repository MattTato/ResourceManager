package com.example.tatoczenkom.resourcemanager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryFragment extends Fragment implements View.OnClickListener {


    public BatteryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battery, container, false);

        View btnSettings = v.findViewById(R.id.opensettings_button);
        btnSettings.setOnClickListener(this);
        return v;
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.opensettings_button:
                StartBatteryUsageActivity();
                break;
        }
    }

    private void StartBatteryUsageActivity(){
        getActivity().startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
    }
}
