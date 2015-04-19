package com.example.tatoczenkom.resourcemanager;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {


    public DataFragment() {
        // Required empty public constructor
    }

    List<App> apps;             // consider use of public/private
    ListAdapter appAdapter;     // used to display
    ListView appList;           // UI-visible list of apps


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    /**
     * Get application data and register on UI.
     */
    private void initializeList() {

        Context cxt = getActivity();//.getApplicationContext();

        // Initialize the logic-side app list, then sort
        apps = MainActivity.pollApps(cxt);
        Collections.sort(apps, new AppDataComparitor());

        // Use the app info to gather a list of display data
        List<String> displayData = new ArrayList<>();
        for (App app : apps) {

            // For each valid app...
            NetStat n = app.getNetworkUsage();
            if (n.valid()) {

                // Get its network usage...
                int units = n.biggestInformativeUnit();
                long sent = n.sent(units);
                long recd = n.received(units);
                String unitAbbv = NetStat.unitAbbv(units);

                // Format the display data and add to list
                String datum = app.name() + " | " + Long.toString(sent) + unitAbbv + "," + Long.toString(recd) + unitAbbv;
                displayData.add(displayData.size(), datum);

            }
        }

        // Initialize the adapter (connect adapter to logic-side list)
        appAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_list_item_1, displayData);

        // Initialize the display list (connect adapter to UI-side list)
        appList = (ListView)(getView().findViewById(R.id.data_app_list));
        appList.setAdapter(appAdapter);
    }
}
