package com.medcorp.nevo.fragment.listener;

/**
 * Created by gaillysu on 15/12/25.
 */
public interface OnStateListener {
    public enum STATE {
        STATE_SEARCHING,STATE_SEARCH_FAILURE,STATE_SEARCH_SUCCESS,STATE_CONNECTING,STATE_DISCONNECT,STATE_CONNECTED,STATE_SYNC_START,STATE_SYNC_END
    }
    public void onStateChanged(STATE state);
}
