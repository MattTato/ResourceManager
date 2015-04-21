package com.example.tatoczenkom.resourcemanager;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 * DATAFRAGMENT
 *
 * This fragment displays network traffic statistics by app.
 */
public class DataFragment extends Fragment {


    public DataFragment() {
        // Required empty public constructor
    }

    List<App> apps;             // consider use of public/private
    ListAdapter appAdapter;     // used to display
    ListView appList;           // UI-visible list of apps

    static final long MILLIS_IN_HOURS = (60 * 60 * 1000);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data, container, false);
        initializeList(v);
        return v;
    }


    /**
     * Get application data and register on UI.
     */
    private void initializeList(View v) {
        Context cxt = getActivity();

        // Initialize the logic-side app list, then sort
        apps = MainActivity.pollApps(cxt);
        Collections.sort(apps, new AppDataComparitor());

        // Use the app info to gather a list of display data
        List<String> displayData = new ArrayList<>();
        for (App app : apps) {

            // For each valid app...
            NetStat n = app.getNetworkUsage();
            if (n.valid()) {

                // Extract network statistics in friendly units
                int unitFlag = NetStat.biggestInformativeUnit(n.total());
                String value = Long.toString(NetStat.convert(n.total(), unitFlag));
                String units = NetStat.unitAbbv(unitFlag);

                // Format the display data and add to list
                String datum = value + ' ' + units + " | " + app.name();
                displayData.add(displayData.size(), datum);

            }
        }

        // Initialize the adapter (connect adapter to logic-side list)
        appAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_list_item_1, displayData);

        // Initialize the display list (connect adapter to UI-side list)
        appList = (ListView)(v.findViewById(R.id.data_app_list));
        appList.setAdapter(appAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                App a = apps.get(position);
                String detail = dataDetail(a);

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setTitle(a.name());
                dlg.setMessage(detail);
                dlg.setNeutralButton("OK", null);
                dlg.create().show();
            }
        });
    }

    /**
     * Returns a string-encoded detail of the network traffic
     * of an App.
     *
     * Explains: package name, network usage (sent, received, total),
     * usage rate (KB/hr).
     *
     * @param a reference to App
     * @return detailed network statistics (as string)
     */
    private String dataDetail(App a) {

        NetStat n = a.getNetworkUsage();
        long millisSinceBoot = SystemClock.elapsedRealtime();
        long hoursSinceBoot = millisSinceBoot / MILLIS_IN_HOURS;

        // PACKAGE NAME FIELD
        String detail = "Package: \"" + a.pkgInfo().packageName + "\"\n";
        detail += "UID=" + Integer.toString(a.uid()) + '\n';

        // HOURS-SINCE-BOOT FIELD
        detail += "\nHours since boot: " + Long.toString(hoursSinceBoot) + '\n';

        // SEND:RECEIVE RATIO FIELD
        detail += "Send:Receive = " + Integer.toString((int)(100.0 * ((double)n.sent() / (double)n.received()) + 0.5)) + "%\n";

        // DATA-BREAKDOWN FIELDS
        detail += '\n';
        detail += detailFieldContent("Sent", n.sent(), hoursSinceBoot);
        detail += detailFieldContent("Recd", n.received(), hoursSinceBoot);
        detail += detailFieldContent("Total", n.total(), hoursSinceBoot);

        return detail;
    }

    /**
     * For a byte-volume, returns a string representation
     * of the volume in terms of friendly units. The unit
     * abbreviation is appended.
     *
     * @param bytes volume of bytes
     * @return string-representation of volume in friendly units
     */
    private String friendlyMeasure(long bytes) {
        int friendlyUnits = NetStat.biggestInformativeUnit(bytes);
        return Long.toString(NetStat.convert(bytes, friendlyUnits)) + ' ' + NetStat.unitAbbv(friendlyUnits);
    }

    /**
     * Returns a string containing a field of data for the
     * data detail dialog. Format:
     * <NAME>: <VOLUME> [(<RATE>)]
     *
     * @param name name of measure
     * @param bytes byte volume
     * @param hoursSinceBoot long-represented hours since boot
     *
     * @return data field for placement within data-detail dialog
     */
    private String detailFieldContent(String name, long bytes, long hoursSinceBoot) {

        // Put NAME and byte-volume
        String content = name + ":\t";

        // If a rate is useful, add as parenthetical
        content += friendlyMeasure(bytes);
        if (hoursSinceBoot >= 2) {
            long dataRate = bytes / hoursSinceBoot;
            content += " (" + friendlyMeasure(dataRate) + "/hr)";
        }
        content += '\n';
        return content;
    }
}
