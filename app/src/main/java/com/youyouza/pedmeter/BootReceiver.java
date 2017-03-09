package com.youyouza.pedmeter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, MainActivity.class);

//        Thread t1 = new WriteMessage( "start in Brocast \n", "bs.txt");
////
//            t1.start();

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.i("BootReceiver","start on BootReveiver");
        context.startActivity(intent1);
    }



    class WriteMessage extends Thread {

        String message = "";
        String fileName = "";

        public WriteMessage(String message, String fileName) {

            this.message += message;
            this.fileName += fileName;
        }

        public void run() {

            writeSD(message, fileName);


        }
    }



    /**
     * here is about write data into file
     */


    public static boolean writeSD(String data, String fileName) {

        try {

            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {

                File ext = Environment.getExternalStorageDirectory();

                FileOutputStream fos = new FileOutputStream(new File(ext, fileName), true);
                fos.write(data.getBytes());
                fos.close();

                return true;

            }

            return false;


        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

            return false;

        }

    }




}
