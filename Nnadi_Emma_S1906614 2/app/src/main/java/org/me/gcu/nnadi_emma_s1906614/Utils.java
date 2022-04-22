package org.me.gcu.nnadi_emma_s1906614;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static Date changePudDate(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return changeStringIntoDate(str);
    }

    public static Date changeStringIntoDate(String date) {
        Date result = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            result = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Date stringToDate(String strDate) {
        if (strDate == null)
            return null;
        //Wed, 06 Apr 2022 10:53:19 GMT
        // change the date format whatever you have used in your model class.
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
