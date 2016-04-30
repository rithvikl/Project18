package tcss450.uw.edu.project18;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Mindy on 4/20/2016.
 */
public class Driver {

    public static final boolean DEBUG = true;

    private Driver(){};

    /**
     * http://www.regexplanet.com/advanced/java/index.html
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
     * http://www.dreamincode.net/forums/topic/14886-date-validation-using-simpledateformat/
     * Checks to see if a date is valid from three Strings.
     * @param day is the day of the month. 1 <= day <= 31
     * @param month is the month in the year. 1 <= month <= 12
     * @param year is the year. year <= 2016 (this year)
     * @return the date in MM/DD/YYYY format if valid,
     *          "error" otherwise.
     */
    public static String isValidDate(String day, String month, String year) {
        String format = "MM/DD/YYYY";
        String date = month + "/" + day + "/" + year;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date test = null;
        try {
            test = sdf.parse(date);
        } catch (ParseException e) {
            return "error";
        }
        if (!sdf.format(test).equals(date))
            return "error";
        return date;
    }

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
                return "Password does not match or is empty.";
        }
        if (pass1.length() < 6) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return "Password must be at least 6 characters in length.";
            else return ret;
        }

        if (!upcase.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return "Password must contain at least 1 uppercase letter.";
            else return ret;
        }

        if (!lcase.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return "Password must contain at least 1 lowercase letter.";
            else return ret;
        }
        if (!num.matcher(pass1).find()) {
            if (type.equals(LoginActivity.PROFILE_NEW))
                return "Password must contain at least 1 number.";
            else return ret;
        }
        return "success";
    }

    /**
     * Assumes that the date is in format MM/DD/YYYY
     * @param date is the user's birthday.
     * @return an array of Strings where
     *          [0] = day
     *          [1] = month
     *          [2] = year
     */
    public static String[] parseDate(String date) throws ParseException{
        String[] ret = new String[3];
        int i1 = date.indexOf('/');
        int i2 = -1;
        if (i1 >= 0) i2 = date.indexOf('/', i1);
        else throw new ParseException("Wrong format.", 0);
        if (i2 >= 0) {
            ret[0] = date.substring(0,i1);
            ret[1] = date.substring(i1+1,i2);
            ret[2] = date.substring(i2+1);
        } else throw new ParseException("Wrong format.", 0);
        return ret;
    }

}
