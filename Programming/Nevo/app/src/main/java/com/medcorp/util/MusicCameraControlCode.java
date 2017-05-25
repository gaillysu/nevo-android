package com.medcorp.util;

/**
 * Created by med on 17/5/24.
 */

public enum MusicCameraControlCode {
    MusicPreviousSong(0),
    MusicNextSong(1),
    MusicPause(2),
    MusicPlay(3),
    SoundUp(4),
    SoundDown(5),
    CameraSnapshot(6),
    CameraBurst(7),
    CameraZoomin(8),
    CameraZoomout(9),
    RejectCall(10),
    AcceptCall(11),
    LEDsOn(12);
    private int code;
    MusicCameraControlCode(int code) {this.code = code;}
    public  int rawValue() {return code;}
}
