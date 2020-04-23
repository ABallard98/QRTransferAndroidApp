package ballard.ayden.QRTransfer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

/**
 * This class is used to implement the file download process. The FileTransferDownload class takes in an
 * IP address, a port number, the file name and the files size in bytes. When the run() method is
 * called, the application connects to the server using a socket. The files information is then
 * sent and reconstructed on this device.
 * @author Ayden Ballard
 */
public class FileTransferDownload extends AsyncTask<Void, Integer, Boolean> {

    private String address; //IP Address of server
    private int port; //Port to connect to
    private String filename; //name of file to transfer
    private int byteSize; //file size of selected file
    private Context context; //application context
    private final WeakReference<ProgressBar> progressBar; //progress bar

    /**
     * Constructor for FileTransferDownload object
     * @param address - address of server
     * @param port - port to connect to
     * @param filename - name of file
     * @param byteSize - size of file
     */
    public FileTransferDownload(Context context, ProgressBar progressBar, String address, int port, String filename, int byteSize) {
        this.address = address;
        this.port = port;
        this.filename = filename;
        this.byteSize = byteSize;
        this.context = context;
        this.progressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        run();
        return true;
    }

    /**
     * Method to update progress bar
     * @param integers - percentage as integer
     */
    protected void onProgressUpdate(Integer... integers){
        //super.onProgressUpdate(integers);
        System.out.println("Downloading: " + integers[0] +  "%");
        progressBar.get().setProgress(integers[0]);
    }

    /**
     * Method to alert user that the file is downloaded and returns them
     * to the main activity
     * @param bool
     */
    protected void onPostExecute(Boolean bool){
        //alert user file was successfully downloaded
        Toast.makeText(context, "FILE DOWNLOADED", Toast.LENGTH_LONG).show();
        //Launch intent to take user back to main activity
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(mainActivityIntent);
    }

    /**
     * Method to download a file from the server onto the user's device
     */
    public void run() {
        try {

            System.out.println("connecting...");
            Socket sock = new Socket("86.157.154.4", port);
            System.out.println("connected");

            //sending instructions to the server
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF("DOWNLOAD-"+filename+"-"+byteSize);

            //input stream to receive file bytes
            DataInputStream dis = new DataInputStream(sock.getInputStream());

            filename = filename.replace(" ",""); //remove whitespaces in filename
            filename = filename.toLowerCase();
            FileOutputStream fos = context.openFileOutput(filename,Context.MODE_PRIVATE);

            //copy file to internal storage of application
            int fileSize = dis.readInt();
            byte[] buffer = new byte[4096];
            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;

                //calculate current download percentage
                int percentage = (int) ((totalRead * 100.0f) / byteSize);
                publishProgress(percentage);

                fos.write(buffer, 0, read);
            }

            System.out.println("read " + totalRead + " bytes.");
            //close input and output streams
            fos.close();
            dis.close();

            //move file to external storage for other apps
            FileManager.copyFileToExternalStorage(context, FileManager.findFile(filename));
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}