package ahto.yellowduck;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class AlarmList implements Parcelable {

    private long alarmtime;
    private int hasAlarm;
    private int requestCode;
    private long snoozeTime;
    private int isAlarming;
    private int hasVib;
    private int soundVolume;
    private String music;
    private int snoozeMinute;
    private ArrayList<Integer> alarmDay = new ArrayList<>();
    private int hour;
    private int minute;
    private int day;
    private int repeat;
    private int repeatsleft;
    private String number;
    private String SMS;
    private int sendSMS;
    private String SMSn;
    private String musicN;

    public long getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(long alarmtime) {
        this.alarmtime = alarmtime;
    }

    public int getHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(int hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getSnoozeTime() {
        return snoozeTime;
    }

    public void setSnoozeTime(long snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    public int getIsAlarming() {
        return isAlarming;
    }

    public void setIsAlarming(int isAlarming) {
        this.isAlarming = isAlarming;
    }

    public int getHasVib() {
        return hasVib;
    }

    public void setHasVib(int hasVib) {
        this.hasVib = hasVib;
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public int getSnoozeMinute() {
        return snoozeMinute;
    }

    public void setSnoozeMinute(int snoozeMinute) {
        this.snoozeMinute = snoozeMinute;
    }

    public ArrayList<Integer> getAlarmDay() {
        return alarmDay;
    }

    public void setAlarmDay(ArrayList<Integer> alarmDay) {
        this.alarmDay = alarmDay;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeatsleft() {
        return repeatsleft;
    }

    public void setRepeatsleft(int repeatsleft) {
        this.repeatsleft = repeatsleft;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSMS() {
        return SMS;
    }

    public void setSMS(String SMS) {
        this.SMS = SMS;
    }

    public int getSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(int sendSMS) {
        this.sendSMS = sendSMS;
    }

    public String getSMSn() {
        return SMSn;
    }

    public void setSMSn(String SMSn) {
        this.SMSn = SMSn;
    }

    public String getMusicN(){
        return musicN;
    }

    public AlarmList(long alarmtime, int hasAlarm, int requestCode, long snoozeTime,
                     int isAlarming, int hasVib, int soundVolume, String music, int snoozeMinute,
                     int hour, int minute, int day, int repeat, String number, String SMS, int sendSMS, String SMSName, String musicN){
        this.alarmtime = alarmtime;
        this.hasAlarm = hasAlarm;
        this.requestCode = requestCode;
        this.snoozeTime = snoozeTime;
        this.isAlarming = isAlarming;
        this.hasVib = hasVib;
        this.soundVolume = soundVolume;
        this.music = music;
        this.snoozeMinute = snoozeMinute;
        this.hour = hour;
        this.minute = minute;
        this.day = day;
        this.repeat = repeat;
        this.number = number;
        this.SMS = SMS;
        this.sendSMS = sendSMS;
        this.SMSn = SMSName;
        this.musicN = musicN;
    }


    public String getMusic() {
        return music;
    }

    private AlarmList(Parcel in){
        alarmtime = in.readLong();
        hasAlarm = in.readInt();
        requestCode = in.readInt();
        snoozeTime = in.readLong();
        isAlarming = in.readInt();
        hasVib = in.readInt();
        soundVolume = in.readInt();
        music = in.readString();
        snoozeMinute = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        day = in.readInt();
        repeat = in.readInt();
        number = in.readString();
        SMS = in.readString();
        sendSMS = in.readInt();
        SMSn = in.readString();
        musicN = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(this.alarmtime);
        dest.writeInt(this.hasAlarm);
        dest.writeInt(this.requestCode);
        dest.writeLong(this.snoozeTime);
        dest.writeInt(this.isAlarming);
        dest.writeInt(this.hasVib);
        dest.writeInt(this.soundVolume);
        dest.writeString(this.music);
        dest.writeInt(this.snoozeMinute);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
        dest.writeInt(this.day);
        dest.writeInt(this.repeat);
        dest.writeString(this.number);
        dest.writeString(this.SMS);
        dest.writeInt(this.sendSMS);
        dest.writeString(this.SMSn);
        dest.writeString(this.musicN);
    }

    public static final Parcelable.Creator<AlarmList> CREATOR = new Parcelable.Creator<AlarmList>(){

        @Override
        public AlarmList createFromParcel(Parcel in) {
            return new AlarmList(in);
        }

        @Override
        public AlarmList[] newArray(int size) {
            return new AlarmList[size];
        }
    };
}
