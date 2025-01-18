package org.harmoniapp.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class HolidayCalculatorTest {

    @Test
    public void calculateEasterTest() {
        LocalDate easter2023 = HolidayCalculator.calculateEaster(2023);
        assertEquals(LocalDate.of(2023, Month.APRIL, 9), easter2023);
    }

    @Test
    public void calculateWorkingDaysTest() {
        LocalDate start = LocalDate.of(2023, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(2023, Month.JANUARY, 10);
        Long workingDays = HolidayCalculator.calculateWorkingDays(start, end);
        assertEquals(7, workingDays);
    }
}