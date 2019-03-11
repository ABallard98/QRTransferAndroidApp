package ballard.ayden.dissertationproject;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity {


    private ImageView downloadedImageView;
    private TextView imageNameTextView;
    private File fileToDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        downloadedImageView = findViewById(R.id.downloadedImageView);
        imageNameTextView = findViewById(R.id.imageNameTextView);

        imageNameTextView.setText("????");

        fileToDisplay = findFile(getIntent().getStringExtra("fileName"));

        imageNameTextView.setText(fileToDisplay.getName());
        downloadedImageView.setImageBitmap(BitmapFactory.decodeFile(fileToDisplay.getPath()));
    }

    private File findFile(String filename){
        File downloadedFilesFolder = new File(MainActivity.DB_PATH);
        File[] listOfFiles = downloadedFilesFolder.listFiles();
        for(File f : listOfFiles){
            if(f.getName().equals(filename)){
                return f;
            }
        }
        return null;
    }

}
