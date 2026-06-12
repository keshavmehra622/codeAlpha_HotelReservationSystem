import java.util.ArrayList;
import java.util.Random;

public class HotelService {

    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Booking> bookings = new ArrayList<>();
    private int bookingCounter = 1;

    public HotelService() {
        for (int i = 1; i <= 20; i++) {
            rooms.add(new Room(100 + i, "Standard", 1500, true));
            rooms.add(new Room(200 + i, "Deluxe", 2500, true));
            rooms.add(new Room(300 + i, "Suite", 5000, true));
        }
    }

    public String getAvailableRoomsText() {
        return "ROOM AVAILABILITY\n\n" +
                "• Standard Rooms Available : " + countAvailable("Standard") + " / 20\n" +
                "  Price Per Day            : ₹1500\n\n" +
                "• Deluxe Rooms Available   : " + countAvailable("Deluxe") + " / 20\n" +
                "  Price Per Day            : ₹2500\n\n" +
                "• Suite Rooms Available    : " + countAvailable("Suite") + " / 20\n" +
                "  Price Per Day            : ₹5000\n\n" +
                "Room ID will be assigned automatically during booking.";
    }

    public String searchRoomText(String category) {
        int count = countAvailable(category);
        double price = getPriceByCategory(category);

        if (count == 0 || price == -1) {
            return "No available room found!";
        }

        return "CATEGORY DETAILS\n\n" +
                "• Category        : " + category + "\n" +
                "• Available Rooms : " + count + " / 20\n" +
                "• Price Per Day   : ₹" + price + "\n\n" +
                "Room ID will be assigned automatically.";
    }

    public double calculateAmountByCategory(String category, int days) {
        double price = getPriceByCategory(category);

        if (price == -1 || days <= 0 || countAvailable(category) == 0) {
            return -1;
        }

        return price * days;
    }

    public String bookRoomWithPaymentText(String name, String phone, String category, int days, String paymentMethod) {
        Room room = findAvailableRoomByCategory(category);

        if (room == null) {
            return "Room unavailable!";
        }

        Customer customer = new Customer(name, phone);
        String transactionId = generateTransactionId();

        Booking booking = new Booking(
                bookingCounter++,
                customer,
                room,
                days,
                paymentMethod,
                transactionId
        );

        bookings.add(booking);
        FileManager.saveBookings(bookings);

        return "BOOKING CONFIRMED\n\n" + booking.toString();
    }

    public String viewBookingsText() {
        if (bookings.isEmpty()) {
            return "No bookings found!";
        }

        StringBuilder text = new StringBuilder();

        for (Booking booking : bookings) {
            text.append(booking.toString()).append("\n\n");
        }

        return text.toString();
    }

    public String cancelBookingText(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                booking.cancelBooking();
                FileManager.saveBookings(bookings);
                return "Booking Cancelled Successfully!\n\n" + booking;
            }
        }

        return "Booking Not Found!";
    }

    private int countAvailable(String category) {
        int count = 0;

        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category) && room.isAvailable()) {
                count++;
            }
        }

        return count;
    }

    private double getPriceByCategory(String category) {
        if (category.equalsIgnoreCase("Standard")) return 1500;
        if (category.equalsIgnoreCase("Deluxe")) return 2500;
        if (category.equalsIgnoreCase("Suite")) return 5000;

        return -1;
    }

    private Room findAvailableRoomByCategory(String category) {
        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category) && room.isAvailable()) {
                return room;
            }
        }

        return null;
    }

    private String generateTransactionId() {
        Random random = new Random();
        return "TXN" + System.currentTimeMillis() + random.nextInt(999);
    }
}