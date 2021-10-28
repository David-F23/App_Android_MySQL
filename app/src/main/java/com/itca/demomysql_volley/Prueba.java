package com.itca.demomysql_volley;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Prueba extends AppCompatActivity {

    private EditText et_idcategoria, et_namecategoria;
    private Spinner sp_estado;
    private Button btnSave, btnNew;

    String datoSelect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        et_idcategoria = findViewById(R.id.et_idcategoria1);
        et_namecategoria = findViewById(R.id.et_namecategoria1);
        sp_estado = findViewById(R.id.sp_estado1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.estadoCategorias, android.R.layout.simple_spinner_item);
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

        btnSave = findViewById(R.id.btnSave1);
        btnNew = findViewById(R.id.btnNew1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = et_idcategoria.getText().toString();
                String nombre = et_namecategoria.getText().toString();
                String estado = sp_estado.getSelectedItem().toString();

                if (id.length() == 0){
                    et_idcategoria.setError("Campo obligatorio");

                }else if(et_namecategoria.length() == 0){
                    et_namecategoria.setError("Campo obligatorio");

                }else if(sp_estado.getSelectedItemPosition() > 0){
                    Toast.makeText(getApplicationContext(), id+nombre+estado, Toast.LENGTH_SHORT).show();
                    save_server(getApplicationContext(), Integer.parseInt(id), nombre, Integer.parseInt(datoSelect));
                    //new Metodo(getContext()).execute(id, nombre, estado);
                }else{
                    Toast.makeText(getApplicationContext(), "Debe selecionar un estado de categoría", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_categories();
            }
        });
    }




    public void save_server(final Context context, final int id_categoria, final String nom_categoria, final int estado_categoria){

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
                Toast.makeText(getApplicationContext(), "No se pudo guardar.\nIntentelo mas tarde", Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                //map.put("Content-Type","application/json; charset=utf-8");
                //map.put("Accept","application/json");
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
}