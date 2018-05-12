package com.example.casualbaptou.attractivewine;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class MainCocktailLoaderIntent extends IntentService {

    private static final String ACTION_get_cocktail_API = "com.example.utilisateur.attractivewine2.action.Beers";//"com.example.casualbaptou.attractivewine.action.cocktails";

    public MainCocktailLoaderIntent() {
        super("MainCocktailLoaderIntent");
    }

    public static void startActionGetCocktail(Context context) {
        try{
            Intent intent = new Intent(context, MainCocktailLoaderIntent.class);
            intent.setAction(ACTION_get_cocktail_API);
            context.startService(intent);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_get_cocktail_API.equals(action)) {
                saveCocktailList();
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.COCKTAILS_UPDATE));
            }
        }
    }

    private void saveCocktailList() {
        try{
            for(int i=0; i<3; i++)
            {
                URL url = new URL( URLRefs.URLbase + URLRefs.Refs[4+i] );
                Log.i(TAG, URLRefs.URLbase + URLRefs.Refs[4+i]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                    File f = new File(getCacheDir(), i+"cocktailArray.json");
                    copyInputStreamToFile(conn.getInputStream(), f );
                    Log.i(TAG,  "Cocktail list " + (i+1) + " json downloaded");
                }
                else
                    Log.e(TAG, "Can't access API");
            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void copyInputStreamToFile(InputStream in, File file)
    {   //puts the json in a file
        try {
            OutputStream out = new FileOutputStream(file, true);
            byte[] buf = new byte[1024];
            int lenght;
            while ((lenght = in.read(buf)) > 0) {
                out.write(buf, 0, lenght);
            }
            out.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
