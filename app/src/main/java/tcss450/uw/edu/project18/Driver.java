package tcss450.uw.edu.project18;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * This class holds miscellaneous functions for concurrency
 * across activities and fragments.
 * Created by Mindy on 4/20/2016.
 */
public class Driver {

    /**
     * Debug mode displays far more error messages.
     */
    public static final boolean DEBUG = true;

    /**
     * Private constructor.
     */
    private Driver(){};

    /**
     * http://www.regexplanet.com/advanced/java/index.html
     * This checks if the email string is in a standard format.
     * It expects the domain to be all letter characters.
     * @param email is the user's email; also used as their username.
     * @return true if the email is in standard format,
     *          false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email.isEmpty()) return false;
        if (!email.matches("\\S+@\\w+.\\D{3}"))
            return false;
        return true;
    }

    /**
     * Checks to see if a date is valid from three Strings.
     * @param day is the day of the month. 1 <= day <= 31
     * @param month is the month in the year. 1 <= month <= 12
     * @param year is the year. year <= 2016 (this year)
     * @return the date in MM/DD/YYYY format if valid.
     * @throws IllegalArgumentException
     */
    public static String isValidDate(String day, String month, String year)
        throws IllegalArgumentException {
        String ret = "error";
        int[] days;
        int m = Integer.parseInt(month);
        if (m > 12 || m < 1) throw new IllegalArgumentException("Month");
        int y = Integer.parseInt(year);
        if (y < 1900 || y > Calendar.getInstance().get(Calendar.YEAR))
            throw new IllegalArgumentException("Year");
        if (isLeapYear(y)) {
            days = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        } else {
            days = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        }
        int d = Integer.parseInt(day);
        if (d < 1 || d > days[m-1]) throw new IllegalArgumentException("Day");
        return month + "/" + day + "/" + year;
    }

    public static boolean isValidDate(String date) {
        try {
            int[] date_array = getValueOfDate(date);
            if(date_array[1] > 12 || date_array[1] < 1)
                throw new ParseException("Incorrent month: " + date_array[1], 1);
            if (date_array[0] < 1900 || date_array[0] > Calendar.getInstance().get(Calendar.YEAR))
                throw new ParseException("Year out of bounds: " + date_array[0], 2);
            int[] days;
            if (isLeapYear(date_array[0]))
                days = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            else days = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if (date_array[2] < 1 || date_array[2] > days[date_array[1]-1])
                throw new ParseException("Incorrect day: " + date_array[2], 3);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Determines if the given value is a leap year.
     * @param y is the year.
     * @return true if it is a leap year,
     *          false otherwise.
     */
    public static boolean isLeapYear(int y) {
        if (y % 4 != 0) return false;
        if (y % 400 == 0) return true;
        if (y % 100 == 0) return false;
        return true;
    }


    public static final String PWD_ERROR_8CHAR = "Password must be at least 8 characters in length.";
    public static final String PWD_ERROR_MATCH = "Password does not match or is empty.";
    public static final String PWD_ERROR_UPPER = "Password must contain at least 1 uppercase letter.";
    public static final String PWD_ERROR_LOWER = "Password must contain at least 1 lowercase letter.";
    public static final String PWD_ERROR_NUM = "Password must contain at least 1 number.";
    /**
     * http://codereview.stackexchange.com/questions/63283/password-validation-in-java
     * Checks to see if the password is valid.
     * @param type is the type of check. The validation will be different if the user
     *             is logging in versus creating a profile. Use LoginActivity.PROFILE_NEW
     *             for adding a profile.
     * @param pass1 is the user's password. This is needed for both checks.
     * @param pass2 is the confimation password. This is only needed if the type is
     *              other than new.
     * @return "success" if the password is valid,
     *          an error message otherwise.
     */
    public static String isValidPassword(String type, String pass1, String pass2) {
        Pattern upcase = Pattern.compile("[A-Z]");
        Pattern lcase = Pattern.compile("[a-z]");
        Pattern num = Pattern.compile("[0-9]");
        String ret = "Invalid password.";
        if (type.equals(LoginActivity.PROFILE_NEW)) {
            if (pass1.isEmpty() || pass2.isEmpty() ||
                    !pass1.equals(pass2))
                return PWD_ERROR_MATCH;
        }
        if (pass1.length() < 8) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return PWD_ERROR_8CHAR;
            else return ret;
        }

        if (!upcase.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return PWD_ERROR_UPPER;
            else return ret;
        }

        if (!lcase.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return PWD_ERROR_LOWER;
            else return ret;
        }
        if (!num.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return PWD_ERROR_NUM;
            else return ret;
        }
        return "success";
    }

    public static String parseDateForDB(int year, int month, int day) {
        String str = "";
        str += String.valueOf(year);
        str += month < 10 ? "0" + month : String.valueOf(month);
        str += day < 10 ? "0" + day : String.valueOf(day);
        return str;
    }

    /**
     * Formats a date string in format YYYYMMDD to a legible date in format
     * Month Day, Year
     * @param date is the String date in format YYYYMMDD
     * @return a String that is a more legible date.
     * @throws ParseException if the date is in the wrong format.
     */
    public static String parseDateForDisplay(String date) throws ParseException{
        String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October",
            "November", "December"};
        try {
            //get 4 chars for year
            String year = date.substring(0, 4);
            String month = months[Integer.parseInt(date.substring(4, 6))-1];
            String day = date.substring(6);
            return month + " " + day + ", " + year;
        } catch (Exception e) {
            throw new ParseException("Could not reformat date string.", 0);
        }
    }

    /**
     * Verifies and returns a date in YYYYMMDD format in an integer array.
     * @param date is the date string to convert.
     * @return an integer array representing 0: year; 1: month, 2: day
     * @throws ParseException if in the wrong format.
     */
    public static int[] getValueOfDate(String date) throws ParseException {
        int[] vals = new int[3];
        try {
            vals[0] = Integer.valueOf(date.substring(0, 4));
            vals[1] = Integer.valueOf(date.substring(4, 6));
            vals[2] = Integer.valueOf(date.substring(6));
        } catch (Exception e) {
            throw new ParseException("Could not convert date string.", 0);
        }
        return vals;
    }

    public static String parseDateForDisplay(int year, int month, int day) throws ParseException{
        return parseDateForDisplay(parseDateForDB(year,month,day));
    }

    public static boolean networkConnectionExists(Context context, Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            Toast.makeText(context, "No network connection available. Cannot authenticate user", Toast.LENGTH_SHORT) .show();
            return false;
        }
    }

    public static String cleanString(String s) {
        s = s.replaceAll("'", "");
        s = s.replaceAll("$", "");
        s = s.replaceAll("\"", "");
        Log.i("CLEANSTRING", "Clean: " + s);
        return s;
    }
}
