package com.wisdom.separate.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 日期处理类
 */
public class CalendarUtil {

	public static final String STANDARD_FMT = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 当前日期减1s，一般用在快速从当前日期的00:00:00到上一天23:59:59
	 */
	public static void minusOneSecond(Date date) {
		Long minusMillis = date.getTime() - 1000 ;
		date.setTime(minusMillis);
	}
	
	/**
	 * 获取date所在日的00:00:00
	 * @param date
	 * @return
	 */
	public static Date getStartTimeOfDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setCalendarToStartTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 获取日期所在的年的第一天
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();  
        return currYearFirst;  
	}
	
	/**
	 * 获取date所在日的23:59:59
	 * @param date
	 * @return
	 */
	public static Date getEndTimeOfDay(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setCalendarToEndTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 统计两个日期之间的天数
	 * @param date1: 较早的日期
	 * @param date2：较晚的日期
	 * @return
	 */
	public static Long countDays(Date date1, Date date2) {
		Date startTime1 = getStartTimeOfDate(date1);
		Date startTime2 = getStartTimeOfDate(date2);
		long days = (int) ((startTime2.getTime() - startTime1.getTime()) / (1000*3600*24));
		return days;
	}

	/**
	 * 加n天后的日期
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addDays(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, n);
		return cal.getTime();
	}
	
	/**
	 * 获取date所在周的周一的00:00:00
	 * @param date
	 * @return
	 */
	public static Date getStartTimeOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		setCalendarToStartTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 获取date所在周的周日的23:59:59
	 * @param date
	 * @return
	 */
	public static Date getEndTimeOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		setCalendarToEndTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 获取date所在月的起始时间
	 * @param date
	 * @return
	 */
	public static Date getStartTimeOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setCalendarToStartTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 获取date所在月的结束时间
	 * @param date
	 * @return
	 */
	public static Date getEndTimeOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		setCalendarToEndTime(cal);
		return cal.getTime();
	}
	
	/**
	 * 获取date所在的月份
	 * @param date
	 * @return
	 */
	public static Integer getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		return cal.get(Calendar.MONTH);
	}

	/**
	 * 获取给定日期所在的周一
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date);
		int delta = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
        	delta = -6;
        } else {
        	delta = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, delta);
        return cal.getTime();
	}
	
	/**
	 * 获取给定日期所在的周日
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}
		cal.add(Calendar.DATE, - dayOfWeek + 7);
		return cal.getTime();
	}
	
	/**
	 * 计算两个日期之间的周数，按自然周计算，第1周的周六和第3周的周一，差2周，而不是一周
	 * @param date1	: 较早的日期
	 * @param date2	: 靠近现在的日期
	 * @return
	 */
	public static Integer countWeeks(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setFirstDayOfWeek(Calendar.MONDAY);
		c2.setFirstDayOfWeek(Calendar.MONDAY);
		c1.setTime(date1);
		c2.setTime(date2);
		
		Date date11 = getFirstDayOfWeek(date1);
		Date date22 = getFirstDayOfWeek(date2);
		Long weeks = (date22.getTime() - date11.getTime()) / (7*24*60*60*1000);
		return weeks.intValue();
	}
	
	/**
	 * 加n周后的日期
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addWeeks(Date date, Integer n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.WEEK_OF_YEAR, n);
		return cal.getTime();
	}
	
	/**
	 * 计算两个日期之间的月份
	 * @param date1	: 较早的日期
	 * @param date2	: 靠近现在的日期
	 * @return
	 */
	public static int countMonths(Date date1, Date date2) {

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (year < 0) {
			year = -year;
			return year * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		}
		return year * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
	}
	
	/**
	 * 加n月后的日期
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addMonth(Date date, Integer n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

    /** 
     * 取得月最后一天 
     *  
     * @param date 
     * @return 
     */  
    public static Date getLastDateOfMonth(Date date) {  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));  
        return c.getTime();  
    }  
	
    /** 
     * 取得月第一天 
     *  
     * @param date 
     * @return 
     */  
    public static Date getFirstDateOfMonth(Date date) {  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));  
        return c.getTime();  
    }  
    
    /** 
     * 取得季度第一天 
     *  
     * @param date 
     * @return 
     */  
    public static Date getFirstDateOfQuarter(Date date) {  
        return getFirstDateOfMonth(getQuarterDate(date)[0]);  
    }  
  
    /** 
     * 取得季度最后一天 
     *  
     * @param date 
     * @return 
     */  
    public static Date getLastDateOfQuarter(Date date) {  
        return getLastDateOfMonth(getQuarterDate(date)[2]);  
    } 
	
	/** 
     * 取得季度月 
     *  
     * @param date 
     * @return 
     */  
    public static Date[] getQuarterDate(Date date) {  
        Date[] quarter = new Date[3];  
  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
  
        int nQuarter = getQuarter(date);  
        if (nQuarter == 1) {// 第一季度  
            c.set(Calendar.MONTH, Calendar.JANUARY);  
            quarter[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.FEBRUARY);  
            quarter[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.MARCH);  
            quarter[2] = c.getTime();  
        } else if (nQuarter == 2) {// 第二季度  
            c.set(Calendar.MONTH, Calendar.APRIL);  
            quarter[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.MAY);  
            quarter[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.JUNE);  
            quarter[2] = c.getTime();  
        } else if (nQuarter == 3) {// 第三季度  
            c.set(Calendar.MONTH, Calendar.JULY);  
            quarter[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.AUGUST);  
            quarter[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.SEPTEMBER);  
            quarter[2] = c.getTime();  
        } else if (nQuarter == 4) {// 第四季度  
            c.set(Calendar.MONTH, Calendar.OCTOBER);  
            quarter[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.NOVEMBER);  
            quarter[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.DECEMBER);  
            quarter[2] = c.getTime();  
        }  
        return quarter;  
    }
    
    /** 
     * 获取季度 
     * @param date	1 第一季度 2 第二季度 3 第三季度 4 第四季度 
     * @return 
     */  
    public static Integer getQuarter(Date date) {  
    					 
        int quarter = 0;  
  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        int month = c.get(Calendar.MONTH);  
        switch (month) {  
        case Calendar.JANUARY:  
        case Calendar.FEBRUARY:  
        case Calendar.MARCH:  
        	quarter = 1;  
            break;  
        case Calendar.APRIL:  
        case Calendar.MAY:  
        case Calendar.JUNE:  
        	quarter = 2;  
            break;  
        case Calendar.JULY:  
        case Calendar.AUGUST:  
        case Calendar.SEPTEMBER:  
        	quarter = 3;  
            break;  
        case Calendar.OCTOBER:  
        case Calendar.NOVEMBER:  
        case Calendar.DECEMBER:  
        	quarter = 4;  
            break;  
        default:  
            break;  
        }  
        return quarter;  
    }  
    
    /**
     * 加n个季度之后的时间，只是季度加，举例：如果现在是九月，下一个季度就是十月，而不是加三个月。
     * @param startupDate
     * @param n
     * @return
     */
	public static Date addQuarter(Date date, int n) {
		Date firstDay = CalendarUtil.getStartTimeOfDate(getFirstDateOfQuarter(date));	// 获取当前季度的第一天的00:00:00
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDay);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.MONTH, n*3);
		return cal.getTime();
	}
	
	/**
	 * 两个日期之间一共有多少个季度
	 * @param date1	: 较早的日期
	 * @param date2	: 靠近现在的日期
	 * @return
	 */
	public static Integer countQuarters(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		Date date1QuarterFirstDay = CalendarUtil.getFirstDateOfQuarter(date1);
		Date date2QuarterFirstDay = CalendarUtil.getFirstDateOfQuarter(date2);
		Integer months = CalendarUtil.countMonths(date1QuarterFirstDay, date2QuarterFirstDay);
		Integer quarters = months/3;
		return quarters;
	}
    
	/**
	 * 两个日期之间一共有多少年
	 * @param date1	: 较早的日期
	 * @param date2	: 靠近现在的日期
	 * @return
	 */
	public static Integer countYears(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(date1);
		c2.setTime(date2);

		Integer year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		
		return year;
	}
	/**
	 * 在date上加n年
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addYears(Date date, int n) {
		Date firstDay = CalendarUtil.getStartTimeOfDate(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDay);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.YEAR, n);
		return cal.getTime();
	}
	
	/**
	 * date是否在startDate和endDate区间内
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Boolean between(Date date, Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		Long startDateMills = startDate.getTime();
		Long endDateMills = endDate.getTime();
		Long dateMills = date.getTime();
		if (dateMills >= startDateMills && dateMills <= endDateMills) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * date1 早于 date2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Boolean earlyThan(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		if (date1.getTime() < date2.getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * date1 迟于 date2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Boolean lateThan(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return null;
		}
		if (date1.getTime() > date2.getTime()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 标准的日期格式化
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		DateFormat df = new SimpleDateFormat(STANDARD_FMT);
		return df.format(date);
	}
	
	/**
	 * 日期格式化
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	private static void setCalendarToStartTime(Calendar cal) {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		// 时分秒（毫秒数）
		long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
		// 凌晨00:00:00
		cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);
	}
	
	private static void setCalendarToEndTime(Calendar cal) {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		// 时分秒（毫秒数）
		long millisecond = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000;
		// 凌晨00:00:00
		cal.setTimeInMillis(cal.getTimeInMillis() - millisecond);
		// 凌晨23:59:59
		cal.setTimeInMillis(cal.getTimeInMillis() + 23 * 60 * 60 * 1000 + 59 * 60 * 1000 + 59 * 1000);
	}
	

	public static Date halfHourLater(Date date){
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE,30);
		return cal.getTime();
	}
}
