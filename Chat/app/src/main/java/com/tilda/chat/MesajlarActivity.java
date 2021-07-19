package com.tilda.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MesajlarActivity extends AppCompatActivity {
    ArrayList<String> mesajArrayList;
    ArrayAdapter<String> arrayAdapter;
    ListView liste;
    String kullanici_adi;
    EditText eMesaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesajlar);
        liste = findViewById(R.id.liste);
        eMesaj = findViewById(R.id.editText);

        mesajArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mesajArrayList);
        liste.setAdapter(arrayAdapter);

        kullanici_adi = getIntent().getStringExtra("kullaniciAdi");
        setTitle(kullanici_adi);

        listele();

        CountDownTimer timer = new CountDownTimer(50000000,5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                listele();
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.item_yenile){
            listele();
        }
        else if(item.getItemId()==R.id.item_cikis){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void listele(){
        MesajlariCek gorev = new MesajlariCek();
        gorev.execute();
    }
    public void btnGonder(View v){
        MesajGonder gorev = new MesajGonder();
        gorev.execute(kullanici_adi, eMesaj.getText().toString());
        eMesaj.setText("");
    }

    class MesajlariCek extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL adres = new URL("http://www.tildaweb.com/chat/listele.php");
                HttpURLConnection baglanti = (HttpURLConnection) adres.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(baglanti.getInputStream()));
                String sonuc = "";
                String satir = "";
                while ((satir = br.readLine()) != null) {
                    sonuc += satir;
                }
                br.close();
                baglanti.disconnect();
                return sonuc;
            } catch (Exception ex) {
                System.out.println("Hata : " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray mesajlar = new JSONArray(s);
                mesajArrayList.clear();
                for (int i = 0 ; i<mesajlar.length(); i++){
                    JSONObject m = mesajlar.getJSONObject(i);
                    String gonderen = m.getString("kullanici_adi");
                    String mesaj = m.getString("mesaj");
                    mesajArrayList.add("[" + gonderen + "] :  " + mesaj);
                }
                arrayAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class MesajGonder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String kullaniciAdi = strings[0];
                String mesaj = strings[1];
                URL adres = new URL("http://www.tildaweb.com/chat/ekle.php?kullanici_adi="+kullaniciAdi+"&mesaj="+mesaj);
                HttpURLConnection baglanti = (HttpURLConnection) adres.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(baglanti.getInputStream()));
                String sonuc = "";
                String satir = "";
                while ((satir = br.readLine()) != null) {
                    sonuc += satir;
                }
                br.close();
                baglanti.disconnect();
                return sonuc;
            } catch (Exception ex) {
                System.out.println("Hata : " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MesajlarActivity.this, s, Toast.LENGTH_SHORT).show();
            listele();
        }
    }
}
