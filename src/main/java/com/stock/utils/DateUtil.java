package com.stock.utils;

import java.util.Calendar;

public class DateUtil {

	/**
	 * 
	 * @param days - number of days to shift the current date
	 * @return
	 */
	public static String getShiftedDate(int days) {
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DAY_OF_MONTH, days);
		
		int startDay = cal.get(Calendar.DAY_OF_MONTH); //  System.out.println("startDay=" + startDay);
	    int startMonth = cal.get(Calendar.MONTH);      //  System.out.println("startMonth=" + startMonth);
	    int startYear = cal.get(Calendar.YEAR);        //  System.out.println("startYear=" + startYear);
	    
	    String mMonth = getMySqlMonthNumber(startMonth);

	    String viewStartDay = "" + startDay;
	    if(startDay <10) {
	    	viewStartDay = "0" + startDay;
	    }
	    return "" + startYear + "-" + mMonth + "-" + viewStartDay;
	}
	
	
	/**
	 * Converting Java month number into normal month nmber used in MySql db
	 * i.e. in Java 0 is a January, MySql 1 is January.
	 * @param month
	 * @return normal month number as a string with preciding 0.
	 */
	public static String getMySqlMonthNumber(int month) {

		if (month < 0 && month > 11) {
			return "Invalid month";
		}

		String monthString;
		switch (month) {
		case 0:
			monthString = "01";
			break;
		case 1:
			monthString = "02";
			break;
		case 2:
			monthString = "03";
			break;
		case 3:
			monthString = "04";
			break;
		case 4:
			monthString = "05";
			break;
		case 5:
			monthString = "06";
			break;
		case 6:
			monthString = "07";
			break;
		case 7:
			monthString = "08";
			break;
		case 8:
			monthString = "09";
			break;
		case 9:
			monthString = "10";
			break;
		case 10:
			monthString = "11";
			break;
		case 11:
			monthString = "12";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
	}
}
