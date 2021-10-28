package com.itca.demomysql_volley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    String URL_PRUEBA = "https://mjgl.com.sv/pruebaVolley/test.php";
    private TextView textView;
    private Button btn1,btnRegistrarse, btnrecuperar;
    private EditText user, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas_volley);

        textView = findViewById(R.id.tvres);
        btn1 = findViewById(R.id.btn1);
        btnRegistrarse = findViewById(R.id.btnregistrar);
        btnrecuperar = findViewById(R.id.btnrecuperarClave);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recibirJson();
                //pruebaVolley();
                //mensaje("hola");

                if(user.length() == 0){

                    user.setError("Campo obligatorio");

                }else if(pass.length() == 0){

                    pass.setError("Campo obligatorio");

                }else{

                    login(getApplicationContext(), user.getText().toString(), pass.getText().toString());

                }

            }
        });

        btnrecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, "Registrar", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, "Recuperar clave", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void baseRequest(){

        StringRequest request = new StringRequest(Request.Method.GET, URL_PRUEBA, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", "1");

                return map;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void pruebaVolley(){

        String url = "https://mjgl.com.sv/pruebaVolley/test.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System .out.println(response.substring(0, 16));
                        textView.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(Login.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /*private void peticionJson(){

        String url = "https://mjgl.com.sv/pruebaVolley/test.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                textView.setText("Response: " + response.toString());
                Toast.makeText(getApplicationContext(), "" + response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        })

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }*/

    private void recibirJson(){
        StringRequest request = new StringRequest(Request.Method.GET, URL_PRUEBA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respuestaJSON = new JSONObject(response.toString());

                    String valor1 = respuestaJSON.getString("id");
                    String valor2 = respuestaJSON.getString("nombre");
                    textView.setText("Response: " + response.toString());

                    Toast.makeText(getApplicationContext(), "id: " + valor1 + "\nNombre: " + valor2, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public void mensaje(String s){
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    private void login(final Context context, final String user, final String pass){

        String url = "http://defv.freeoda.com/login.php";
        //String url = "https://mjgl.com.sv/ws_2021/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")){
                    Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        JSONArray res = new JSONArray(s);

                        for(int i = 0; i < res.length(); i++){

                            JSONObject respuestaJSON = res.getJSONObject(i);
                            String id = respuestaJSON.getString("id");
                            String nombre = respuestaJSON.getString("nombre");
                            String apellidos = respuestaJSON.getString("apellidos");
                            String correo = respuestaJSON.getString("correo");
                            String usuario = respuestaJSON.getString("usuario");
                            String clave = respuestaJSON.getString("clave");
                            String tipo = respuestaJSON.getString("tipo");
                            String estado = respuestaJSON.getString("estado");
                            String pregunta = respuestaJSON.getString("pregunta");
                            String respuesta = respuestaJSON.getString("respuesta");
                            String fecha_registro = respuestaJSON.getString("fecha_registro");

                        }


                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(Login.this, s.toString(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Login.this, "Error: problemas con la conexiòn al servidor", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                HashMap<String,String> parametros = new HashMap<>();
                parametros.put("usu", user.trim());
                parametros.put("pas", pass.trim());

                return parametros;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}