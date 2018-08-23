package com.ULock.util;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Abhijeet Gupta on 10/08/2017.
 */
//Class to store the app installation date and store it in a hidden directory folder
public class TrialCheck {

        File data = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/.aaa" + File.separator);
        File file = new File(data, "ulockconfig.txt");
        private long date = 0;

        public long readFile()
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                try {
                    date = Long.parseLong(br.readLine());
                    br.close();
                } catch (NumberFormatException | IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                try {
                    file.createNewFile();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                e.printStackTrace();
            }
            return date;
        }
//Method to write the app installation date
        public void writeFile(long date) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(String.valueOf(date));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Method to find the different between the current date and the app installation date
    public static int findDaysDiff(long unixStartTime,long unixEndTime)
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(unixStartTime);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(unixEndTime);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return (int) ((calendar2.getTimeInMillis()-calendar1.getTimeInMillis())/(24 * 60 * 60 * 1000));

    }
    //Method to get the date of the app
    public String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
    }

