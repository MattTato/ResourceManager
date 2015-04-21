package com.example.tatoczenkom.resourcemanager;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * Fragment meant to show some simple properties on the phone's battery
 * Also links over to the Android Battery Usage settings page
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
        GetBatteryInformation(v);
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

    private void GetBatteryInformation(View v){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        CheckBox checkCharging = (CheckBox)v.findViewById(R.id.chargingCheckBox);
        checkCharging.setChecked(isCharging);
        checkCharging.setClickable(false);

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        CheckBox acChargeBox = (CheckBox)v.findViewById(R.id.acChargeCheckBox);
        acChargeBox.setClickable(false);
        acChargeBox.setChecked(acCharge);

        CheckBox usbChargeBox = (CheckBox)v.findViewById(R.id.usbChargeCheckBox);
        usbChargeBox.setClickable(false);
        usbChargeBox.setChecked(usbCharge);

        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        boolean isGood = health == BatteryManager.BATTERY_HEALTH_GOOD;

        CheckBox batHealthBox = (CheckBox)v.findViewById(R.id.batHealthGoodCheckBox);
        batHealthBox.setClickable(false);
        batHealthBox.setChecked(isGood);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        batteryPct *= 100;

        TextView batteryPctText = (TextView)v.findViewById(R.id.chargingPercentText);
        batteryPctText.setText("Battery percentage: " + Float.toString(batteryPct) + "%");
    }
}
