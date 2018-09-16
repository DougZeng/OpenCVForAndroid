package com.wesine.opencv320;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Main3Activity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        imageView = (ImageView) findViewById(R.id.imageView3);
    }

    public void capture(View view) {
//        Webcam.setDriver();
////        Webcam.setDriver(new V4l4jDriver());
//        Webcam webcam = Webcam.getDefault();
//        webcam.open(true);
//        ByteBuffer imageBytes = webcam.getImageBytes();
//        saveFile(imageBytes);

//        ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));

    }

    private void saveFile(ByteBuffer imageBytes) {
        try {
            ByteBuffer byteBuf = imageBytes.allocate(1024 * 14 * 1024);
            byte[] bbb = new byte[14 * 1024 * 1024];
            FileInputStream fis = new FileInputStream("e://data/other/UltraEdit_17.00.0.1035_SC.exe");
            FileOutputStream fos = new FileOutputStream("e://data/other/outFile.txt");
            FileChannel fc = fis.getChannel();
            long timeStar = System.currentTimeMillis();// 得到当前的时间
            fc.read(byteBuf);// 1 读取
            //MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            System.out.println(fc.size() / 1024);
            long timeEnd = System.currentTimeMillis();// 得到当前的时间
            System.out.println("Read time :" + (timeEnd - timeStar) + "ms");
            timeStar = System.currentTimeMillis();
            fos.write(bbb);//2.写入
            //mbb.flip();
            timeEnd = System.currentTimeMillis();
            System.out.println("Write time :" + (timeEnd - timeStar) + "ms");
            fos.flush();
            fc.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
