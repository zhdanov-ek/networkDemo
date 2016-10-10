package com.example.gek.networkdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gek on 09.10.16.
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {

        private Context context;
        // Конструктор свой
        public DownloadFile(Context context) {
        this.context = context;
    }

        // Срабатывает в самом начале обработки AsyncTask
        @Override
        protected void onPreExecute() {
        super.onPreExecute();
    }

        // Основной код задачи
        // На входе в массив два параметра: URL файла для скачивания и путь с именем для записи локально
        @Override
        protected String doInBackground(String... sArgs) {
        // входящий поток данных (наш файл на http)
        InputStream input = null;
        // исходящий поток данных (файл на карте памяти)
        OutputStream output = null;
        // класс позволяющий создать http соединение
        HttpURLConnection connection = null;
        // сообщение, которое будет выведено в конце обработки
        String message = null;
        try {
            // создаем объект URL по первому рнашему параметру - полному пути для скачивания файла
            URL url = new URL(sArgs[0]);
            // открываем соединение  и помещаем его в наш HttpURLConnection
            connection = (HttpURLConnection) url.openConnection();
            // подключаемся
            connection.connect();

            // ожидаем HTTP 200 OK, что означает успешное нахождение файла вместо ошибок
            // если состояние подключения с ошибкой то выводим его и заканчиваем блок
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // Определяем общий размер файла, что может нам дать возможность
            // отображать промежуточные состояние загрузки данных
            // В случае -1: сервер не предоставил размер файла
            int fileLength = connection.getContentLength();

            // --------- Блок загрузки самого файла ----------
            // указываем наши потоки для загрузки и выгрузки данных
            input = connection.getInputStream();
            output = new FileOutputStream(sArgs[1]);

            // Буфер куда будут помещаться данные при каждом цикле считывания с потока
            byte data[] = new byte[4096];
            // Тут будет указываться сколько именно байтов было считано и помещено в масив data[]
            int count = 0;
            // Тут сумируем сколько всего данных считали - нужно для своих нужд, в частности
            // для отображения промежуточных состояний загрузки файла
            long total = 0;
            // собираем значения всех переменных для отображения хода загрузки
            String s = "fileLength = " + fileLength + "\n";
            s += "total = " + total + "; count = " + count +"\n";
            while ((count = input.read(data)) != -1) {
                total += count;
//                    if (fileLength > 0) // only if total length is known
//                        // Передаем промежуточные данные методу onProgressUpdate
//                        // для передачи в главный интерфейс
//                        publishProgress((int) (total * 100 / fileLength));
                // пишем в исходящий поток с массива начиная с 0 count байт
                output.write(data, 0, count);
                s += "total = " + total + "; count = " + count +"\n";
            }
            message = "Файл " + sArgs[0] + " успешно загружен.\n" + s;


        } catch (Exception e) {
            // В случае ошики возвращаем текст ошибки, который покажем пользователю через Toast
            return e.toString();
        } finally {
            // в конце закрываем все потоки и соединение
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return message;
    }

        // Этим методом можем передавать инфу в главное окно во время работы задачи
        @Override
        protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

        // Событие отрабатывает по окончанию отработки AsyncTask.
        // Показываем ошибку если она была, информируем о других событиях, которые посчитаем нужными
        @Override
        protected void onPostExecute(String message) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
