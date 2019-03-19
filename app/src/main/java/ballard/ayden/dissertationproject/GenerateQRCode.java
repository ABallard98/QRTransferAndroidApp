package ballard.ayden.dissertationproject;


import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.QRCode;



public class GenerateQRCode {

    public synchronized static String generateQRCodeText(File file) {
        String filename = file.getName().replace("-","");
        long fileSizeBytes = file.length();
        String qrCodeText = "80.2.250.205-8007-"+filename+"-"+fileSizeBytes;

        return qrCodeText;
    }

    public synchronized static Bitmap generateQRCodeImage(File file){
        String qrText = generateQRCodeText(file);

        Bitmap bitmap = net.glxn.qrgen.android.QRCode.from(qrText).bitmap();

        return bitmap;
    }
}
