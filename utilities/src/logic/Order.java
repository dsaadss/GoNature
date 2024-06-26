package logic;

import java.io.Serializable;

/**
 * Represents an order made by a user for visiting a park.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderId;
    private String parkName;
    private String userId;
    private String dateOfVisit;
    private String timeOfVisit;
    private String numberOfVisitors;
    private String telephoneNumber;
    private String email;
    private String isConfirmed;
    private String isVisit;
    private String isCanceled;
    private String totalPrice;

    /**
     * Constructs an Order object with the provided parameters.
     *
     * @param orderId          The unique identifier of the order
     * @param parkName         The name of the park associated with the order
     * @param date             The date of the visit
     * @param timeOfVisit      The time of the visit
     * @param numberOfVisitors The number of visitors in the order
     * @param telephoneNumber  The telephone number of the user making the order
     * @param Email            The email of the user making the order
     */
    public Order(String orderId, String parkName, String date, String timeOfVisit, String numberOfVisitors, String telephoneNumber, String Email) {
        this.orderId = orderId;
        this.parkName = parkName;
        this.dateOfVisit = date;
        this.timeOfVisit = timeOfVisit;
        this.numberOfVisitors = numberOfVisitors;
        this.telephoneNumber = telephoneNumber;
        this.email = Email;
    }

    /**
     * Constructs an Order object with the provided parameters.
     *
     * @param orderId          The unique identifier of the order
     * @param parkName         The name of the park associated with the order
     * @param date             The date of the visit
     * @param timeOfVisit      The time of the visit
     * @param numberOfVisitors The number of visitors in the order
     */
    public Order(String orderId, String parkName, String date, String timeOfVisit, String numberOfVisitors) {
        this.orderId = orderId;
        this.parkName = parkName;
        this.dateOfVisit = date;
        this.timeOfVisit = timeOfVisit;
        this.numberOfVisitors = numberOfVisitors;
    }

    /**
     * Constructs an Order object with the provided parameters.
     *
     * @param orderId          The unique identifier of the order
     * @param parkName         The name of the park associated with the order
     * @param date             The date of the visit
     * @param timeOfVisit      The time of the visit
     * @param numberOfVisitors The number of visitors in the order
     * @param email            The email of the user making the order
     */
    public Order(String orderId, String parkName, String date, String timeOfVisit, String numberOfVisitors, String email) {
        this.orderId = orderId;
        this.parkName = parkName;
        this.dateOfVisit = date;
        this.timeOfVisit = timeOfVisit;
        this.numberOfVisitors = numberOfVisitors;
        this.email = email;
    }

    // Getters
    public String getDate() {
        return dateOfVisit;
    }

    public String getParkName() {
        return parkName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTimeOfVisit() {
        return timeOfVisit;
    }

    public String getNumberOfVisitors() {
        return numberOfVisitors;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getIsCanceled() {
        return isCanceled;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public String getIsVisit() {
        return isVisit;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    // Setters
    public void setUserId(String id) {
        if (id != null)
            this.userId = id;
        else
            this.userId = "";
    }


    public void setParkName(String parkname) {
        if (parkname != null)
            this.parkName = parkname;
        else
            this.parkName = "";

    }

    public void setIsCanceled(String canceled) {
        if (canceled != null)
            this.isCanceled = canceled;
        else
            this.isCanceled = "";

    }

    public void setTotalPrice(String total) {
        if (total != null)
            this.totalPrice = total;
        else {
            this.totalPrice = "";
        }
    }

    public void setIsConfirmed(String confirmed) {
        if (confirmed != null)
            this.isConfirmed = confirmed;
        else
            this.isConfirmed = "";

    }

    public void setDate(String date) {
        if (date != null)
            this.dateOfVisit = date;
        else
            this.dateOfVisit = "";

    }

    public void setOrderId(String orderNumber) {
        if (orderNumber != null)
            this.orderId = orderNumber;
        else
            this.orderId = "";
    }

    public void setTimeOfVisit(String timeOfVisit) {
        if (timeOfVisit != null)
            this.timeOfVisit = timeOfVisit;
        else
            this.timeOfVisit = "";
    }

    public void setNumberOfVisitors(String numberOfVisitors) {
        if (numberOfVisitors != null)
            this.numberOfVisitors = numberOfVisitors;
        else
            this.numberOfVisitors = "";
    }

    public void setTelephoneNumber(String telephoneNumber) {
        if (telephoneNumber != null)
            this.telephoneNumber = telephoneNumber;
        else
            this.telephoneNumber = "";
    }

    public void setEmail(String Email) {
        if (Email != null)
            this.email = Email;
        else
            this.email = "";
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s\n", orderId, parkName, timeOfVisit, numberOfVisitors, email, telephoneNumber);
    }

}
