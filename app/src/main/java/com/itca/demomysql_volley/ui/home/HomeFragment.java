package com.itca.demomysql_volley.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itca.demomysql_volley.ListCategorias;
import com.itca.demomysql_volley.Login;
import com.itca.demomysql_volley.MySingleton;
import com.itca.demomysql_volley.R;
import com.itca.demomysql_volley.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{

    //ESTE FRAGMENT REPRESENTA LAS CATEGORÍAS

    private EditText et_idcategoria, et_namecategoria;
    private Spinner sp_estado;
    private Button btnSave, btnNew, verCat;

    String datoSelect = "";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /*ArrayList<String> objeto = getArguments().getStringArrayList("lista");
        String o = getArguments().getString("lista");

        et_namecategoria.setText(o);

        //String notas = getIntent().getStringExtra("titulo");

        traercategoria(getContext(), 1);*/

        et_idcategoria = view.findViewById(R.id.et_idcategoria);
        et_namecategoria = view.findViewById(R.id.et_namecategoria);
        sp_estado = view.findViewById(R.id.sp_estado);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.estadoCategorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_estado.setAdapter(adapter);

        sp_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_estado.getSelectedItemPosition() > 0){
                    datoSelect = sp_estado.getSelectedItem().toString();
                }else{
                    datoSelect = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave = view.findViewById(R.id.btnSave);
        btnNew = view.findViewById(R.id.btnNew);
        verCat = view.findViewById(R.id.verCat);
        btnNew.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        verCat.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnSave:
                String id = et_idcategoria.getText().toString();
                String nombre = et_namecategoria.getText().toString();
                String estado = sp_estado.getSelectedItem().toString();

                if (id.length() == 0){
                    et_idcategoria.setError("Campo obligatorio");

                }else if(et_namecategoria.length() == 0){
                    et_namecategoria.setError("Campo obligatorio");

                }else if(sp_estado.getSelectedItemPosition() > 0){
                    Toast.makeText(getContext(), id+nombre+estado, Toast.LENGTH_SHORT).show();
                    save_server(getContext(), Integer.parseInt(id), nombre, Integer.parseInt(estado));
                    //new Metodo(getContext()).execute(id, nombre, estado);
                }else{
                    Toast.makeText(getContext(), "Debe selecionar un estado de categoría", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnNew:
                new_categories();
                break;

            case R.id.verCat:
                Intent intent = new Intent(getContext(), ListCategorias.class);
                startActivity(intent);

            default:
                Toast.makeText(getContext(), "hola", Toast.LENGTH_SHORT).show();
        }
    }


    private void save_server(final Context context, final int id_categoria, final String nom_categoria, final int estado_categoria){

        String url = "http://defv.freeoda.com/guardar_categorias.php";
        //http://162.253.155.225/guardar_categorias.php

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONObject requestJSON = null;

                try{

                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if(estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    }else if (estado.equals("2")){
                        Toast.makeText(context, "" + mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    e.getMessage();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                error.getMessage();
                Toast.makeText(getContext(), "No se pudo guardar.\nIntentelo mas tarde", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type","application/json; charset=utf-8");
                map.put("Accept","application/json");
                map.put("id", String.valueOf(id_categoria));
                map.put("nombre", nom_categoria);
                map.put("estado", String.valueOf(estado_categoria));
                return map;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void new_categories(){
        et_idcategoria.setText(null);
        et_namecategoria.setText(null);
        sp_estado.setSelection(0);
    }

    /*private void traercategoria(final Context context, final int id){

        String url = "http://defv.freeoda.com/traerCat.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONArray cat = new JSONArray(response);

                    for(int i = 0; i < cat.length(); i++){

                        JSONObject respuestaJSON = cat.getJSONObject(i);
                        String id = respuestaJSON.getString("id_categoria");
                        String nombre = respuestaJSON.getString("nom_categoria");
                        String apellidos = respuestaJSON.getString("estado_categoria");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: problemas con la conexiòn al servidor", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                HashMap<String,String> parametros = new HashMap<>();
                parametros.put("id", String.valueOf(id));
                return parametros;
            }
        };
    }*/
}