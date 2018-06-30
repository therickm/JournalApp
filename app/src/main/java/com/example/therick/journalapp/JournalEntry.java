package com.example.therick.journalapp;

public class JournalEntry {
    private String mName;
    private String mEntry;
    private String mDate;
    private String mTime;
    private String mUid;

    public JournalEntry() {}  // Needed for Firebase

    public JournalEntry(String name, String entry, String date, String time, String uid) {
        mName = name;
        mEntry = entry;
        mDate = date;
        mTime = time;
        mUid = uid;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public String getEntry() { return mEntry; }

    public void setEntry(String entry) { mEntry = entry; }

    public String getDate() { return mDate; }

    public void setDate(String date) { mDate = date; }

    public String getTime() { return mTime; }

    public void setTime(String time) { mTime = time; }

    public String getUid() { return mUid; }

    public void setUid(String uid) { mUid = uid; }
}
