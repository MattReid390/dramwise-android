package org.me.gcu.dramwise.util;

public final class UnitsCalculator {

    private UnitsCalculator() {}

    /**
     * UK alcohol units:
     * units = (volume_ml * ABV%) / 1000
     * (since 1 unit = 10ml ethanol; ethanol ml = volume * abv/100)
     */
    public static double calculateUnits(double volumeMl, double abvPercent) {
        if (volumeMl <= 0 || abvPercent <= 0) return 0.0;
        return (volumeMl * abvPercent) / 1000.0;
    }
}