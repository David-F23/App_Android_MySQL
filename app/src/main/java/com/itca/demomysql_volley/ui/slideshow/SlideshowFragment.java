package com.itca.demomysql_volley.ui.slideshow;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.itca.demomysql_volley.databinding.FragmentSlideshowBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private EditText id, nombre, apellido, correo, usuario, clave, pregunta, respuesta;
    private TextView fechaHora;
    private Spinner estado, tipo;
    private Button btnSave, btnNew;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        id = view.findViewById(R.id.id_usuario);
        nombre = view.findViewById(R.id.nombre_usuario);
        apellido = view.findViewById(R.id.apellido_usuario);
        correo = view.findViewById(R.id.correo);
        usuario = view.findViewById(R.id.usuario);
        clave = view.findViewById(R.id.clave);
        tipo = view.findViewById(R.id.tipo_usuario);
        estado = view.findViewById(R.id.estado_usuario);
        pregunta = view.findViewById(R.id.pregunta);
        respuesta = view.findViewById(R.id.respueta);

        btnSave = view.findViewById(R.id.btnSaveUser);
        btnNew = view.findViewById(R.id.btnNewUser);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.estadoUsers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estado.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.tipoUser, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id_user = id.getText().toString();
                String name = nombre.getText().toString();
                String apellidos = apellido.getText().toString();
                String email = correo.getText().toString();
                String user = usuario.getText().toString();
                String contra = clave.getText().toString();
                String tipo_us = tipo.getSelectedItem().toString();
                String estado_us = estado.getSelectedItem().toString();
                String pregunt = pregunta.getText().toString();
                String resp = respuesta.getText().toString();

                if(id_user.length() == 0){
                    id.setError("Campo obligatorio");

                }else if(name.length() == 0){
                    nombre.setError("Campo obligatorio");

                }else if(apellidos.length() == 0){
                    apellido.setError("Campo obligatorio");

                }else if(email.length() == 0){
                    correo.setError("Campo obligatorio");

                }else if(user.length() == 0){
                    usuario.setError("Campo obligatorio");

                }else if(contra.length() == 0){
                    clave.setError("Campo obligatorio");

                }else if(tipo.getSelectedItemPosition() == 0){
                    Toast.makeText(getContext(), "Debe selecionar el tipo", Toast.LENGTH_SHORT).show();

                }else if(estado.getSelectedItemPosition() == 0){
                    Toast.makeText(getContext(), "Debe selecionar el estado", Toast.LENGTH_SHORT).show();

                }else if(pregunt.length() == 0){
                    pregunta.setError("Campo obligatorio");

                }else if(resp.length() == 0){
                    respuesta.setError("Campo obligatorio");
                }else{

                    save_users(getContext(), id_user, name, apellidos, email, user, contra, tipo_us, estado_us, pregunt, resp);
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUser();
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

    private void save_users(final Context context, final String id, final String nombre, final String apellido, final String correo, final String usuario, final String clave, final String tipo, final String estado, final String pregunta, final String respuesta) {

        String url = "http://defv.freeoda.com/guardar_users.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject requestJSON = null;

                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if (estado.equals("1")) {
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    } else if (estado.equals("2")) {
                        Toast.makeText(context, "" + mensaje, Toast.LENGTH_SHORT).show();
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
        }) {

            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", id);
                map.put("nombre", nombre);
                map.put("apellido", apellido);
                map.put("correo", correo);
                map.put("usuario", usuario);
                map.put("clave", clave);
                map.put("tipo", tipo);
                map.put("estado", estado);
                map.put("pregunta", pregunta);
                map.put("respuesta", respuesta);

                return map;

            }

        };

        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void newUser(){
        id.setText(null);
        nombre.setText(null);
        apellido.setText(null);
        correo.setText(null);
        usuario.setText(null);
        clave.setText(null);
        pregunta.setText(null);
        respuesta.setText(null);
        estado.setSelection(0);
        tipo.setSelection(0);
    }

}