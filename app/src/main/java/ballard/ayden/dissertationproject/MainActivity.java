package ballard.ayden.dissertationproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class MainActivity extends AppCompatActivity {

    public static String DB_PATH;


    private Button viewFilesButton;
    private Button downloadFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        DB_PATH =  getFilesDir().getAbsolutePath();

        viewFilesButton = findViewById(R.id.viewFilesButton);
        downloadFileButton = findViewById(R.id.downloadFileButton);

    } //end of onCreate

    public void launchDownloadFileActivity(View v){
        Intent downloadFileIntent = new Intent(this, DownloadFileActivity.class);
        this.startActivity(downloadFileIntent);
    }

    public void launchViewFilesActivity(View v){
        Intent viewFilesIntent = new Intent(this, FileListDisplay.class);
        this.startActivity(viewFilesIntent);
    }



}//end of class
