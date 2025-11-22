package me.mrxeman.utilitypets.utils;

import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
public enum TimeUnits {

    TICK(1),
    SECONDS(20),
    MINUTE(20 * 60),
    HOURS(20 * 60 * 60),
    DAYS(20 * 60 * 60 * 24);

    public final int ticks;

    TimeUnits(int ticks) {
        this.ticks = ticks;
    }

    public int toTicks(int amount) {
        return amount * ticks;
    }

    @Contract(pure = true)
    public double fromTicks(int ticks) {
        return ticks / (double) this.ticks;
    }
}
