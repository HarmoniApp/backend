package org.harmoniapp.utils;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating holidays and working days.
 */
public class HolidayCalculator {

    /**
     * Calculates the date of Easter for a given year.
     * The calculation is based on the algorithm known as Computus.
     *
     * @param year the year for which to calculate the date of Easter
     * @return the LocalDate representing the date of Easter in the given year
     */
    public static LocalDate calculateEaster(int year) {
        int G = year % 19;
        int C = year / 100;
        int H = (C - C / 4 - (8 * C + 13) / 25 + 19 * G + 15) % 30;
        int I = H - (H / 28) * (1 - (H / 29) * ((21 - G) / 11));
        int J = (year + year / 4 + I + 2 - C + C / 4) % 7;
        int L = I - J;
        int month = 3 + (L + 40) / 44;
        int day = L + 28 - 31 * (month / 4);

        return LocalDate.of(year, month, day);
    }

    /**
     * Calculates the number of working days between two dates.
     * The calculation excludes weekends and public holidays.
     *
     * @param start the start date (inclusive)
     * @param end   the end date (inclusive)
     * @return the number of working days between the two dates
     */
    public static Long calculateWorkingDays(LocalDate start, LocalDate end) {
        List<LocalDate> holidays = getHolidaysForYear(LocalDate.now().getYear());

        long workingDays = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWorkingDay(date, holidays)) {
                workingDays++;
            }
        }

        return workingDays;
    }

    /**
     * Returns a list of public holidays for a given year.
     *
     * @param year the year for which to get the public holidays
     * @return a list of LocalDate objects representing the public holidays
     */
    private static List<LocalDate> getHolidaysForYear(int year) {
        List<LocalDate> holidays = new ArrayList<>();

        holidays.add(LocalDate.of(year, Month.JANUARY, 1));   // New Year's Day
        holidays.add(LocalDate.of(year, Month.JANUARY, 6));   // Epiphany
        holidays.add(LocalDate.of(year, Month.MAY, 1));       // Labor Day
        holidays.add(LocalDate.of(year, Month.MAY, 3));       // Constitution Day (May 3rd)
        holidays.add(LocalDate.of(year, Month.AUGUST, 15));   // Assumption of Mary
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 1));  // All Saints' Day
        holidays.add(LocalDate.of(year, Month.NOVEMBER, 11)); // Independence Day
        holidays.add(LocalDate.of(year, Month.DECEMBER, 25)); // Christmas Day
        holidays.add(LocalDate.of(year, Month.DECEMBER, 26)); // Second Day of Christmas

        // Calculate Easter, Easter Monday, and Corpus Christi
        LocalDate easter = calculateEaster(year);
        holidays.add(easter);
        holidays.add(easter.plusDays(1));  // Easter Monday
        holidays.add(easter.plusDays(60)); // Corpus Christi

        return holidays;
    }

    /**
     * Checks if a given date is a working day.
     * A working day is defined as a weekday (Monday to Friday) that is not a public holiday.
     *
     * @param date     the date to check
     * @param holidays the list of public holidays
     * @return true if the date is a working day, false otherwise
     */
    private static boolean isWorkingDay(LocalDate date, List<LocalDate> holidays) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek != 6 && dayOfWeek != 7 && !holidays.contains(date);
    }
}

