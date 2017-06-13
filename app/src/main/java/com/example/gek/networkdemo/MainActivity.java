package com.example.gek.networkdemo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvInfo;
    private ImageView imageView;
    private Button btnStatus, btnLoadImageAsyncTask, btnLoadXmlAsyncTask, btnLoadImageFromUrl;
    private File sdPathAbsolute;            // Путь к флешке
    private File fullPathImageFile;              // Полный путь с к файлу image на флешке
    private File fullPathXMLFile;              // Полный путь с к файлу xml на флешке

    private Handler mHandler;

    // Закачивание обычной картинки
    private static final String IMAGE_FILE_NAME = "image.jpg";
    private static final String IMAGE_PATH_HTTP = "http://vzpharm.com.ua/admin/pb/zhdanov.jpg";

    // Закачиваем XML файл, который получаем через API и ключ с сайта прогнозов погоды
    private static final String XML_FILE_NAME = "openweathermap.xml";
    private static final String XML_PATH_HTTP = "http://api.openweathermap.org/data/2.5/forecast?q=Cherkassy,ua&mode=xml&APPID=a258e2b0740fbe17ae9248bf4b7f99b4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        tvInfo = (TextView)findViewById(R.id.tvInfo);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnStatus.setOnClickListener(this);
        btnLoadImageAsyncTask = (Button) findViewById(R.id.btnLoadImageAsyncTask);
        btnLoadImageAsyncTask.setOnClickListener(this);
        btnLoadXmlAsyncTask = (Button) findViewById(R.id.btnLoadXmlAsyncTask);
        btnLoadXmlAsyncTask.setOnClickListener(this);
        findViewById(R.id.btnLoadImageFromUrl).setOnClickListener(this);

        // получаем путь к SD от системы в объект типа File
        sdPathAbsolute = Environment.getExternalStorageDirectory();
        fullPathImageFile = new File(sdPathAbsolute, IMAGE_FILE_NAME);
        fullPathXMLFile = new File(sdPathAbsolute, XML_FILE_NAME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStatus:
                if (isOnline())
                    tvInfo.setText("online" + connectionInfo() );
                else
                    tvInfo.setText("offline");
                break;

            case R.id.btnLoadXmlAsyncTask:
                DownloadFile downloadXml = new DownloadFile(this);
                downloadXml.execute(XML_PATH_HTTP, fullPathXMLFile.getAbsolutePath());
                break;

            case R.id.btnLoadImageAsyncTask:
                DownloadFile downloadImage = new DownloadFile(this);
                downloadImage.execute(IMAGE_PATH_HTTP, fullPathImageFile.getAbsolutePath());
                break;
            case R.id.btnLoadImageFromUrl:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadImageViaHandler();
                    }
                }).start();

                break;
            default:
                break;
        }

    }


    private void loadImageViaHandler(){
        try {
            URL url = new URL(IMAGE_PATH_HTTP);
            InputStream is = url.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            final File file = new File(getBaseContext().getFilesDir(), "image.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                }
            });

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }

    private boolean isOnline() {
        // Обращаемся к сервису, который отвечает за соединение с интернетом
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    /** Получаем инфу с системного сервиса о состоянии сети */
    private String connectionInfo(){
        String s = "";
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        s += "\n getExtraInfo = " + nInfo.getExtraInfo();
        s += "\n getSubtypeName = " + nInfo.getSubtypeName();
        s += "\n getTypeName = " + nInfo.getTypeName();
        s += "\n getDetailedState = " + nInfo.getDetailedState().toString();;
        return s;
    }

}
