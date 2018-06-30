package com.example.therick.journalapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class JournalAdaptor extends ArrayAdapter <JournalEntry> {
    private Activity context;
    List<JournalEntry> EntryList;

    public JournalAdaptor(Activity context, List<JournalEntry> EntryList){
        super(context,R.layout.journal_ist_item,EntryList);
       this.context = context;
       this.EntryList = EntryList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.journal_ist_item,null,true);
        TextView tvEntry = v.findViewById(R.id.vEntry);
        TextView tvDate = v.findViewById(R.id.vDate);
        TextView tvTime = v.findViewById(R.id.vTime);

        JournalEntry journalEntry = EntryList.get(position);
        tvEntry.setText(journalEntry.getEntry());
        tvDate.setText(journalEntry.getDate());
        tvTime.setText(journalEntry.getTime());
        return v;
    }
}
