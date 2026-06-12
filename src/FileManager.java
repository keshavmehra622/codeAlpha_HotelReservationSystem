import java.io.FileWriter;
import java.util.ArrayList;

public class FileManager {

    public static void saveBookings(ArrayList<Booking> bookings) {

        try {

            FileWriter writer =
                    new FileWriter(
                            "../data/bookings.txt"
                    );

            for (Booking b : bookings) {

                writer.write(

                        "Booking ID : "
                                + b.getBookingId()

                                + "\nCustomer : "
                                + b.getCustomer().getName()

                                + "\nPhone : "
                                + b.getCustomer().getPhone()

                                + "\nRoom ID : "
                                + b.getRoom().getRoomId()

                                + "\nCategory : "
                                + b.getRoom().getCategory()

                                + "\nDays : "
                                + b.getNumberOfDays()

                                + "\nAmount : ₹"
                                + b.getTotalAmount()

                                + "\nPayment : "
                                + b.getPaymentMethod()

                                + "\nTransaction : "
                                + b.getTransactionId()

                                + "\nBooking Date : "
                                + b.getBookingDate()

                                + "\nStatus : "
                                + b.getStatus()

                                + "\n=============================\n\n"

                );
            }

            writer.close();

            System.out.println(
                    "Bookings saved successfully."
            );

        }

        catch (Exception e) {

            System.out.println(
                    "Error saving bookings!"
            );

            e.printStackTrace();

        }
    }

}