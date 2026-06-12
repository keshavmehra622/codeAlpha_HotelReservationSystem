import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {

    private int bookingId;
    private Customer customer;
    private Room room;
    private int numberOfDays;
    private double totalAmount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String bookingDate;

   public Booking(int bookingId, Customer customer, Room room, int numberOfDays, String paymentMethod, String transactionId) {

        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.numberOfDays = numberOfDays;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;

        this.totalAmount = room.getPrice() * numberOfDays;
        this.status = "Booked";

        this.bookingDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));

        this.room.setAvailable(false);
    }

    public int getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void cancelBooking() {
        if (status.equals("Booked")) {
            status = "Cancelled";
            room.setAvailable(true);
        }
    }

    @Override
    public String toString() {
        return "Booking ID      : " + bookingId +
                "\nCustomer Name   : " + customer.getName() +
                "\nPhone Number    : " + customer.getPhone() +
                "\nRoom ID         : " + room.getRoomId() +
                "\nRoom Category   : " + room.getCategory() +
                "\nNumber of Days  : " + numberOfDays +
                "\nTotal Amount    : ₹" + totalAmount +
                "\nPayment Method  : " + paymentMethod +
                "\nTransaction ID  : " + transactionId +
                "\nBooking Date    : " + bookingDate +
                "\nStatus          : " + status +
                "\n----------------------------------------";
    }
}