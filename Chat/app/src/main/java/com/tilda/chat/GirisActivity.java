package com.tilda.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GirisActivity extends AppCompatActivity {
    EditText eKullaniciAdi, eSifre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        eKullaniciAdi = findViewById(R.id.eKullaniciAdi);
        eSifre = findViewById(R.id.eSifre);
    }

    public void btnGiris(View v) {
        String kullanici_adi = eKullaniciAdi.getText().toString();
        String sifre = eSifre.getText().toString();
        if (!kullanici_adi.equals("") && !sifre.equals("")) {
            GirisYap gorev = new GirisYap();
            gorev.execute(kullanici_adi, sifre);
        } else {
            Toast.makeText(this, "Kullanıcı adı veya şifre boş bırakılamaz!", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnKaydol(View v) {
        String kullanici_adi = eKullaniciAdi.getText().toString();
        String sifre = eSifre.getText().toString();
        if (!kullanici_adi.equals("") && !sifre.equals("")) {
            KullaniciKaydiYap gorev = new KullaniciKaydiYap();
            gorev.execute(kullanici_adi, sifre);
        }
        else {
            Toast.makeText(this, "Kullanıcı adı veya şifre boş bırakılamaz!", Toast.LENGTH_SHORT).show();
        }
    }

    class KullaniciKaydiYap extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String kullaniciAdi = strings[0];
                String sifre = strings[1];
                URL adres = new URL("http://www.tildaweb.com/chat/kullaniciEkle.php?kullanici_adi="+kullaniciAdi+"&sifre="+sifre);
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
            Toast.makeText(GirisActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    class GirisYap extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String kullaniciAdi = strings[0];
                String sifre = strings[1];
                URL adres = new URL("http://www.tildaweb.com/chat/giris.php?kullanici_adi="+kullaniciAdi+"&sifre="+sifre);
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
            Toast.makeText(GirisActivity.this, s, Toast.LENGTH_SHORT).show();
            if(s.equals("true")) {
                Intent i = new Intent(GirisActivity.this, MesajlarActivity.class);
                i.putExtra("kullaniciAdi", eKullaniciAdi.getText().toString());
                startActivity(i);

                eKullaniciAdi.setText("");
                eSifre.setText("");
            }
        }
    }
}
