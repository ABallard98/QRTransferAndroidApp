package ballard.ayden.dissertationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

/**
 * This class acts as the controller for the DownloadFileActivity. In this activity, the user is
 * able to take an image of a QR code using their camera. The information is then extracted from
 * the QR code, and the file liked to that QR code is then downloaded on to the users device.
 * @author Ayden Ballard
 */

public class DownloadFileActivity extends AppCompatActivity {

    private ImageView cameraPicTaken; //image view for pic taken by user
    private ImageView fileTypeImageView; //image view for the file type
    private Button takePicButton; //button to prompt user to take a picture using camera
    private Button downloadButton; //transfer button to upload file to server
    private static final int REQUEST_IMAGE_CAPTURE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_files);

        takePicButton = findViewById(R.id.cameraButton);
        downloadButton = findViewById(R.id.downloadButton);
        cameraPicTaken = findViewById(R.id.qrCode);
        fileTypeImageView = findViewById(R.id.fileTypeImageView);

        //action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //download button and file type image to be invisible till QR code scanned
        downloadButton.setVisibility(View.INVISIBLE);
        fileTypeImageView.setVisibility(View.INVISIBLE);

        //needed to save file
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    } //end of onCreate

    /**
     * Method to scan the image taken to detect a barcode
     * @param v - view
     */
    public void downloadButtonOnClick(View v){
        if(cameraPicTaken.getDrawable() == null){
            //if no picture has been taken yet
            return;
        } else{
            //Bitmap image to scan
            BitmapDrawable drawable = (BitmapDrawable) cameraPicTaken.getDrawable();
            Bitmap toScan = drawable.getBitmap();

            //initialise barcode detector
            BarcodeDetector detector =
                    new BarcodeDetector.Builder(getApplicationContext())
                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                            .build();
            if(!detector.isOperational()){
                return;
            }
            try{
                //detect barcode
                Frame frame = new Frame.Builder().setBitmap(toScan).build();
                SparseArray<Barcode> barcodeArray = detector.detect(frame);
                Barcode thisCode = barcodeArray.valueAt(0);

                String foundText = thisCode.rawValue;

                //grab ipAddress, port, filename and file-size from found String
                String ipAddress=  FoundTextReader.readIpAddress(foundText);
                int port = FoundTextReader.readPort(foundText);
                String fileName = FoundTextReader.readFileName(foundText);
                int fileSize = FoundTextReader.readFileSizeBytes(foundText);

                //create FileTransferDownload thread
                FileTransferDownload fileTransferDownload =
                        new FileTransferDownload(ipAddress,port,fileName,fileSize);
                fileTransferDownload.run(this);

                //load image into imageView
                cameraPicTaken.setImageBitmap(BitmapFactory.decodeFile(
                        MainActivity.DB_PATH+"/"+fileName));

                //alert user file was successfully downloaded
                Toast.makeText(this, "FILE DOWNLOADED", Toast.LENGTH_LONG).show();

                //return user to MainActivity
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                this.startActivity(mainActivityIntent);

            } catch (Exception e){
                e.printStackTrace();
                //alert user file could not be transferred
                Toast.makeText(this, "Error - File could not be downloaded",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method to scan the QR Code and set the text of the download to the name of file stored
     * in the QR code.
     */
    private void setDownloadButton(){

        //Bitmap image to scan
        BitmapDrawable drawable = (BitmapDrawable) cameraPicTaken.getDrawable();
        Bitmap toScan = drawable.getBitmap();

        //initialise barcode detector
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();

        if(!detector.isOperational()){
            return;
        }

        try {
            //detect barcode
            Frame frame = new Frame.Builder().setBitmap(toScan).build();
            SparseArray<Barcode> barcodeArray = detector.detect(frame);
            Barcode thisCode = barcodeArray.valueAt(0);

            //found text from the QR code
            String foundText = thisCode.rawValue;

            //grab ipAddress  found String
            String fileName = FoundTextReader.readFileName(foundText);

            String fileSize = FoundTextReader.readFileSizeString(foundText);

            //set file type image view
            if(fileName.contains(".pdf")){
                this.fileTypeImageView.setImageResource(R.drawable.ic_pdf);
            } else if(fileName.contains(".png") || fileName.contains(".jpg")){
                this.fileTypeImageView.setImageResource(R.drawable.image_icon);
            }

            //set download button text
            downloadButton.setText("Download " + fileName + "\n(" + fileSize + ")");

            //set file type image view and download button visibility to true
            this.fileTypeImageView.setVisibility(View.VISIBLE);
            downloadButton.setVisibility(View.VISIBLE);

        } catch (Exception e){
            e.printStackTrace();
        }
   }
    /**
     * Method to start an implicit intent to activate the camera to allow the user
     * to take a picture of a barcode
     * @param v
     */
    public void takePicture(View v){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // request code
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        if (cameraPicTaken.getDrawable() == null){
            //downloadButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Activity to take user back to homepage
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }


    /**
     * Method to take the image taken by the user and decode the QR code in image if it exists
     * @param requestCode - int
     * @param resultCode - int
     * @param data - Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == REQUEST_IMAGE_CAPTURE){
            //  data.getExtras()
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            try {
                ImageView qrImageView = findViewById(R.id.qrCode);
                qrImageView.setImageBitmap(thumbnail);
                setDownloadButton();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Picture Not taken", Toast.LENGTH_LONG);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
