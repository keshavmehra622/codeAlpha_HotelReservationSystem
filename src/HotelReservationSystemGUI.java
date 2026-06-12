import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HotelReservationSystemGUI extends JFrame {

    private HotelService service;

    private final Color card = new Color(22, 26, 55);
    private final Color fieldColor = new Color(35, 42, 85);
    private final Color glow = new Color(0, 210, 255);

    public HotelReservationSystemGUI() {
        service = new HotelService();

        setTitle("Grand Palace Hotel Reservation System");
        setSize(1150, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(25, 25));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("GRAND PALACE HOTEL", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 46));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Luxury Stay • Smart Booking • Secure payment", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        subtitle.setForeground(new Color(210, 220, 255));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 24, 24));
        buttonPanel.setOpaque(false);

        JButton rooms = createButton("View Rooms", new Color(0, 170, 255));
        JButton search = createButton("Search Room", new Color(160, 90, 255));
        JButton book = createButton("Book Room", new Color(0, 210, 130));
        JButton bookings = createButton("Bookings", new Color(255, 180, 0));
        JButton cancel = createButton("Cancel", new Color(255, 70, 100));
        JButton exit = createButton("Exit", new Color(100, 110, 145));

        buttonPanel.add(rooms);
        buttonPanel.add(search);
        buttonPanel.add(book);
        buttonPanel.add(bookings);
        buttonPanel.add(cancel);
        buttonPanel.add(exit);

        JPanel welcomeCard = new JPanel(new GridLayout(2, 1));
        welcomeCard.setBackground(card);
        welcomeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel welcome = new JLabel("Welcome , Guest", JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(Color.WHITE);

        JLabel info = new JLabel("Make your stay comfortable • Feel like your own home", JLabel.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        info.setForeground(new Color(190, 200, 255));

        welcomeCard.add(welcome);
        welcomeCard.add(info);

        JPanel topCenter = new JPanel(new BorderLayout(18, 18));
        topCenter.setOpaque(false);
        topCenter.add(welcomeCard, BorderLayout.NORTH);
        topCenter.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(topCenter, BorderLayout.CENTER);

        add(mainPanel);

        rooms.addActionListener(e ->
                resultPopup("Available Rooms", service.getAvailableRoomsText())
        );

        search.addActionListener(e -> {
            String category = inputBox("Search Room", "Enter Category: Standard / Deluxe / Suite");

            if (category != null && !category.trim().isEmpty()) {
                resultPopup("Search Result", service.searchRoomText(category));
            }
        });

        book.addActionListener(e -> {
            try {
                String name = inputBox("Customer Details", "Enter Customer Name");
                String phone = inputBox("Customer Details", "Enter Phone Number");
                String category = inputBox("Room Category", "Enter Category: Standard / Deluxe / Suite");
                String daysInput = inputBox("Stay Details", "Enter Number of Days");

                if (name == null || phone == null || category == null || daysInput == null) {
                    return;
                }

                int days = Integer.parseInt(daysInput);
                double amount = service.calculateAmountByCategory(category, days);

                if (amount == -1) {
                    resultPopup("Room Not Available", "Sorry, no room available in this category.");
                    return;
                }

                String paymentMethod = paymentBox(amount);

                if (paymentMethod == null) {
                    resultPopup("Payment Cancelled", "Payment cancelled.\nBooking not created.");
                    return;
                }

                if (!paymentMethod.equalsIgnoreCase("Cash")) {
                    boolean otpVerified = otpPopup();

                    if (!otpVerified) {
                        resultPopup("Payment Failed", "Wrong OTP entered.\nBooking not created.");
                        return;
                    }
                }

                showPaymentProcessing(paymentMethod, amount, () -> {
                    String bookingText = service.bookRoomWithPaymentText(name, phone, category, days, paymentMethod);

                    String bill = createPaymentProof(
                            name,
                            phone,
                            category,
                            days,
                            amount,
                            paymentMethod
                    );

                    billPopup("Booking Confirmed", bookingText + "\n\n" + bill);
                });

            } catch (Exception ex) {
                resultPopup("Invalid Input", "Please enter correct details.");
            }
        });

        bookings.addActionListener(e ->
                resultPopup("Booking Details", service.viewBookingsText())
        );

        cancel.addActionListener(e -> {
            try {
                String idText = inputBox("Cancel Booking", "Enter Booking ID");

                if (idText == null) return;

                int id = Integer.parseInt(idText);
                resultPopup("Cancel Booking", service.cancelBookingText(id));

            } catch (Exception ex) {
                resultPopup("Invalid Booking ID", "Please enter valid booking ID.");
            }
        });

        exit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(270, 95));
        button.setBorder(BorderFactory.createEmptyBorder(18, 35, 18, 35));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(button.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 35, 35);

                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(2, 2, c.getWidth() - 5, c.getHeight() - 5, 35, 35);

                super.paint(g, c);
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
                button.setFont(new Font("Segoe UI", Font.BOLD, 24));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }
        });

        return button;
    }

    private String inputBox(String titleText, String labelText) {
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(card);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel title = new JLabel(labelText, JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        field.setBackground(fieldColor);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setPreferredSize(new Dimension(430, 55));
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton ok = createButton("OK", new Color(0, 180, 255));
        JButton cancel = createButton("Cancel", new Color(255, 80, 100));

        final String[] result = new String[1];

        ok.addActionListener(e -> {
            result[0] = field.getText();
            dialog.dispose();
        });

        cancel.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        field.addActionListener(e -> ok.doClick());
        dialog.getRootPane().setDefaultButton(ok);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(ok);
        btnPanel.add(cancel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                field.requestFocusInWindow();
            }
        });

        fadeIn(dialog);
        dialog.setVisible(true);

        return result[0];
    }

    private String paymentBox(double amount) {
        JDialog dialog = new JDialog(this, "Payment Gateway", true);
        dialog.setSize(600, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(card);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Secure Payment Gateway", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel amountLabel = new JLabel("Payable Amount: ₹" + amount, JLabel.CENTER);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        amountLabel.setForeground(new Color(0, 255, 170));

        JComboBox<String> methodBox = new JComboBox<>(new String[]{"UPI", "Debit Card", "Credit Card", "Cash"});
        methodBox.setFont(new Font("Segoe UI", Font.BOLD, 20));
        methodBox.setBackground(fieldColor);
        methodBox.setForeground(Color.WHITE);

        JLabel detailLabel = new JLabel("Enter UPI ID / Card Number", JLabel.CENTER);
        detailLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailLabel.setForeground(Color.WHITE);

        JTextField detailField = new JTextField();
        detailField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        detailField.setBackground(fieldColor);
        detailField.setForeground(Color.WHITE);
        detailField.setCaretColor(Color.WHITE);
        detailField.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        methodBox.addActionListener(e -> {
            String selected = methodBox.getSelectedItem().toString();

            if (selected.equalsIgnoreCase("Cash")) {
                detailLabel.setText("Cash payment selected. No OTP required.");
                detailLabel.setForeground(new Color(0, 255, 170));
                detailField.setText("Cash");
                detailField.setEnabled(false);
            } else {
                detailLabel.setText("Enter UPI ID / Card Number");
                detailLabel.setForeground(Color.WHITE);
                detailField.setText("");
                detailField.setEnabled(true);
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        inputPanel.setOpaque(false);
        inputPanel.add(methodBox);
        inputPanel.add(detailLabel);
        inputPanel.add(detailField);

        JButton continueBtn = createButton("Continue", new Color(0, 210, 130));
        JButton cancel = createButton("Cancel", new Color(255, 80, 100));

        final String[] result = new String[1];

        continueBtn.addActionListener(e -> {
            String selected = methodBox.getSelectedItem().toString();

            if (!selected.equalsIgnoreCase("Cash") && detailField.getText().trim().isEmpty()) {
                detailLabel.setText("Please enter payment details!");
                detailLabel.setForeground(new Color(255, 90, 90));
                return;
            }

            result[0] = selected;
            dialog.dispose();
        });

        cancel.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        detailField.addActionListener(e -> continueBtn.doClick());
        dialog.getRootPane().setDefaultButton(continueBtn);

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 10));
        top.setOpaque(false);
        top.add(title);
        top.add(amountLabel);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(continueBtn);
        btnPanel.add(cancel);

        panel.add(top, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                detailField.requestFocusInWindow();
            }
        });

        fadeIn(dialog);
        dialog.setVisible(true);

        return result[0];
    }

    private boolean otpPopup() {
        JDialog dialog = new JDialog(this, "OTP Verification", true);
        dialog.setSize(520, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(card);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("OTP Verification", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Default OTP : 1234", JLabel.CENTER);
        hint.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hint.setForeground(new Color(0, 255, 170));

        JTextField otpField = new JTextField();
        otpField.setFont(new Font("Segoe UI", Font.BOLD, 24));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setBackground(fieldColor);
        otpField.setForeground(Color.WHITE);
        otpField.setCaretColor(Color.WHITE);
        otpField.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton verify = createButton("Verify OTP", new Color(0, 210, 130));
        JButton cancel = createButton("Cancel", new Color(255, 80, 100));

        final boolean[] verified = {false};

        verify.addActionListener(e -> {
            String enteredOtp = otpField.getText().trim();

            if (enteredOtp.equals("1234")) {
                verified[0] = true;
                dialog.dispose();
            } else {
                hint.setText("Wrong OTP! Default OTP is 1234");
                hint.setForeground(new Color(255, 80, 100));
            }
        });

        cancel.addActionListener(e -> {
            verified[0] = false;
            dialog.dispose();
        });

        otpField.addActionListener(e -> verify.doClick());
        dialog.getRootPane().setDefaultButton(verify);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 12));
        center.setOpaque(false);
        center.add(hint);
        center.add(otpField);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 15, 0));
        buttons.setOpaque(false);
        buttons.add(verify);
        buttons.add(cancel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        dialog.add(panel);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                otpField.requestFocusInWindow();
            }
        });

        fadeIn(dialog);
        dialog.setVisible(true);

        return verified[0];
    }

    private void showPaymentProcessing(String method, double amount, Runnable afterSuccess) {
        JDialog dialog = new JDialog(this, "Processing Payment", true);
        dialog.setSize(590, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(card);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Processing Payment...", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel details = new JLabel(
                "<html><center>Method: " + method +
                        "<br>Amount: ₹" + amount +
                        "</center></html>",
                JLabel.CENTER
        );

        details.setFont(new Font("Segoe UI", Font.BOLD, 18));
        details.setForeground(new Color(200, 210, 255));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 220, 150));
        progressBar.setBackground(fieldColor);

        JLabel status = new JLabel("Connecting to payment system...", JLabel.CENTER);
        status.setFont(new Font("Segoe UI", Font.BOLD, 17));
        status.setForeground(new Color(0, 255, 170));

        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 14));
        bottom.setOpaque(false);
        bottom.add(progressBar);
        bottom.add(status);

        panel.add(title, BorderLayout.NORTH);
        panel.add(details, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.add(panel);

        Timer timer = new Timer(45, null);

        timer.addActionListener(new ActionListener() {
            int value = 0;
            int dots = 0;

            public void actionPerformed(ActionEvent e) {
                value += 2;
                dots++;

                progressBar.setValue(value);
                String dotText = ".".repeat(dots % 4);

                if (method.equalsIgnoreCase("Cash")) {
                    if (value < 40) status.setText("Collecting cash payment" + dotText);
                    else if (value < 80) status.setText("Confirming cash receipt" + dotText);
                    else status.setText("Generating bill" + dotText);
                } else {
                    if (value < 30) status.setText("Connecting to secure server" + dotText);
                    else if (value < 60) status.setText("Verifying payment details" + dotText);
                    else if (value < 85) status.setText("Generating digital bill" + dotText);
                    else status.setText("Finalizing payment" + dotText);
                }

                if (value >= 100) {
                    timer.stop();
                    status.setText("Payment Successful");

                    Timer closeTimer = new Timer(700, ev -> {
                        dialog.dispose();
                        afterSuccess.run();
                    });

                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
            }
        });

        timer.start();

        fadeIn(dialog);
        dialog.setVisible(true);
    }

    private void resultPopup(String titleText, String message) {
        showPopup(titleText, message, false);
    }

    private void billPopup(String titleText, String message) {
        showPopup(titleText, message, true);
    }

    private void showPopup(String titleText, String message, boolean showPdfButton) {
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setSize(780, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(25, 25));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(glow, 4),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Color.WHITE);

        JTextArea area = new JTextArea(message);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Consolas", Font.BOLD, 19));
        area.setBackground(new Color(18, 22, 55));
        area.setForeground(new Color(240, 245, 255));
        area.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 200), 3));

        JButton close = createButton("Close", new Color(0, 180, 255));
        close.addActionListener(e -> dialog.dispose());

        JPanel bottom;

        if (showPdfButton) {
            JButton pdfButton = createButton("Save as PDF Bill", new Color(0, 210, 130));
            pdfButton.addActionListener(e -> BillGenerator.printBillAsPdf(titleText, message));

            bottom = new JPanel(new GridLayout(1, 2, 20, 0));
            bottom.setOpaque(false);
            bottom.add(pdfButton);
            bottom.add(close);
        } else {
            bottom = new JPanel();
            bottom.setOpaque(false);
            bottom.add(close);
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.add(panel);

        fadeIn(dialog);
        dialog.setVisible(true);
    }

    private void fadeIn(JDialog dialog) {
        try {
            dialog.setOpacity(0f);

            Timer timer = new Timer(15, null);

            timer.addActionListener(new ActionListener() {
                float opacity = 0f;

                public void actionPerformed(ActionEvent e) {
                    opacity += 0.08f;
                    dialog.setOpacity(Math.min(opacity, 1f));

                    if (opacity >= 1f) {
                        timer.stop();
                    }
                }
            });

            timer.start();

        } catch (Exception ignored) {
        }
    }

    private String createPaymentProof(
            String name,
            String phone,
            String category,
            int days,
            double amount,
            String method
    ) {
        String dateTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a")
        );

        return "\n\n====================================\n" +
                "        DIGITAL PAYMENT BILL        \n" +
                "====================================\n" +
                "Hotel Name     : Grand Palace Hotel\n" +
                "Payment Status : SUCCESS\n" +
                "Payment Method : " + method + "\n" +
                "Paid Amount    : ₹" + amount + "\n" +
                "Customer Name  : " + name + "\n" +
                "Phone Number   : " + phone + "\n" +
                "Category       : " + category + "\n" +
                "Days           : " + days + "\n" +
                "Date & Time    : " + dateTime + "\n" +
                "====================================\n" +
                "Thank you for booking with us\n" +
                "====================================";
    }

    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(5, 7, 20),
                    getWidth(), getHeight(), new Color(35, 20, 75)
            );

            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(new Color(0, 200, 255, 35));
            g2d.fillOval(80, 80, 250, 250);

            g2d.setColor(new Color(180, 80, 255, 35));
            g2d.fillOval(getWidth() - 300, 120, 260, 260);

            g2d.setColor(new Color(0, 255, 170, 25));
            g2d.fillOval(getWidth() / 2 - 150, getHeight() - 250, 300, 300);
        }
    }
}