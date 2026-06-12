import java.awt.*;
import java.awt.print.*;

public class BillGenerator {

    public static void printBillAsPdf(String title, String billText) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(title);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            g2.setFont(new Font("Serif", Font.BOLD, 24));
            g2.drawString("GRAND PALACE HOTEL", 160, 40);

            g2.setFont(new Font("Serif", Font.BOLD, 16));
            g2.drawString("Payment Bill / Transaction Receipt", 150, 70);

            g2.drawLine(40, 90, 520, 90);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

            int y = 120;
            String[] lines = billText.split("\n");

            for (String line : lines) {
                g2.drawString(line, 50, y);
                y += 18;
            }

            g2.drawLine(40, y + 10, 520, y + 10);
            g2.setFont(new Font("Serif", Font.BOLD, 13));
            g2.drawString("Thank you for booking with Grand Palace Hotel!", 120, y + 40);

            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}