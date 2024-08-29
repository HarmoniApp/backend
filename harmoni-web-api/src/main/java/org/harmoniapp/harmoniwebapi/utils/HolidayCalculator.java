package org.harmoniapp.harmoniwebapi.utils;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

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
     * Working days are defined as weekdays (Monday to Friday) excluding public holidays.
     * Public holidays include fixed dates and movable feasts like Easter, Easter Monday, and Corpus Christi.
     *
     * @param start the start date of the period
     * @param end   the end date of the period
     * @return the number of working days between the start and end dates
     */
    public static Long calculateWorkingDays(LocalDate start, LocalDate end) {
        int currentYear = LocalDate.now().getYear();
        List<LocalDate> holidays = new ArrayList<>();

        holidays.add(LocalDate.of(currentYear, Month.JANUARY, 1));   // New Year's Day
        holidays.add(LocalDate.of(currentYear, Month.MAY, 1));       // Labor Day
        holidays.add(LocalDate.of(currentYear, Month.MAY, 3));       // Constitution Day (May 3rd)
        holidays.add(LocalDate.of(currentYear, Month.AUGUST, 15));   // Assumption of Mary
        holidays.add(LocalDate.of(currentYear, Month.NOVEMBER, 1));  // All Saints' Day
        holidays.add(LocalDate.of(currentYear, Month.NOVEMBER, 11)); // Independence Day
        holidays.add(LocalDate.of(currentYear, Month.DECEMBER, 25)); // Christmas Day
        holidays.add(LocalDate.of(currentYear, Month.DECEMBER, 26)); // Second Day of Christmas

        // Calculate Easter, Easter Monday, and Corpus Christi
        LocalDate easter = calculateEaster(currentYear);
        LocalDate easterMonday = easter.plusDays(1);
        LocalDate corpusChristi = easter.plusDays(60);

        holidays.add(easter);
        holidays.add(easterMonday);
        holidays.add(corpusChristi);

        long totalDays = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int dayOfWeek = date.getDayOfWeek().getValue();

            if (dayOfWeek != 6 && dayOfWeek != 7) {
                if (!holidays.contains(date)) {
                    totalDays++;
                }
            }
        }

        return totalDays;
    }
}

