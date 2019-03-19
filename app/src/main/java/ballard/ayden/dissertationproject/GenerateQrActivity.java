package ballard.ayden.dissertationproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class GenerateQrActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private Button selectFileButton;
    private static final int GET_CONTENT_REQUEST_CODE = 42;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);


        qrImageView = findViewById(R.id.qrImageView);
        selectFileButton = findViewById(R.id.selectFileButton);

    }

    public void selectFile(View v){
        Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileChooserIntent.addCategory(Intent.CATEGORY_OPENABLE);

        fileChooserIntent.setType("image/*");

        startActivityForResult(fileChooserIntent,GET_CONTENT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GET_CONTENT_REQUEST_CODE:
                    Uri uri = resultData.getData();
                    try {

                        InputStream in = getContentResolver().openInputStream(uri);

                        String newFilePath = MainActivity.DB_PATH + "tempFile.jpg   ";

                        OutputStream out = new FileOutputStream(new File(
                                newFilePath));

                        byte[] buf = new byte[1024];
                        int len;
                        while((len=in.read(buf))>0){
                            out.write(buf,0,len);
                        }
                        out.close();
                        in.close();

                        File tempFile = new File(newFilePath);

                        qrImageView.setImageBitmap(
                                QRCode.from(GenerateQRCode.generateQRCodeText(tempFile)).bitmap());

                        ClientTransferUpload clientTransferUpload =
                                new ClientTransferUpload(tempFile);
                        clientTransferUpload.run();
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        break;
                    }


            }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }






}
