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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 *
 * MALICIOUSAPPSFRAGMENT
 *
 * This fragment shows the top 5 applications that use the most data on the device.
 */
public class MaliciousAppsFragment extends Fragment {


    public MaliciousAppsFragment() {
        // Required empty public constructor
    }

    List<AppGroup> groups;      // logic-side list of like-UID app groups
    ListAdapter appAdapter;     // used to display
    ListView appList;           // UI-visible list of apps
    NetStat aggregate;
    long millisSinceBoot;
    long hoursSinceBoot;
    TextView topText;
    static final long MILLIS_IN_HOURS = (60 * 60 * 1000);
    static final int MAX_NUM = 5;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_malicious_apps, container, false);
        initializeList(v);
        return v;
    }



    /**
     * Get application data and register on UI.
     */
    private void initializeList(View v) {
        Context cxt = getActivity();

        // Update time
        this.millisSinceBoot = SystemClock.elapsedRealtime();
        this.hoursSinceBoot = this.millisSinceBoot / MILLIS_IN_HOURS;

        // Gather informal groupings of apps by UID
        Map<Integer, Set<App>> appGroups = new HashMap<>();
        List<App> apps = MainActivity.pollApps(cxt);
        for (App a : apps) {
            int k = a.uid(); // use the UID as the package group key

            // If the UID has yet to be encountered...
            if (!appGroups.containsKey(k)) {
                Set<App> grouping = new HashSet<>();    // ... make a group for it
                appGroups.put(k, grouping);             // ... add the group to the others
            }

            // Put the app into its appropriate group
            appGroups.get(k).add(a);
        }

        // Filter groups into a formal AppGroup list, then sort
        groups = new ArrayList<>();
        Set<Integer> uids = appGroups.keySet();
        for (int uid : uids) {
            AppGroup temp = new AppGroup(uid);
            temp.apps = appGroups.get(uid);
            groups.add(temp);
        }

        // Filter out any undesirable groups
        for (int i= groups.size()-1; i >= 0; i--) {
            if (groups.get(i).getNetworkUsage().total() < 1) {
                groups.remove(i);
            }
        }
        Collections.sort(groups, new AppGroupDataComparator());

        // Use the app info to gather the list of display data
        List<String> displayData = new ArrayList<>();
        long totalSent = 0, totalRecd = 0;
        for (int i=0; i<groups.size(); i++) { // for each app group...
            // Check if there is a group to display
            if (i + 1 <= MAX_NUM) {
                // ... get network activity
                NetStat n = groups.get(i).getNetworkUsage();
                totalSent += n.sent();
                totalRecd += n.received();

                // Extract network statistics in friendly units
                int unitFlag = NetStat.biggestInformativeUnit(n.total());
                String value = Long.toString(NetStat.convert(n.total(), unitFlag));
                String units = NetStat.unitAbbv(unitFlag);

                // Format the display data and add to list
                String datum = value + ' ' + units + " | " + groups.get(i).name();
                displayData.add(displayData.size(), datum);
            }
        }
        aggregate = new NetStat(totalSent, totalRecd);

        // Initialize the adapter (connect adapter to logic-side list)
        appAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_list_item_1, displayData);

        // Initialize the display list (connect adapter to UI-side list)
        appList = (ListView)(v.findViewById(R.id.mal_app_list));
        appList.setAdapter(appAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppGroup group = groups.get(position);  // get group data
                dataDetailDialog(group.name(), group.getNetworkUsage(), group);
            }
        });

        // Put text at the top of the list to say what this screen does
        topText = (TextView) (v.findViewById(R.id.textView1));
        topText.setText("These are the applications that are taking up the most data, and " +
        "therefore could be malicious.");
        if (groups.size() > 0) {
            topText.setVisibility(View.VISIBLE);
        } else {
            topText.setVisibility(View.GONE);
        }
    }

    private void dataDetailDialog(String title, NetStat n, AppGroup g) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        dlg.setTitle(title);
        dlg.setMessage(dataDetail(n, g));
        dlg.setNeutralButton("OK", null);
        dlg.create().show();
    }

    /**
     * Returns a string-encoded detail of the network traffic
     * of an App.
     *
     * Explains: package name, network usage (sent, received, total),
     * usage rate (KB/hr).
     *
     * @param group set of like-UID App
     * @return detailed network statistics (as string)
     */
    private String dataDetail(NetStat n, AppGroup group) {

        // HOURS-SINCE-BOOT FIELD
        String detail = "\nHours since boot: " + Long.toString(hoursSinceBoot) + '\n';

        // SEND:RECEIVE RATIO FIELD
        detail += "Send:Receive = " + Integer.toString((int)(100.0 * ((double)n.sent() / (double)n.received()) + 0.5)) + "%\n";

        // DATA-BREAKDOWN FIELDS
        detail += '\n';
        detail += detailFieldContent("Sent", n.sent());
        detail += detailFieldContent("Recd", n.received());
        detail += detailFieldContent("Total", n.total());

        // LIST PACKAGES
        if (group != null) {
            if (group.apps != null) {
                if (group.apps.size() > 1) {
                    detail += '\n';
                    detail += "PACKAGES:\n";
                    for (App app : group.apps) {
                        String appname = app.name();
                        String pkgname = app.pkgInfo().packageName;

                        String usename = appname;
                        if (appname == null) {
                            usename = pkgname;
                        }

                        detail += '"' + usename + '"' + '\n';
                    }

                }

            }

        }

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
     *
     * @return data field for placement within data-detail dialog
     */
    private String detailFieldContent(String name, long bytes) {

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
