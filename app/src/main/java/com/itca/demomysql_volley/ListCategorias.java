package com.itca.demomysql_volley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itca.demomysql_volley.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListCategorias extends AppCompatActivity {

    android.widget.ListView listaCat;
    ArrayList<String> notas;
    ArrayAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categorias);

        listaCat = findViewById(R.id.listCategorias);

        traerCategorias();
    }

    private void traerCategorias(){

        ArrayList<String> lista = new ArrayList<>();
        ArrayList<dto_categorias> listaCategorias = new ArrayList<>();

        String url = "http://defv.freeoda.com/buscar_categorias.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONArray array = new JSONArray(response);
                    int totalencontrados = array.length();
                    dto_categorias obj_categorias = null;

                    for(int i = 0; i < array.length(); i++){

                        JSONObject categoriasObjet = array.getJSONObject(i);

                        int id_categoria = categoriasObjet.getInt("id_categoria");
                        String nombre = categoriasObjet.getString("nom_categoria");
                        int estado = Integer.parseInt(categoriasObjet.getString("estado_categoria"));

                        obj_categorias = new dto_categorias(id_categoria, nombre, estado);
                        listaCategorias.add(obj_categorias);
                        lista.add(listaCategorias.get(i).getNom_categoria());

                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lista);
                        listaCat.setAdapter(adapter);
                        listaCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                Intent intent = new Intent(ListCategorias.this, HomeFragment.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("lista", (ArrayList<String>) listaCat.getItemAtPosition(i));
                                bundle.putString("senal", "1");
                                //String categorias = (String) listaCat.getItemAtPosition(i);
                                //intent.putExtra("id", categorias);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        Log.i("id_categoria", String.valueOf(obj_categorias.getId_categoria()));
                        Log.i("nombre_categoria", String.valueOf(obj_categorias.getId_categoria()));
                        Log.i("estado_categoria", String.valueOf(obj_categorias.getId_categoria()));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error: compruebe su conexión a internet", Toast.LENGTH_SHORT).show();

            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}