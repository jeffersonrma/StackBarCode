package br.com.ktek.connection;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpGetRequest extends AsyncTask<String,Void, String> {

    @Override
    protected String doInBackground(String... Url) {
        try {
            return BuscarNome(Url[0]);
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    String responseToString(HttpEntity responseEntity) throws Exception
    {
        char[] buffer = new char[(int) responseEntity.getContentLength()];
        InputStream stream = responseEntity.getContent();
        InputStreamReader reader = new InputStreamReader(stream);
        reader.read(buffer);
        stream.close();
        return new String(buffer).replaceAll("\\u0000", "");
    }

    String BuscarNome(String Url) throws Exception{
        HttpGet request = new HttpGet(Url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        HttpEntity responseEntity = response.getEntity();
        return responseToString(responseEntity);
    }
}
