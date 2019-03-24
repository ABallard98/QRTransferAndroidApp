package ballard.ayden.dissertationproject;


import android.graphics.Bitmap;
import java.io.File;

public class GenerateQRCode {

    /**
     * Method to generate the text to be contained in the QR Code
     * @param file - File to be transferred
     * @return String - text to be contained in QR code
     */
    public synchronized static String generateQRCodeText(File file) {
        String filename = file.getName().replace("-","");
        long fileSizeBytes = file.length();
        String qrCodeText = "80.2.250.205-8007-"+filename+"-"+fileSizeBytes;

        return qrCodeText;
    }

    /**
     * Method to generate the QR Code image as a Bitmap
     * @param file - File to be transferred
     * @return Bitmap - QR code
     */
    public synchronized static Bitmap generateQRCodeImage(File file){
        String qrText = generateQRCodeText(file);
        Bitmap bitmap = net.glxn.qrgen.android.QRCode.from(qrText).bitmap();
        return bitmap;
    }
}
