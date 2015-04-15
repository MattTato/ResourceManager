package com.example.tatoczenkom.resourcemanager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

/**
 *
 */
public class App extends PackageInfo {

    // Declare private fields
    private PackageInfo pkgInfo;
    private String displayName;     // human-readable name for the purposes of this app

    /**
     * CONSTRUCTOR.
     * @param i Package state
     * @param displayName Package name purposed for display.
     */
    public App(PackageInfo i, String displayName) {

        this.pkgInfo = i;
        this.displayName = displayName;
    }

    /**
     * @return Network statistic structure
     */
    public NetStat getNetworkUsage() {
        NetStat s = new NetStat(this.uid());
        //s.getByUid(this.uid());
        return s;
    }

    /**
     * Return the name of this application.
     * @return
     */
    public String name() {

        return this.displayName;
    }

    /**
     * Get a reference to the package's application information.
     * @return
     */
    public ApplicationInfo appinfo() {
        return this.pkgInfo.applicationInfo;
    }

    public int uid() {
        return this.pkgInfo.applicationInfo.uid;
    }


}
