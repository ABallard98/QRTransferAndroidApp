package ballard.ayden.QRTransfer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * This class acts as the controller for the GenerateQrActivity. This class is used when the user
 * wishes to upload a file from their mobile phone to the server. When the user selects a file, it
 * is uploaded to the server, and a QR code that links the file is generated.
 * @author Ayden Ballard
 */
public class GenerateQrActivity extends AppCompatActivity {

    private ImageView qrImageView; //ImageView for generated QR code
    private Button selectFileButton; //Button to prompt the user to select a file
    private static final int GET_CONTENT_REQUEST_CODE = 42;

    /**
     * Initializer for GenerateQRActivity
     * @param savedInstanceState - Bundle
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        qrImageView = findViewById(R.id.qrImageView);
        selectFileButton = findViewById(R.id.selectFileButton);
    }

    /**
     * Method to select a file from user's device
     * @param v - View
     */
    public void selectFile(View v){
        Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileChooserIntent.addCategory(Intent.CATEGORY_OPENABLE);

        fileChooserIntent.setType("*/*");
        String[] mimeTypes = {"image/*","application/pdf"};
        fileChooserIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(fileChooserIntent,GET_CONTENT_REQUEST_CODE);
    }

    /**
     * Method to take the selected file from the user, copy it to internal storage, generate a QR
     * code for the selected file and then upload the file to the server to transfer
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GET_CONTENT_REQUEST_CODE:
                    Uri uri = resultData.getData();
                    try {

                        //input stream of selected file
                        InputStream in = getContentResolver().openInputStream(uri);

                        //new path of file
                        String newFilePath = MainActivity.DB_PATH + "tempFile.jpg";

                        //output stream to move file to new path
                        OutputStream out = new FileOutputStream(new File(
                                newFilePath));

                        //copy file
                        byte[] buf = new byte[1024];
                        int len;
                        while((len=in.read(buf))>0){
                            out.write(buf,0,len);
                        }
                        out.close();
                        in.close();

                        //File to be transferred in internal storage
                        File tempFile = new File(newFilePath);

                        //generate and allocate QR code for selected file
                        qrImageView.setImageBitmap(
                                QRCode.from(GenerateQRCode.generateQRCodeText(tempFile)).bitmap());

                        //upload to server
                        FileTransferUpload fileTransferUpload =
                                new FileTransferUpload(tempFile);
                        fileTransferUpload.run();

                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        break;
                    }

            }
    }
}
