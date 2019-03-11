package ballard.ayden.dissertationproject;

import java.util.Scanner;

/*
QR CODE STRING
        <IP ADDRESS>-PORT-FILENAME-FILESIZE
*/

public class FoundTextReader {

    public static int readPort(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        String port = in.next();
        in.close();
        return Integer.valueOf(port);
    }

    public static String readIPaddress(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        String ipAddress = in.next();
        in.close();
        return ipAddress;
    }

    public static String readFileName(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        in.next();//skip port
        String filename = in.next();
        in.close();
        return filename;
    }

    public static int readFileSizeBytes(String foundText){
        System.out.println(foundText);
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        in.next();//skip port
        in.next();//skip filename

        int fileSizeBytes = in.nextInt();
        in.close();
        return Integer.valueOf(fileSizeBytes);
    }




}
