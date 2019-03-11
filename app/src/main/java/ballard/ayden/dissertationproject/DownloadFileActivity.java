package ballard.ayden.dissertationproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class DownloadFileActivity extends AppCompatActivity {

    private ImageView cameraPicTaken;
    private TextView foundStringTextView;
    private Button takePicButton;
    private Button downloadButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1337;

    public static String DB_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_files);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        //getActionBar().setTitle("QR FileTransfer");
        //initialise elements
        //DB_PATH =  getFilesDir().getAbsolutePath();
        takePicButton = findViewById(R.id.cameraButton);
        downloadButton = findViewById(R.id.downloadButton);
        cameraPicTaken = findViewById(R.id.qrCode);
        foundStringTextView = findViewById(R.id.foundStringTextView);


        downloadButton.setVisibility(View.INVISIBLE);

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

        } else{
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

            //detect barcode
            Frame frame = new Frame.Builder().setBitmap(toScan).build();
            SparseArray<Barcode> barcodeArray = detector.detect(frame);
            Barcode thisCode = barcodeArray.valueAt(0);

            String foundText = thisCode.rawValue;
            foundStringTextView.setText(foundText);

            String ipAddress=  FoundTextReader.readIPaddress(foundText);
            int port = FoundTextReader.readPort(foundText);
            String fileName = FoundTextReader.readFileName(foundText);
            int filesize = FoundTextReader.readFileSizeBytes(foundText);

            try{
                ClientTransfer clientTransfer = new ClientTransfer(ipAddress,port,fileName,filesize);
                clientTransfer.run(this);

                //load image
                cameraPicTaken.setImageBitmap(BitmapFactory.decodeFile(DB_PATH+"/"+fileName));
                Toast.makeText(this, "FILE DOWNLOADED", Toast.LENGTH_LONG).show();

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        /*
        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+foundText));
        startActivity(i);
        */
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
            downloadButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if( requestCode == REQUEST_IMAGE_CAPTURE){
            //  data.getExtras()
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            try {
                ImageView qrImageView = findViewById(R.id.qrCode);
                qrImageView.setImageBitmap(thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Picture Not taken", Toast.LENGTH_LONG);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.files, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
