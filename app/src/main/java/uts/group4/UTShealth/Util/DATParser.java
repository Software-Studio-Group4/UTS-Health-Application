package uts.group4.UTShealth.Util;

/*************************************************************************************************
 * This class parses date and time from strings.
 * Strings supplied must be in the form of   00:00AM  and  DD/MM/YYYY
 ************************************************************************************************/

public class DATParser {
    //empty constructor
    public void DATParser(){

    }

    public static int timeStrToInt(String time){
        if(time.contains("AM") || time.contains("12")){
            return Integer.parseInt(time.replaceAll( "[^\\d]", "" ));
        }
        else if(time.contains("PM")){
            return (1200 + Integer.parseInt(time.replaceAll( "[^\\d]", "" )));
        }
        return Integer.parseInt(time.replaceAll( "[^\\d]", ""));
    }

    public static String timeIntToStr(int time){
        char[] chars = ("" + time).toCharArray();
        if(chars.length == 1){
            return ("00:0" + chars[0] + "AM");
        }
        else if(chars.length == 2){
            return ("00:" + chars[0] + chars[1] + "AM");
        }
        else if(chars.length == 3){
            return ("0" + chars[0] + ":" + chars[1] + chars[2] + "AM");
        }
        else if(time < 1200 && chars.length == 4){
            return (chars[0] + chars[1] +":"+ chars[2] + chars[3] + "AM");
        }
        else if(time >= 1200 && chars.length == 4){
            return (chars[0] + chars[1] +":"+ chars[2] + chars[3] + "PM");
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
}
