package com.simple.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunCmd {
    public static void runcmd(String cmd) throws IOException, InterruptedException {
        new Thread(() -> {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c",cmd+" > /data/user/0/com.simple.container/files/out");
            // ProcessBuilder pb2 = new ProcessBuilder("/bin/sh","-c","ls /sdcard > /data/user/0/com.simple.container/files/out");

            Process ps= null;
            try {
                ps = pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //String outputFile=getFilesDir().getAbsolutePath()+"/out";

            try (InputStream inputStream = ps.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 InputStream errStream = ps.getErrorStream();
                 BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream))
                 //FileWriter writer = new FileWriter(outputFile)
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    // writer.write(line);
                    //writer.write("\n");
                }
                String line2;
                while ((line2 = reader.readLine()) != null) {
                    System.out.println(line2);
                    // writer.write(line);
                    //writer.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }
}
