package ru.opi.model;

public enum Priority {
    КРИТИЧЕСКИЙ("Критический", 4, 0),
    ВЫСОКИЙ("Высокий", 8, 1),
    СРЕДНИЙ("Средний", 24, 4),
    НИЗКИЙ("Низкий", 72, 8);

    private final String displayName;
    private final int slaHours;
    private final int waitTimeHours;

    Priority(String displayName, int slaHours, int waitTimeHours) {
        this.displayName = displayName;
        this.slaHours = slaHours;
        this.waitTimeHours = waitTimeHours;
    }

    public String getDisplayName() { return displayName; }
    public int getSlaHours() { return slaHours; }
    public int getWaitTimeHours() { return waitTimeHours; }
}