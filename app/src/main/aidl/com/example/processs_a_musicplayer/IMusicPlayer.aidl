// IMusicPlayer.aidl
package com.example.processs_a_musicplayer;

// Declare any non-default types here with import statements
import com.example.processs_a_musicplayer.IClientCallback;

interface IMusicPlayer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */


    void start();

    void stop();

    boolean getPlayerStatus();

    void registerCallback(IClientCallback callback);
    void unregisterCallback(IClientCallback callback);
}