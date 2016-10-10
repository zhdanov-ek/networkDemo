package com.example.gek.networkdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvInfo;
    Button btnStatus, btnLoadImageAsyncTask, btnLoadXmlAsyncTask;
    File sdPathAbsolute;            // Путь к флешке
    File fullPathImageFile;              // Полный путь с к файлу image на флешке
    File fullPathXMLFile;              // Полный путь с к файлу xml на флешке

    // Закачивание обычной картинки
    String imageFileName = "image.jpg";
    String imagePathHttp = "http://vzpharm.com.ua/admin/pb/zhdanov.jpg";

    // Закачиваем XML файл, который получаем через API и ключ с сайта прогнозов погоды
    String xmlFileName = "openweathermap.xml";
    String xmlFilePathHttp = "http://api.openweathermap.org/data/2.5/forecast?q=Cherkassy,ua&mode=xml&APPID=a258e2b0740fbe17ae9248bf4b7f99b4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = (TextView)findViewById(R.id.tvInfo);
        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnStatus.setOnClickListener(this);
        btnLoadImageAsyncTask = (Button) findViewById(R.id.btnLoadImageAsyncTask);
        btnLoadImageAsyncTask.setOnClickListener(this);
        btnLoadXmlAsyncTask = (Button) findViewById(R.id.btnLoadXmlAsyncTask);
        btnLoadXmlAsyncTask.setOnClickListener(this);

        // получаем путь к SD от системы в объект типа File
        sdPathAbsolute = Environment.getExternalStorageDirectory();
        fullPathImageFile = new File(sdPathAbsolute, imageFileName);
        fullPathXMLFile = new File(sdPathAbsolute, xmlFileName);
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
                downloadXml.execute(xmlFilePathHttp, fullPathXMLFile.getAbsolutePath());
                break;

            case R.id.btnLoadImageAsyncTask:
                DownloadFile downloadImage = new DownloadFile(this);
                downloadImage.execute(imagePathHttp, fullPathImageFile.getAbsolutePath());
                break;
            default:
                break;
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
