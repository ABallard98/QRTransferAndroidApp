package ballard.ayden.QRTransfer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;

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
    private TextView serverStatusTextView; //text view for server status

    private Menu menu;

    //permission codes
    private final int CAMERA_PERMISSION = 3001;
    private final int CONTACT_PERMISSION = 3002;
    private final int STORAGE_EXTERNAL_PERMISSION = 3003;
    private final int ALL_PERMISSIONS = 1;
    private final String[] PERMISSIONS = {
      Manifest.permission.CAMERA,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


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
        serverStatusTextView = findViewById(R.id.serverStatusTextView);

        logoImageView.setImageResource(R.drawable.qrlogo);
        DB_PATH =  getFilesDir().getAbsolutePath(); //assign file directory for app

        //action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //todo check permissions
        if(!checkPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSIONS);
        }

        serverStatusTextView.setText(R.string.server_connection_fail);
        serverStatusTextView.setTextColor(Color.RED);

        checkServerStatus(); //checking for server status

    } //end of onCreate

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();

        //sort menu properties
        inflater.inflate(R.menu.home_menu, menu);
        this.menu = menu;


        return true;
    }

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

    /**
     * Method to check server status and update server status text view
     */
    private void checkServerStatus(){
        Thread thread = new Thread(() -> {
            try{
                Socket testSocket = new Socket("86.157.154.80", 8007);
                if(testSocket.isConnected()) { //if socket successfully connects
                    //update server status text view
                    serverStatusTextView.setText(R.string.server_connection_successful);
                    serverStatusTextView.setTextColor(Color.GREEN);
                }
                testSocket.close(); //close socket
            } catch (Exception e){
                e.printStackTrace();
            }
        }); //end of thread
        thread.start();
    }

    /**
     * Method to check if permissions are granted, if not - ask for permissions
     * @param context - application context
     * @param permissions - permissions to be checked if granted
     * @return boolean - false if any permission is not granted
     */
    private boolean checkPermissions(Context context, String[] permissions){
        if(context != null && permissions != null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**+
     * Method to handle on request permission results
     * @param requestCode - request code of permission
     * @param permissions - permissions asked to be granted
     * @param grantResults - permission granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode) {
            case(CAMERA_PERMISSION):
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Camera Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    Toast.makeText(this,"Camera Permission Denied - expect errors",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            case(STORAGE_EXTERNAL_PERMISSION):
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Storage Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    Toast.makeText(this,"Storage Permission Denied - expect errors",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            case(CONTACT_PERMISSION):
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Contact Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    Toast.makeText(this,"Contact Permission Denied - expect errors",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
        }
    }


}//end of class
