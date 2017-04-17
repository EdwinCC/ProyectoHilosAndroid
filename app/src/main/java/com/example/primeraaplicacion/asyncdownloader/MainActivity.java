package com.example.primeraaplicacion.asyncdownloader;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private long id;

    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnHilo = (Button)findViewById(R.id.btnHilo);
        btnSinHilos = (Button)findViewById(R.id.btnSinHilos);
        btnAsyncTask = (Button)findViewById(R.id.btnAsyncTask);
        pbarProgreso = (ProgressBar)findViewById(R.id.pbarProgreso);
        btnLimpiar = (Button)findViewById(R.id.btnLimpiar);
        btnAsyncDescarga = (Button)findViewById(R.id.btnAsyncDownload);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        IntentFilter filter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver,filter);


        btnAsyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setMessage("Procesando...");
                pDialog.setCancelable(true);
                pDialog.setMax(100);
                tarea1 = new MiTareaAsincrona();
                tarea1.execute();
            }
        });

        btnAsyncDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncdescarga=new AsyncDescarga();
                asyncdescarga.execute("http://wallpapercave.com/wp/wp1848549.jpg");
            }
        });


        btnSinHilos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbarProgreso.setMax(100); //valor máximo de ProgressBar
                pbarProgreso.setProgress(0); // Empieza vacía ProgressBar
                for (int i = 1; i <= 10; i++) {
                    tareaLarga();
                    pbarProgreso.incrementProgressBy(10); //incremento
                }
                Toast.makeText(getApplicationContext(), "Tarea finalizada!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pbarProgreso.setProgress(0); // Empieza vacía ProgressBar






            }
        });


        btnHilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbarProgreso.setMax(100); //valor máximo de ProgressBar
                pbarProgreso.setProgress(0); // Empieza vacía ProgressBar
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pbarProgreso.post(new Runnable() {
                            @Override
                            public void run() {
                                pbarProgreso.setProgress(0);
                            }
                        });
                        for(int i=1;i<=10;i++) {
                            tareaLarga();
                            pbarProgreso.post(new Runnable() {
                                @Override
                                public void run() {
                                    pbarProgreso.incrementProgressBy(10);
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Tarea Finalizada!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();


                /*for(int i=1; i<=10; i++) {
                    tareaLarga();
                    pbarProgreso.incrementProgressBy(10); //incremento
                }
                Toast.makeText(getApplicationContext(), "Tarea finalizada!",
                        Toast.LENGTH_SHORT).show();
                */


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }


    public void descargar(View button)
    {
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://2.bp.blogspot.com/-SbC1w7EL1QY/UqtTuggr-rI/AAAAAAAAKww/RItiEd3v-l0/s1600/i-see-no-god-only-doge.jpg"));
        //podemos limitar la descarga a un tipo de red (opcional)
        //IMPORTANTE: esta opción la comento porque da problemas en el emulador
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //esta información  se mostrará en el área de notificaciones
        request.setTitle("Descarga");
        request.setDescription("Prueba del servicio Download Manager.");

        //vamos a guardar el fichero (opcional). ver tip 5
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            request.setDestinationInExternalFilesDir(this, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tipsAndroid",System.currentTimeMillis() + "imagen.png");
        }

        //iniciamos la descarga
        id = downloadManager.enqueue(request);

    }

    //muestra las últimas descargas realizadas con el servicio
    public void ver(View button)
    {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);
    }



    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        //gestionamos la finalización de la descarga
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id, 0);
            Cursor cursor = downloadManager.query(query);

            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //podemos recuperar el fichero descargado
                    ParcelFileDescriptor file = null;
                    try {
                        file = downloadManager.openDownloadedFile(id);
                        Toast.makeText(MainActivity.this, "Fichero obtenido con éxito!! ", Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException ex) {
                        Toast.makeText(MainActivity.this, "Exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if (status == DownloadManager.STATUS_FAILED) {
                    Toast.makeText(MainActivity.this, "FAILED: " + reason, Toast.LENGTH_LONG).show();
                }
                else if (status == DownloadManager.STATUS_PAUSED) {
                    Toast.makeText(MainActivity.this, "PAUSED: " + reason, Toast.LENGTH_LONG).show();
                }
                else if (status == DownloadManager.STATUS_PENDING) {
                    Toast.makeText(MainActivity.this, "PENDING: " + reason, Toast.LENGTH_LONG).show();

                }
                else if (status == DownloadManager.STATUS_RUNNING) {
                    Toast.makeText(MainActivity.this, "RUNNING: " + reason, Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    private void tareaLarga() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) { }
    }

    private class AsyncDescarga extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "descargando...", Toast.LENGTH_SHORT).show();

            //pDialog = new ProgressDialog(MainActivity.this);
            //pDialog.setMessage("Loading... Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Boolean doInBackground(String... url) {
            downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://wallpapercave.com/wp/wp1848549.jpg"));
            request.setTitle("Descarga");
            request.setDescription("Prueba del servicio Download Manager.");

            //vamos a guardar el fichero (opcional). ver tip 5
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                request.setDestinationInExternalFilesDir(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tipsAndroid",System.currentTimeMillis() + "danielme.png");
            }

            //iniciamos la descarga
            id = downloadManager.enqueue(request);
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(MainActivity.this, "descargado!", Toast.LENGTH_SHORT).show();
            }
            //pDialog.dismiss();
        }

    }


    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {
        boolean cancelado;
        @Override
        protected Boolean doInBackground(Void... params) {
            for(int i=1; i<=10; i++) {
                tareaLarga();
                publishProgress(i*10);
                if(cancelado)
                    break;
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pbarProgreso.setProgress(progreso);
        }
        @Override
        protected void onPreExecute() {
            pbarProgreso.setMax(100);
            pbarProgreso.setProgress(0);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
                Toast.makeText(MainActivity.this, "Tarea finalizada!",
                        Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onCancelled(Boolean result) {
            if(!result) {
                Toast.makeText(MainActivity.this, "Tarea cancelada!",
                        Toast.LENGTH_SHORT).show();

            }
        }

    }





    private Button btnAsyncTask;
    private Button btnHilo;
    private Button btnSinHilos;
    private Button btnLimpiar;
    private Button btnAsyncDescarga;
    private ProgressBar pbarProgreso;
    private MiTareaAsincrona tarea1;
    private ProgressDialog pDialog;
    private AsyncDescarga asyncdescarga;

}
