package com.hsae.autosdk.dvr;

/**
 * Created by FlyZebra on 2018/6/6.
 * Descrip:
 */

interface IDvrTyListener {
    /** state
     0x00 = initial status
     0x01 = general recording mode
     0x02 = stop recording mode
     0x03 = manually start  emergency video mode
     0x04 = auto start emergency video mode
     0x05 = power off status
     0x06 = system failure
     0x07 = invalid
     **/
    void notityWorkStatus(int state);

    /** state
     0x00: initial status
     0x01: In connection
     0x02: Connection Fail
     0x03: STA Mode, Connection Successful
     0x04: MAC Data error
     0x05: Password Data error
     0x06~0x07: invalid
     **/
    void notityLinkStatus(int state);

    /** state
     0x00 = initial status
     0x01 = Need to update DSP software
     0x02 = Need to update MCU software
     0x03 = Need to update DSP and MCU
     **/
    void notityUpdateNotify(int state);

    /**state
     0b = Not Respond
     1b = Photo taken
     **/
    void notityTakePhotoRespond(int state);

    /**state
     0000000b~1100100b = 0%~100%
     1100101b~1111100b = reserved
     1111101b = Upgrade Failed
     1111110b = Update Successful
     1111111b = initial status
     **/
    void notityUpdateSchedule(int state);

    /**state
     0x00 = SD insert
     0x01 = SD pull out
     0x02 = SD failure
     0x03 = SD full
     0x04 = emergency video file full
     0x05 = photo file full
     0x06 = emergency video file and photo file full
     0x07 = write protection
     **/
    void notitySDCardStatus(int state);
}
