package ballard.ayden.QRTransfer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * This class acts as the controller for the MainActivity. This activity is opened at launch and
 * allows the user to either scan a QR code, generate a QR code for a file, or see previously
 * transferred files.
 * @author Ayden Ballard
 */
public class MainActivity extends AppCompatActivity {

    public static String DB_PATH; //path for internal files

    private Button viewFilesButton; //button to launch view files activity
    private Button downloadFileButton; //button to launch downloaded files activity
    private Button selectFileButton; //button to launch generate QR activity
    private ImageView logoImageView; //image view for logo

    /**
     * Initializer for Main activity
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        viewFilesButton = findViewById(R.id.viewFilesButton);
        downloadFileButton = findViewById(R.id.downloadFileButton);
        selectFileButton = findViewById(R.id.selectFileButton);
        logoImageView = findViewById(R.id.logoImageView);

        logoImageView.setImageResource(R.drawable.qrlogo);
        DB_PATH =  getFilesDir().getAbsolutePath(); //assign file directory for app

    } //end of onCreate

    /**
     * Method to launch the DownloadFile activity
     * @param v - view
     */
    public void launchDownloadFileActivity(View v){
        Intent downloadFileIntent = new Intent(this, DownloadFileActivity.class);
        this.startActivity(downloadFileIntent);
    }

    /**
     * Method to launch the ViewFiles activity
     * @param v - view
     */
    public void launchViewFilesActivity(View v){
        Intent viewFilesIntent = new Intent(this, FileListDisplay.class);
        this.startActivity(viewFilesIntent);
    }

    /**
     * Method to launch GenerateQr activity
     * @param v - view
     */
    public void launchGenerateQrActivity(View v){
        Intent generateQrIntent = new Intent(this, GenerateQrActivity.class);
        this.startActivity(generateQrIntent);
    }



}//end of class
