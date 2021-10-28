package com.itca.demomysql_volley.ui.gallery;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.itca.demomysql_volley.MySingleton;
import com.itca.demomysql_volley.R;
import com.itca.demomysql_volley.databinding.FragmentGalleryBinding;
import com.itca.demomysql_volley.dto_categorias;
import com.itca.demomysql_volley.dto_productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    //ESTE FRAGMENT REPRESENTA LOS PRODUCTOS

    private EditText id_producto, nombre_producto, descri_producto, et_stock, et_precio, unidad_medida;
    private Spinner estado_producto, categoria_producto;
    private TextView tv_fechahora;
    private Button btnSave, btnNew;

    ProgressDialog progressDialog;
    ArrayList<String> lista = null;
    ArrayList<dto_categorias> listaCategorias;

    String elementos[] = {"uno", "dos", "tres", "cuatro", "cinco"};
    final String[] elementos1 = new String[]{
            "Selecione",
            "1",
            "2",
            "3",
            "4",
            "5"
    };

    String id_categoria = "";
    String nombre_categoria = "";
    int cont = 0;

    String datoStatusProduct = "";
    dto_productos dto = new dto_productos();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        id_producto = view.findViewById(R.id.id_producto);
        nombre_producto = view.findViewById(R.id.nombre_producto);
        descri_producto = view.findViewById(R.id.descri_producto);
        et_stock = view.findViewById(R.id.stock);
        et_precio = view.findViewById(R.id.precio);
        unidad_medida = view.findViewById(R.id.unidadMedida);
        estado_producto = view.findViewById(R.id.estado_producto);
        tv_fechahora = view.findViewById(R.id.tv_fechahora);
        btnSave = view.findViewById(R.id.btnSave);
        btnNew = view.findViewById(R.id.btnNew);
        categoria_producto = view.findViewById(R.id.categoria_producto);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.estadoProductos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estado_producto.setAdapter(adapter);

        estado_producto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(estado_producto.getSelectedItemPosition() > 0){
                    datoStatusProduct = estado_producto.getSelectedItem().toString();
                }else{
                    datoStatusProduct = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fk_categorias(getContext());

        categoria_producto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(cont >= 1 && categoria_producto.getSelectedItemPosition() > 0){
                    String itemSpinner = categoria_producto.getSelectedItem().toString();
                    String s[] = itemSpinner.split("~");
                    id_categoria = s[0].trim();
                    nombre_categoria = s[1];
                    Toast.makeText(getContext(), "Id cat: " + id_categoria + "\nNombre: "+ nombre_categoria, Toast.LENGTH_SHORT).show();

                }else{
                    id_categoria = "";
                    nombre_categoria = "";
                }
                cont++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = id_producto.getText().toString();
                String nombre = nombre_producto.getText().toString();
                String descripcion = descri_producto.getText().toString();
                String stock = et_stock.getText().toString();
                String precio = et_precio.getText().toString();
                String unidad = unidad_medida.getText().toString();

                if(id.length() == 0){
                    id_producto.setError("Campo Obligatorio");

                }else if(nombre.length() == 0){
                    nombre_producto.setError("Campo Obligatorio");

                }else if(descripcion.length() == 0){
                    descri_producto.setError("Campo Obligatorio");

                }else if(stock.length() == 0){
                    et_stock.setError("Campo Obligatorio");

                }else if(precio.length() == 0){
                    et_precio.setError("Campo Obligatorio");

                }else if(unidad.length() == 0){
                    unidad_medida.setError("Campo Obligatorio");

                }else if(estado_producto.getSelectedItemPosition() == 0){
                    Toast.makeText(getContext(), "Debe selecionar el estado eel producto", Toast.LENGTH_SHORT).show();

                }else if(categoria_producto.getSelectedItemPosition() > 0){

                    save_productos(getContext(), Integer.parseInt(id), nombre, descripcion, stock, precio, unidad, datoStatusProduct, id_categoria);

                }else{
                    Toast.makeText(getContext(), "Debe selecionar la categorpia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newProduct();
            }
        });

        return view;
    }

    private String timedate(){

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        String fecha = sdf.format(cal.getTime());
        return fecha;
    }

    public void fk_categorias(final Context context){

        listaCategorias = new ArrayList<dto_categorias>();
        lista = new ArrayList<String>();
        lista.add("Selecione la categoria");

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
                        lista.add(listaCategorias.get(i).getId_categoria() + " ~ " + listaCategorias.get(i).getNom_categoria());

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lista);
                        categoria_producto.setAdapter(adapter);

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

                Toast.makeText(getContext(), "Error: compruebe su conexión a internet", Toast.LENGTH_SHORT).show();

            }
        });

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void save_productos(final Context context, final int id_prod, final String nom_prod, final String des_prod, final String stock, final String precio, final String uni_medida, final String estado_prod, final String categoria){

        String url = "http://defv.freeoda.com/guardar_productos.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject requestJSON = null;

                try{
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if(estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    }else if(estado.equals("2")){
                        Toast.makeText(context, ""+mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No se pudo guardar. \nIntentelo más tarde.", Toast.LENGTH_SHORT).show();
            }

        }){

            protected Map<String, String> getParams() throws AuthFailureError{

                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id_prod", String.valueOf(id_prod));
                map.put("nom_prod", nom_prod);
                map.put("des_prod", des_prod);
                map.put("stock", stock);
                map.put("precio", precio);
                map.put("uni_medida", uni_medida);
                map.put("estado_prod", estado_prod);
                map.put("categoria", categoria);

                return map;
            }

        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void newProduct(){
        id_producto.setText(null);
        nombre_producto.setText(null);
        descri_producto.setText(null);
        et_precio.setText(null);
        et_stock.setText(null);
        unidad_medida.setText(null);
        estado_producto.setSelection(0);
        categoria_producto.setSelection(0);
    }
}