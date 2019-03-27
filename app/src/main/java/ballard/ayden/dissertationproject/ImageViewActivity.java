package ballard.ayden.dissertationproject;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * This class is used to allow the user to open and inspect images transferred using the application.
 * @author Ayden Ballard
 */
public class ImageViewActivity extends AppCompatActivity {

    private ImageView downloadedImageView; //ImageView for downloaded image
    private TextView imageNameTextView; //TextView for name of file
    private File fileToDisplay; //File to display

    /**
     * Initializer for ImageViewActivity
     * @param savedInstanceState - Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        downloadedImageView = findViewById(R.id.downloadedImageView);
        imageNameTextView = findViewById(R.id.imageNameTextView);

        //find file to display
        fileToDisplay = FileManager.findFile(getIntent().getStringExtra("fileName"));

        //load file name into text view
        imageNameTextView.setText(fileToDisplay.getName());

        //load image into image view
        downloadedImageView.setImageBitmap(BitmapFactory.decodeFile(fileToDisplay.getPath()));
    }

}
