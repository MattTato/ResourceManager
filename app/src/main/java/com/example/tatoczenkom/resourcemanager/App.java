package com.example.tatoczenkom.resourcemanager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

/**
 * APP
 *
 * This class serves as the go-to collection of data
 * and methods that allow 'ResourceManager' to
 * conceptualize an app.
 */
public class App extends PackageInfo {

    // Declare private fields
    private PackageInfo pkgInfo;
    private String displayName;     // human-readable name for the purposes of this app
    private NetStat netstat;        // data point: network statistics

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
     * Returns the netstat field. Initializes the field if necessary.
     *
     * This app's netstat field is not necessarily used.
     * It is therefore not initialized in the constructor.
     *
     * @return Network statistic structure
     */
    public NetStat getNetworkUsage() {

        // Initialize if necessary
        if (this.netstat == null) {
            this.netstat = new NetStat(this.uid());
        }

        return this.netstat;
    }

    /**
     * Return the name of this application.
     * @return display name of this application
     */
    public String name() {

        return this.displayName;
    }

    /**
     * Get a reference to the package's application information.
     * @return package's application information structure
     */
    public ApplicationInfo appinfo() {
        return this.pkgInfo.applicationInfo;
    }

    /**
     * Return the UID of this package.
     * @return package UID
     */
    public int uid() {
        return this.pkgInfo.applicationInfo.uid;
    }


}
