package ballard.ayden.dissertationproject;

import android.content.Context;
import android.os.Environment;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientTransfer {

    private String address;
    private int port;
    private String filename;
    private int byteSize;

    public ClientTransfer(String address, int port, String filename, int byteSize) {
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

            FileOutputStream fos = context.openFileOutput(filename,Context.MODE_PRIVATE);
            int fileSize = dis.readInt();

            byte[] buffer = new byte[4096];
            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                System.out.println("read " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
            }

            //close input and output streams
            fos.close();
            dis.close();

            MainActivity.copyFileToExternalStorage(context, MainActivity.findFile(filename));

            System.out.println("FILE SAVED - " + MainActivity.DB_PATH + filename);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}