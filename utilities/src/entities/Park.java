package entities;

import java.io.Serializable;

/**
 * Represents a park with its attributes.
 */
public class Park implements Serializable {
    private static final long serialVersionUID = 1L;

    private String parkString; // Name of the park
    private String maxCapacity; // Maximum capacity of visitors
    private String pricePerPerson; // Price for each visitor
    private String availableSpots; // Number of spots available for visitors
    private String visitTimeLimit; // Visit time limit for each visitor
    private String parkMangerId; // ID of the park manager

    /**
     * Constructs a park object with the specified attributes.
     *
     * @param name          The name of the park
     * @param maxCapacity   The maximum capacity of visitors
     * @param pricePerPerson   The price for each visitor
     * @param availableSpots   The number of spots available for visitors
     * @param visitTimeLimit   The visit time limit for each visitor
     * @param parkMangerId   The ID of the park manager
     */
    public Park(String name, String maxCapacity, String pricePerPerson,
                String availableSpots, String visitTimeLimit, String parkMangerId) {
        this.parkString = name;
        this.maxCapacity = maxCapacity;
        this.pricePerPerson = pricePerPerson;
        this.availableSpots = availableSpots;
        this.visitTimeLimit = visitTimeLimit;
        this.parkMangerId = parkMangerId;
    }

    /**
     * Gets the name of the park.
     *
     * @return The name of the park
     */
    public String getParkString() {
        return parkString;
    }

    /**
     * Sets the name of the park.
     *
     * @param parkString The name of the park to set
     */
    public void setParkString(String parkString) {
        this.parkString = parkString;
    }

    /**
     * Gets the maximum capacity of visitors.
     *
     * @return The maximum capacity of visitors
     */
    public String getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Sets the maximum capacity of visitors.
     *
     * @param maxCapacity The maximum capacity of visitors to set
     */
    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Gets the price for each visitor.
     *
     * @return The price for each visitor
     */
    public String getPricePerPerson() {
        return pricePerPerson;
    }

    /**
     * Sets the price for each visitor.
     *
     * @param pricePerPerson The price for each visitor to set
     */
    public void setPricePerPerson(String pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    /**
     * Gets the number of spots available for visitors.
     *
     * @return The number of spots available for visitors
     */
    public String getAvailableSpots() {
        return availableSpots;
    }

    /**
     * Sets the number of spots available for visitors.
     *
     * @param availableSpots The number of spots available for visitors to set
     */
    public void setAvailableSpots(String availableSpots) {
        this.availableSpots = availableSpots;
    }

    /**
     * Gets the visit time limit for each visitor.
     *
     * @return The visit time limit for each visitor
     */
    public int getVisitTimeLimit() {
        return Integer.parseInt(visitTimeLimit);
    }

    /**
     * Sets the visit time limit for each visitor.
     *
     * @param visitTimeLimit The visit time limit for each visitor to set
     */
    public void setVisitTimeLimit(String visitTimeLimit) {
        this.visitTimeLimit = visitTimeLimit;
    }

    /**
     * Gets the ID of the park manager.
     *
     * @return The ID of the park manager
     */
    public String getParkMangerId() {
        return parkMangerId;
    }

    /**
     * Sets the ID of the park manager.
     *
     * @param parkMangerId The ID of the park manager to set
     */
    public void setParkMangerId(String parkMangerId) {
        this.parkMangerId = parkMangerId;
    }

    /**
     * Calculates and returns the number of visitors currently in the park.
     *
     * @return The number of visitors currently in the park
     */
    public int getVisitorsInPark() {
        return Integer.parseInt(maxCapacity) - Integer.parseInt(availableSpots);
    }

    /**
     * Returns a string representation of the park.
     *
     * @return A string representation of the park
     */
    @Override
    public String toString() {
        return "Park{" +
                "name='" + parkString + '\'' +
                ", maxCapacity='" + maxCapacity + '\'' +
                ", pricePerPerson='" + pricePerPerson + '\'' +
                ", availableSpots='" + availableSpots + '\'' +
                ", visitTimeLimit='" + visitTimeLimit + '\'' +
                ", parkMangerId='" + parkMangerId + '\'' +
                '}';
    }
}
