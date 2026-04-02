package org.me.gcu.dramwise.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class UnitsCalculatorTest {

    @Test
    public void standardPint_returnsCorrectUnits() {
        // 568ml pint at 5% ABV = 2.84 units
        double result = UnitsCalculator.calculateUnits(568, 5.0);
        assertEquals(2.84, result, 0.001);
    }

    @Test
    public void guinnessPint_returnsCorrectUnits() {
        // 568ml at 4.2% ABV = 2.3856 units
        double result = UnitsCalculator.calculateUnits(568, 4.2);
        assertEquals(2.3856, result, 0.001);
    }

    @Test
    public void coronaBottle_returnsCorrectUnits() {
        // 355ml at 4.6% ABV = 1.633 units
        double result = UnitsCalculator.calculateUnits(355, 4.6);
        assertEquals(1.633, result, 0.001);
    }

    @Test
    public void peroniBottle_returnsCorrectUnits() {
        // 330ml at 5.1% ABV = 1.683 units
        double result = UnitsCalculator.calculateUnits(330, 5.1);
        assertEquals(1.683, result, 0.001);
    }

    @Test
    public void smallMeasure_returnsCorrectUnits() {
        // 25ml shot at 40% ABV = 1.0 unit
        double result = UnitsCalculator.calculateUnits(25, 40.0);
        assertEquals(1.0, result, 0.001);
    }

    @Test
    public void zeroVolume_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(0, 5.0), 0.0);
    }

    @Test
    public void zeroAbv_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(500, 0), 0.0);
    }

    @Test
    public void negativeVolume_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(-100, 5.0), 0.0);
    }

    @Test
    public void negativeAbv_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(500, -1.0), 0.0);
    }

    @Test
    public void bothNegative_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(-100, -5.0), 0.0);
    }

    @Test
    public void bothZero_returnsZero() {
        assertEquals(0.0, UnitsCalculator.calculateUnits(0, 0), 0.0);
    }

    @Test
    public void verySmallAbv_returnsCorrectUnits() {
        // 500ml at 0.5% ABV (low-alcohol beer) = 0.25 units
        double result = UnitsCalculator.calculateUnits(500, 0.5);
        assertEquals(0.25, result, 0.001);
    }

    @Test
    public void highStrengthSpirit_returnsCorrectUnits() {
        // 50ml at 40% ABV (double spirit) = 2.0 units
        double result = UnitsCalculator.calculateUnits(50, 40.0);
        assertEquals(2.0, result, 0.001);
    }
}