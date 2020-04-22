package ballard.ayden.dissertationproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileOutputStream;

/**
 * This class acts as the controller for the DownloadFileActivity. In this activity, the user is
 * able to take an image of a QR code using their camera. The information is then extracted from
 * the QR code, and the file liked to that QR code is then downloaded on to the users device.
 * @author Ayden Ballard
 */

public class DownloadFileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1337;

    private ImageView cameraPicTaken; //image view for pic taken by user
    private ImageView fileTypeImageView; //image view for the file type
    private Button takePicButton; //button to prompt user to take a picture using camera
    private Button downloadButton; //transfer button to upload file to server
    private ProgressBar progressBar; //progress bar of download
    private Camera mCamera; //camera
    private CameraPreview cameraPreview; //camera preview
    private FrameLayout cameraFrame;//camera frame

    private String foundQRText; //found QR text from camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.download_files);

        takePicButton = findViewById(R.id.cameraButton);
        downloadButton = findViewById(R.id.downloadButton);
        cameraPicTaken = findViewById(R.id.qrCode);
        fileTypeImageView = findViewById(R.id.fileTypeImageView);
        progressBar = findViewById(R.id.progressBar);

        cameraFrame = findViewById(R.id.cameraFrame);

        //action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //progress bar should be invisible on launch
        progressBar.setVisibility(View.INVISIBLE);

        //todo delete take pic button
        takePicButton.setVisibility(View.INVISIBLE);

        //download button and file type image to be invisible till QR code scanned
        downloadButton.setVisibility(View.INVISIBLE);
        fileTypeImageView.setVisibility(View.INVISIBLE);

        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        CameraSource cameraSource = new CameraSource.Builder(this,detector)
                .setRequestedPreviewSize(640,480).build();

        //camera preview setup
        mCamera = getCameraInstance();
        mCamera.startPreview();
        cameraPreview = new CameraPreview(this, cameraSource);
        cameraFrame.addView(cameraPreview);

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if(qrCodes.size() != 0){
                    cameraSource.takePicture(null, mPicture);
                    if(cameraPicTaken.getDrawable() != null){
                        BitmapDrawable drawable = (BitmapDrawable) cameraPicTaken.getDrawable();
                        Bitmap toScan = drawable.getBitmap();

                        Frame frame = new Frame.Builder().setBitmap(toScan).build();
                        SparseArray<Barcode> barcodeArray = detector.detect(frame);
                        Barcode thisCode = qrCodes.valueAt(0);
                        String foundText = thisCode.rawValue;
                        foundQRText = foundText;

                        setDownloadButton(foundText);
                    }
                }//end of if
            }
        });

        //needed to save file
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    } //end of onCreate

    /**
     * Method to scan the image taken to detect a barcode
     * @param v - view
     */
    public void downloadButtonOnClick(View v){
        if(cameraPicTaken.getDrawable() == null){ //if no picture has been taken yet
            return;
        } else{

            //make progress bar visible
            this.runOnUiThread(new Thread(new Runnable(){
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }));

            try{
                String foundText = foundQRText;

                //grab ipAddress, port, filename and file-size from found String
                String ipAddress=  FoundTextReader.readIpAddress(foundText);
                int port = FoundTextReader.readPort(foundText);
                String fileName = FoundTextReader.readFileName(foundText);
                int fileSize = FoundTextReader.readFileSizeBytes(foundText);

                //Create and run FileTransferDownload Async task
                FileTransferDownload fileTransferDownload =
                        new FileTransferDownload(this, progressBar, ipAddress,port,fileName,fileSize);
                fileTransferDownload.execute();

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
    private void setDownloadButton(String foundText){
        try {
            //make background different colour
            this.runOnUiThread(new Thread(new Runnable(){
                @Override
                public void run() {
                    cameraFrame.setVisibility(View.INVISIBLE);
                    cameraPicTaken.setVisibility(View.VISIBLE);
                }
            }));

            //grab file name and size
            String fileName = FoundTextReader.readFileName(foundText);
            String fileSize = FoundTextReader.readFileSizeString(foundText);

            //set file type image view and download button visibility to true
            this.runOnUiThread(new Thread(new Runnable (){
                @Override
                public void run() {
                    //set file type image view
                    if(fileName.contains(".pdf")){
                        fileTypeImageView.setImageResource(R.drawable.ic_pdf);
                    } else if(fileName.contains(".mp4")){
                        fileTypeImageView.setImageResource(R.drawable.ic_music_video_black);
                    }
                    else if(fileName.contains(".png") || fileName.contains(".jpg")){
                        fileTypeImageView.setImageResource(R.drawable.image_icon);
                    }

                    //set download button text
                    downloadButton.setText("Download " + fileName + "\n(" + fileSize + ")");

                    fileTypeImageView.setVisibility(View.VISIBLE);
                    downloadButton.setVisibility(View.VISIBLE);
                }
            }));
        } catch (Exception e){
            //Toast.makeText(this,"QR code not found.", Toast.LENGTH_LONG).show();
        }
   }



    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        } catch (Exception e){
            e.printStackTrace();
        }
        return c;
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
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            try {
                ImageView qrImageView = findViewById(R.id.qrCode);
                qrImageView.setImageBitmap(thumbnail);
               // setDownloadButton();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Picture Not taken", Toast.LENGTH_LONG);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            try{
                //write file
                String tempFilePath = Environment.getExternalStorageDirectory() +
                        File.separator + "tempFile.jpg";
                File temp = new File(tempFilePath);
                FileOutputStream fos = new FileOutputStream(temp);
                fos.write(data);
                fos.close();

                Bitmap picTakenBitmap = BitmapFactory.decodeFile(tempFilePath);

                if(cameraPicTaken.getDrawable() == null){
                    cameraPicTaken.setImageBitmap(picTakenBitmap);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
