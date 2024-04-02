package entities;

import java.io.Serializable;

/**
 * Represents a park with changes to its attributes.
 */
public class ParkForChange implements Serializable {
    private static final long serialVersionUID = 1L;

    private String parkName; // Name of the park
    private String dwellBefore; // Dwell time before change
    private String dwellAfter; // Dwell time after change
    private String maxCapacityBefore; // Maximum capacity before change
    private String maxCapacityAfter; // Maximum capacity after change

    /**
     * Constructs a ParkForChange object with the specified attributes.
     *
     * @param parkName          The name of the park
     * @param dwellBefore       The dwell time before change
     * @param dwellAfter        The dwell time after change
     * @param maxCapacityBefore    The maximum capacity before change
     * @param maxCapacityAfter     The maximum capacity after change
     */
    public ParkForChange(String parkName, String dwellBefore, String dwellAfter, String maxCapacityBefore, String maxCapacityAfter) {
        this.parkName = parkName;
        this.dwellBefore = dwellBefore;
        this.dwellAfter = dwellAfter;
        this.maxCapacityBefore = maxCapacityBefore;
        this.maxCapacityAfter = maxCapacityAfter;
    }

    /**
     * Gets the name of the park.
     *
     * @return The name of the park
     */
    public String getParkName() {
        return parkName;
    }

    /**
     * Sets the name of the park.
     *
     * @param parkName The name of the park to set
     */
    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    /**
     * Gets the dwell time before change.
     *
     * @return The dwell time before change
     */
    public String getDwellBefore() {
        return dwellBefore;
    }

    /**
     * Sets the dwell time before change.
     *
     * @param dwellBefore The dwell time before change to set
     */
    public void setDwellBefore(String dwellBefore) {
        this.dwellBefore = dwellBefore;
    }

    /**
     * Gets the dwell time after change.
     *
     * @return The dwell time after change
     */
    public String getDwellAfter() {
        return dwellAfter;
    }

    /**
     * Sets the dwell time after change.
     *
     * @param dwellAfter The dwell time after change to set
     */
    public void setDwellAfter(String dwellAfter) {
        this.dwellAfter = dwellAfter;
    }

    /**
     * Gets the maximum capacity before change.
     *
     * @return The maximum capacity before change
     */
    public String getMaxCapacityBefore() {
        return maxCapacityBefore;
    }

    /**
     * Sets the maximum capacity before change.
     *
     * @param maxCapacityBefore The maximum capacity before change to set
     */
    public void setMaxCapacityBefore(String maxCapacityBefore) {
        this.maxCapacityBefore = maxCapacityBefore;
    }

    /**
     * Gets the maximum capacity after change.
     *
     * @return The maximum capacity after change
     */
    public String getMaxCapacityAfter() {
        return maxCapacityAfter;
    }

    /**
     * Sets the maximum capacity after change.
     *
     * @param maxCapacityAfter The maximum capacity after change to set
     */
    public void setMaxCapacityAfter(String maxCapacityAfter) {
        this.maxCapacityAfter = maxCapacityAfter;
    }
}
