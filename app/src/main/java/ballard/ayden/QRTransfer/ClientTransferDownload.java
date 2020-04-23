package ballard.ayden.QRTransfer;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientTransferDownload {

    private String address;
    private int port;
    private String filename;
    private int byteSize;

    public ClientTransferDownload(String address, int port, String filename, int byteSize) {
        this.address = address;
        this.port = port;
        this.filename = filename;
        this.byteSize = byteSize;
    }

    public void run(Context context) {
        try {
            System.out.println("connecting...");
            Socket sock = new Socket(address, port);
            System.out.println("connected");

            //sending instructions to the server
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            System.out.println("DOWNLOAD-"+filename+"-"+byteSize);
            dos.writeUTF("DOWNLOAD-"+filename+"-"+byteSize);

            DataInputStream dis = new DataInputStream(sock.getInputStream());

            filename = filename.replace(" ",""); //remove whitespaces in filename

            //todo original file output stream
            FileOutputStream fos = context.openFileOutput(filename,Context.MODE_PRIVATE);

            //FileOutputStream fos = new FileOutputStream(MainActivity.EXTERNAL_FILES_PATH);
            int fileSize = dis.readInt();

            byte[] buffer = new byte[4096];
            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                fos.write(buffer, 0, read);
            }
            System.out.println("read " + totalRead + " bytes.");

            //close input and output streams
            fos.close();
            dis.close();

            //move file to external storage for other apps
            FileManager.copyFileToExternalStorage(context, FileManager.findFile(filename));

        } catch (IOException e){
            e.printStackTrace();
        }
    }


}