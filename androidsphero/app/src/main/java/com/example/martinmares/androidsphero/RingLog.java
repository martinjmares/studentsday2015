package com.example.martinmares.androidsphero;

import android.os.Handler;
import android.widget.TextView;

/**
 */
public class RingLog {

    private final Handler handler = new Handler();
    private final TextView view;
    private final String[] log;
    private int pointer = 0;
    private int size = 0;
    private int totalSize = 0;

    public RingLog(TextView view, int size) {
        this.view = view;
        log = new String[size];
    }

    public synchronized void log(String str) {
        if (str == null) {
            str = "";
        }
        log[pointer] = str;
        pointer++;
        if (pointer >= log.length) {
            pointer = 0;
        }
        if (size < log.length) {
            size++;
        }
        totalSize++;
        updateView();
    }

    private void updateView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.setText("--------- " + totalSize + "--------- ");
                for (int i = pointer; i < size; i++) {
                    view.append("\n");
                    view.append(log[i]);
                }
                for (int i = 0; i < pointer; i++) {
                    view.append("\n");
                    view.append(log[i]);
                }
            }
        });

    }
}
