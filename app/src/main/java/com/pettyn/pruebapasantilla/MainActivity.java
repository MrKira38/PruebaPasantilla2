package com.pettyn.pruebapasantilla;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Types;

import static java.sql.Types.INTEGER;
import static java.sql.Types.NULL;

public class MainActivity extends AppCompatActivity {

    Boolean bandera = false;
    int num, actual, banderaBTN;
    TextView NumeroC, DescripcionC, Cargando, Anterior, Siguiente;
    ImageView ImagenC;
    ProgressBar progressBar;
    Button btnSGT, btnANT, btnBuscador;
    EditText Buscador;
    String[] objetos = new String[3];
    String URL = "https://xkcd.com/info.0.json";
    JSONObject JSONtextos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        Cargando = (TextView) findViewById(R.id.txtCargando);
        btnSGT = (Button) findViewById(R.id.btnSgt);
        btnANT = (Button) findViewById(R.id.btnAnt);
        Anterior = (TextView) findViewById(R.id.txtANT);
        Siguiente = (TextView) findViewById(R.id.txtSGT);
        Buscador = (EditText) findViewById(R.id.txtBuscaNum);
        btnBuscador = (Button) findViewById(R.id.buscaNum);

        new AsyncTaskExample().execute(URL);

        btnANT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banderaBTN = 0;
                num--;
                URL = "https://xkcd.com/" + num + "/info.0.json";
                new AsyncTaskExample().execute(URL);
            }
        });

        btnSGT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banderaBTN = 1;
                num++;
                URL = "https://xkcd.com/" + num + "/info.0.json";
                new AsyncTaskExample().execute(URL);
            }
        });

        btnBuscador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Buscador.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "El campo no puede estar vacio", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    if (Integer.parseInt(Buscador.getText().toString()) > actual) {
                        Toast toast = Toast.makeText(getApplicationContext(), "este comic no existe aun", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        banderaBTN = 2;
                        num = Integer.parseInt(Buscador.getText().toString());
                        URL = "https://xkcd.com/" + num + "/info.0.json";
                        new AsyncTaskExample().execute(URL);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    public class AsyncTaskExample extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            Cargando.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... url) {
            try {
                JSONtextos = JsonParser.readJsonFromUrl(URL);
                objetos[0] = JSONtextos.getString("num");
                objetos[1] = JSONtextos.getString("alt");
                objetos[2] = JSONtextos.getString("img");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                objetos[2] = "";
            }
            return objetos;
        }

        @Override
        protected void onPostExecute(String[] stringFromDoInBackground) {
            NumeroC = (TextView) findViewById(R.id.txtNumero);
            DescripcionC = (TextView) findViewById(R.id.txtDescripcion);
            ImagenC = (ImageView) findViewById(R.id.ImagenComic);

            if (stringFromDoInBackground[2].equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(), "El comic " +num+ " esta vacio", Toast.LENGTH_SHORT);
                toast.show();
                if (banderaBTN == 1)
                    btnANT.performClick();
                else if (banderaBTN == 2)
                    btnSGT.performClick();
                else if (banderaBTN == 3);
                    num = actual;
            }
            else {
                NumeroC.setText("Numero de comic: " + stringFromDoInBackground[0]);
                DescripcionC.setText(stringFromDoInBackground[1]);
                Picasso.with(MainActivity.this).load(stringFromDoInBackground[2]).into(ImagenC);

                progressBar.setVisibility(View.GONE);
                Cargando.setVisibility(View.GONE);
                Buscador.setVisibility(View.VISIBLE);
                btnBuscador.setVisibility(View.VISIBLE);

                if (num == 1) {
                    btnANT.setVisibility(View.GONE);
                    Anterior.setVisibility(View.GONE);
                } else {
                    btnANT.setVisibility(View.VISIBLE);
                    Anterior.setVisibility(View.VISIBLE);
                }

                if (num == actual) {
                    btnSGT.setVisibility(View.GONE);
                    Siguiente.setVisibility(View.GONE);
                } else {
                    btnSGT.setVisibility(View.VISIBLE);
                    Siguiente.setVisibility(View.VISIBLE);
                }

                num = Integer.parseInt(stringFromDoInBackground[0]);
                if (!bandera) {
                    actual = num;
                    bandera = true;
                }
            }
        }
    }
}
