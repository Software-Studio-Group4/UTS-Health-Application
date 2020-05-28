package uts.group4.UTShealth.Util;

import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/*************************************************************************************************
 * This class parses date and time from strings.
 * Strings supplied must be in the form of   00:00AM  and  DD/MM/YYYY
 ************************************************************************************************/

public class DATParser {
    final static private String logger = "DATPARSER";
    //empty constructor
    public void DATParser(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getWeekDay(String date){
        Calendar c = Calendar.getInstance();
        c.set(DATParser.getYear(date), DATParser.getMonthAsInt(date) - 1, DATParser.getDay(date), 1, 0 , 0);

        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int addMinutesHoursInt(int hoursToAdd, int minutesToAdd, int sourceTime){

        String stringTime = sourceTime + "";
        char[] timeCharArr = stringTime.toCharArray();
        int newHour;
        int newMinutes;


        switch(timeCharArr.length){
            case 1 :
                Log.i(logger, "case: " + timeCharArr.length);
                newHour = hoursToAdd;
                newMinutes = Integer.parseInt(timeCharArr[0] + "") + minutesToAdd;
                newHour += newMinutes/60;
                newMinutes = newMinutes%60;
                break;
            case 2 :
                Log.i(logger, "case: " + timeCharArr.length);
                newHour = hoursToAdd;
                newMinutes = Integer.parseInt(timeCharArr[0] + "" + timeCharArr[1] + "") + minutesToAdd;
                newHour += newMinutes/60;
                newMinutes = newMinutes%60;
                break;
            case 3 :
                Log.i(logger, "case: " + timeCharArr.length);
                newHour = Integer.parseInt(timeCharArr[0]+"") + hoursToAdd;
                newMinutes = Integer.parseInt(timeCharArr[1] + "" + timeCharArr[2] + "") + minutesToAdd;
                newHour += newMinutes/60;
                newMinutes = newMinutes%60;
                break;
            case 4 :
                newHour = Integer.parseInt(timeCharArr[0] + "" + timeCharArr[1] + "") + hoursToAdd;
                newMinutes = Integer.parseInt(timeCharArr[2] + "" + timeCharArr[3] + "") + minutesToAdd;
                newHour += newMinutes/60;
                newMinutes = newMinutes%60;
                break;
            default : Log.i(logger, "add minutes to hours invalid source time input "); return 0;
        }

        Log.i(logger, "source time: " + sourceTime +" new hour :" + newHour + " new minutes: " + newMinutes);
        return Integer.parseInt((newHour+""+newMinutes));
    }




    public static int timeStrToInt(String time){
        if(time.contains("AM") || time.contains("12:")){
            return Integer.parseInt(time.replaceAll( "[^\\d]", "" ));
        }
        else if(time.contains("PM")){
            return (1200 + Integer.parseInt(time.replaceAll( "[^\\d]", "" )));
        }
        return Integer.parseInt(time.replaceAll( "[^\\d]", ""));
    }

    public static String timeIntToStr(int time){
        String AMPM;
        int parsedTime;
        if(time < 1200){
            AMPM = "AM";
            parsedTime = time;
        }
        else{
            AMPM ="PM";
            parsedTime = time - 1200;
        }

        char[] chars = ("" + parsedTime).toCharArray();
        if(chars.length == 1){
            return ("00:0" + chars[0] + AMPM);
        }
        else if(chars.length == 2){
            return ("00:" + chars[0] + chars[1] + AMPM);
        }
        else if(chars.length == 3){
            return ("0" + chars[0] + ":" + chars[1] + chars[2] + AMPM);
        }
        else if(chars.length == 4){
            return (chars[0] + "" + chars[1] +":"+ chars[2] + "" + chars[3] + AMPM);
        }

        return "NOT A VALID TIME FORMAT";
    }

    public static String getMonthAsStr(String date){
        String[] stringArr = date.split("/");
        switch(Integer.parseInt(stringArr[1])){
            case 1 : return "January";
            case 2 : return "February";
            case 3 : return "March";
            case 4 : return "April";
            case 5 : return "May";
            case 6 : return "June";
            case 7 : return "July";
            case 8 : return "August";
            case 9 : return "September";
            case 10 : return "October";
            case 11 : return "November";
            case 12 : return "December";
            default : return "ERROR : NOT A VALID MONTH";
        }
    }

    public static int getDay(String date){
        String[] stringArr = date.split("/");
        return Integer.parseInt(stringArr[0]);
    }

    public static int getYear(String date){
        String[] stringArr = date.split("/");
        return Integer.parseInt(stringArr[2]);
    }

    public static int getMonthAsInt(String date){
        String[] stringArr = date.split("/");
        return Integer.parseInt(stringArr[1]);
    }

    public static int weekDayAsInt(String day){
        String weekDay = day.toLowerCase().trim();
        switch(weekDay){
            case"sunday": return 1;
            case"monday": return 2;
            case"tuesday": return 3;
            case"wednesday": return 4;
            case"thursday": return 5;
            case"friday": return 6;
            case"saturday": return 7;
            default: return 0;
        }
    }

    public static String weekDayAsString(int day){
        switch(day){
            case 1: return "Sunday";
            case 2: return "Monday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Thursday";
            case 6: return "Friday";
            case 7: return "Saturday";
            default: return "INVALID DAY";
        }
    }
}
