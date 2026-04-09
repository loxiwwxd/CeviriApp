package com.ebrar.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslateActivity extends AppCompatActivity {

    
    private final LinkedHashMap<String, String> enToTrDict = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> trToEnDict = new LinkedHashMap<>();

    private boolean isEnToTr = true;

    private EditText editSearch;
    private Button btnTranslate, btnEnToTr, btnTrToEn;
    private CardView cardResult;
    private TextView tvSourceWord, tvTranslation, tvListTitle;
    private ListView listWords;
    private ProgressBar progressBar;

    private List<String[]> filteredList = new ArrayList<>();
    private WordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        initDictionary();
        initViews();
        setupListeners();
        updateList("");
    }

    private void initViews() {
        editSearch = findViewById(R.id.editSearch);
        btnTranslate = findViewById(R.id.btnTranslate);
        btnEnToTr = findViewById(R.id.btnEnToTr);
        btnTrToEn = findViewById(R.id.btnTrToEn);
        cardResult = findViewById(R.id.cardResult);
        tvSourceWord = findViewById(R.id.tvSourceWord);
        tvTranslation = findViewById(R.id.tvTranslation);
        tvListTitle = findViewById(R.id.tvListTitle);
        listWords = findViewById(R.id.listWords);
        progressBar = findViewById(R.id.progressBar);

        adapter = new WordAdapter();
        listWords.setAdapter(adapter);
    }

    private void setupListeners() {
        btnEnToTr.setOnClickListener(v -> setDirection(true));
        btnTrToEn.setOnClickListener(v -> setDirection(false));
        btnTranslate.setOnClickListener(v -> doTranslate());

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        listWords.setOnItemClickListener((parent, view, position, id) -> {
            String[] pair = filteredList.get(position);
            editSearch.setText(pair[0]);
            doTranslate();
        });
    }

    private void setDirection(boolean enToTr) {
        isEnToTr = enToTr;
        if (isEnToTr) {
            btnEnToTr.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
            btnEnToTr.setTextColor(0xFFFFFFFF);
            btnTrToEn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBBDEFB));
            btnTrToEn.setTextColor(0xFF1565C0);
            editSearch.setHint("İngilizce kelime veya cümle yazın...");
            tvListTitle.setText("İngilizce → Türkçe Sözlük");
        } else {
            btnTrToEn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
            btnTrToEn.setTextColor(0xFFFFFFFF);
            btnEnToTr.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBBDEFB));
            btnEnToTr.setTextColor(0xFF1565C0);
            editSearch.setHint("Türkçe kelime veya cümle yazın...");
            tvListTitle.setText("Türkçe → İngilizce Sözlük");
        }
        editSearch.setText("");
        cardResult.setVisibility(View.GONE);
        updateList("");
    }

    private void doTranslate() {
        String input = editSearch.getText().toString().trim();
        if (input.isEmpty()) {
            cardResult.setVisibility(View.GONE);
            return;
        }

        // Önce yerel sözlükte ara
        String inputLower = input.toLowerCase(isEnToTr ? Locale.ENGLISH : new Locale("tr", "TR"));
        String localResult = null;
        LinkedHashMap<String, String> dict = isEnToTr ? enToTrDict : trToEnDict;

        for (Map.Entry<String, String> entry : dict.entrySet()) {
            if (entry.getKey().toLowerCase(isEnToTr ? Locale.ENGLISH : new Locale("tr", "TR")).equals(inputLower)) {
                localResult = entry.getValue();
                break;
            }
        }

        if (localResult != null) {
            
            showResult(input, localResult);
        } else {
            
            translateOnline(input);
        }
    }

    private void translateOnline(String text) {
        progressBar.setVisibility(View.VISIBLE);
        btnTranslate.setEnabled(false);
        cardResult.setVisibility(View.GONE);

        String langPair = isEnToTr ? "en|tr" : "tr|en";
        new TranslateTask(text, langPair).execute();
    }

    private void showResult(String source, String translation) {
        tvSourceWord.setText(source);
        tvTranslation.setText(translation);
        cardResult.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        btnTranslate.setEnabled(true);
    }

    
    private class TranslateTask extends AsyncTask<Void, Void, String> {
        private final String sourceText;
        private final String langPair;
        private String errorMsg = null;

        TranslateTask(String sourceText, String langPair) {
            this.sourceText = sourceText;
            this.langPair = langPair;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String encoded = URLEncoder.encode(sourceText, "UTF-8");
                String urlStr = "https://api.mymemory.translated.net/get?q=" + encoded + "&langpair=" + langPair;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    JSONObject responseData = json.getJSONObject("responseData");
                    return responseData.getString("translatedText");
                } else {
                    errorMsg = "Bağlantı hatası: " + code;
                    return null;
                }
            } catch (Exception e) {
                errorMsg = "Çeviri hatası: " + e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                showResult(sourceText, result);
            } else {
                showResult(sourceText, errorMsg != null ? errorMsg : "Çeviri yapılamadı ❌");
            }
        }
    }

    private void updateList(String filter) {
        filteredList.clear();
        LinkedHashMap<String, String> dict = isEnToTr ? enToTrDict : trToEnDict;
        Locale srcLocale = isEnToTr ? Locale.ENGLISH : new Locale("tr", "TR");
        Locale tgtLocale = isEnToTr ? new Locale("tr", "TR") : Locale.ENGLISH;
        String filterLower = filter.toLowerCase(srcLocale);

        for (Map.Entry<String, String> entry : dict.entrySet()) {
            if (filter.isEmpty()
                    || entry.getKey().toLowerCase(srcLocale).contains(filterLower)
                    || entry.getValue().toLowerCase(tgtLocale).contains(filterLower)) {
                filteredList.add(new String[]{entry.getKey(), entry.getValue()});
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class WordAdapter extends BaseAdapter {
        @Override
        public int getCount() { return filteredList.size(); }
        @Override
        public Object getItem(int position) { return filteredList.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(TranslateActivity.this)
                        .inflate(R.layout.item_word, parent, false);
            }
            String[] pair = filteredList.get(position);
            TextView tvWord = convertView.findViewById(R.id.tvWord);
            TextView tvMeaning = convertView.findViewById(R.id.tvMeaning);
            tvWord.setText(pair[0]);
            tvMeaning.setText(pair[1]);
            return convertView;
        }
    }

    private void initDictionary() {
      
        enToTrDict.put("Hello", "Merhaba");
        enToTrDict.put("Goodbye", "Hoşça kal");
        enToTrDict.put("Good morning", "Günaydın");
        enToTrDict.put("Good evening", "İyi akşamlar");
        enToTrDict.put("Good night", "İyi geceler");
        enToTrDict.put("Please", "Lütfen");
        enToTrDict.put("Thank you", "Teşekkür ederim");
        enToTrDict.put("Thanks", "Teşekkürler");
        enToTrDict.put("Yes", "Evet");
        enToTrDict.put("No", "Hayır");
        enToTrDict.put("Sorry", "Özür dilerim");
        enToTrDict.put("Excuse me", "Pardon");
        enToTrDict.put("Welcome", "Hoş geldiniz");
        enToTrDict.put("How are you", "Nasılsınız");
        enToTrDict.put("I am fine", "İyiyim");
        enToTrDict.put("See you later", "Görüşürüz");

      
        enToTrDict.put("One", "Bir");
        enToTrDict.put("Two", "İki");
        enToTrDict.put("Three", "Üç");
        enToTrDict.put("Four", "Dört");
        enToTrDict.put("Five", "Beş");
        enToTrDict.put("Six", "Altı");
        enToTrDict.put("Seven", "Yedi");
        enToTrDict.put("Eight", "Sekiz");
        enToTrDict.put("Nine", "Dokuz");
        enToTrDict.put("Ten", "On");

     
        enToTrDict.put("Red", "Kırmızı");
        enToTrDict.put("Blue", "Mavi");
        enToTrDict.put("Green", "Yeşil");
        enToTrDict.put("Yellow", "Sarı");
        enToTrDict.put("White", "Beyaz");
        enToTrDict.put("Black", "Siyah");
        enToTrDict.put("Orange", "Turuncu");
        enToTrDict.put("Purple", "Mor");
        enToTrDict.put("Pink", "Pembe");
        enToTrDict.put("Brown", "Kahverengi");

     
        enToTrDict.put("Cat", "Kedi");
        enToTrDict.put("Dog", "Köpek");
        enToTrDict.put("Bird", "Kuş");
        enToTrDict.put("Fish", "Balık");
        enToTrDict.put("Horse", "At");
        enToTrDict.put("Cow", "İnek");
        enToTrDict.put("Sheep", "Koyun");
        enToTrDict.put("Chicken", "Tavuk");
        enToTrDict.put("Rabbit", "Tavşan");
        enToTrDict.put("Lion", "Aslan");
        enToTrDict.put("Tiger", "Kaplan");
        enToTrDict.put("Bear", "Ayı");
        enToTrDict.put("Elephant", "Fil");
        enToTrDict.put("Monkey", "Maymun");
        enToTrDict.put("Snake", "Yılan");
        enToTrDict.put("Wolf", "Kurt");
        enToTrDict.put("Fox", "Tilki");
        enToTrDict.put("Turtle", "Kaplumbağa");
        enToTrDict.put("Duck", "Ördek");
        enToTrDict.put("Eagle", "Kartal");

       
        enToTrDict.put("Water", "Su");
        enToTrDict.put("Bread", "Ekmek");
        enToTrDict.put("Milk", "Süt");
        enToTrDict.put("Egg", "Yumurta");
        enToTrDict.put("Cheese", "Peynir");
        enToTrDict.put("Rice", "Pirinç");
        enToTrDict.put("Meat", "Et");
        enToTrDict.put("Fruit", "Meyve");
        enToTrDict.put("Apple", "Elma");
        enToTrDict.put("Banana", "Muz");
        enToTrDict.put("Tomato", "Domates");
        enToTrDict.put("Potato", "Patates");
        enToTrDict.put("Sugar", "Şeker");
        enToTrDict.put("Salt", "Tuz");
        enToTrDict.put("Tea", "Çay");
        enToTrDict.put("Coffee", "Kahve");
        enToTrDict.put("Soup", "Çorba");
        enToTrDict.put("Salad", "Salata");
        enToTrDict.put("Cake", "Pasta");
        enToTrDict.put("Ice cream", "Dondurma");
        enToTrDict.put("Chocolate", "Çikolata");

       
        enToTrDict.put("Mother", "Anne");
        enToTrDict.put("Father", "Baba");
        enToTrDict.put("Sister", "Kız kardeş");
        enToTrDict.put("Brother", "Erkek kardeş");
        enToTrDict.put("Baby", "Bebek");
        enToTrDict.put("Family", "Aile");
        enToTrDict.put("Friend", "Arkadaş");
        enToTrDict.put("Child", "Çocuk");
        enToTrDict.put("Teacher", "Öğretmen");
        enToTrDict.put("Student", "Öğrenci");
        enToTrDict.put("Doctor", "Doktor");

        
        enToTrDict.put("House", "Ev");
        enToTrDict.put("School", "Okul");
        enToTrDict.put("Hospital", "Hastane");
        enToTrDict.put("Restaurant", "Restoran");
        enToTrDict.put("Library", "Kütüphane");
        enToTrDict.put("Park", "Park");
        enToTrDict.put("Street", "Sokak");
        enToTrDict.put("City", "Şehir");
        enToTrDict.put("Country", "Ülke");
        enToTrDict.put("Beach", "Plaj");
        enToTrDict.put("Mountain", "Dağ");
        enToTrDict.put("Sea", "Deniz");
        enToTrDict.put("Garden", "Bahçe");
        enToTrDict.put("Room", "Oda");
        enToTrDict.put("Kitchen", "Mutfak");

       
        enToTrDict.put("To go", "Gitmek");
        enToTrDict.put("To come", "Gelmek");
        enToTrDict.put("To eat", "Yemek");
        enToTrDict.put("To drink", "İçmek");
        enToTrDict.put("To read", "Okumak");
        enToTrDict.put("To write", "Yazmak");
        enToTrDict.put("To speak", "Konuşmak");
        enToTrDict.put("To listen", "Dinlemek");
        enToTrDict.put("To run", "Koşmak");
        enToTrDict.put("To walk", "Yürümek");
        enToTrDict.put("To sleep", "Uyumak");
        enToTrDict.put("To work", "Çalışmak");
        enToTrDict.put("To play", "Oynamak");
        enToTrDict.put("To learn", "Öğrenmek");
        enToTrDict.put("To love", "Sevmek");
        enToTrDict.put("To swim", "Yüzmek");
        enToTrDict.put("To fly", "Uçmak");
        enToTrDict.put("To cook", "Yemek pişirmek");
        enToTrDict.put("To buy", "Satın almak");
        enToTrDict.put("To sell", "Satmak");

       
        enToTrDict.put("Big", "Büyük");
        enToTrDict.put("Small", "Küçük");
        enToTrDict.put("Hot", "Sıcak");
        enToTrDict.put("Cold", "Soğuk");
        enToTrDict.put("New", "Yeni");
        enToTrDict.put("Old", "Eski");
        enToTrDict.put("Good", "İyi");
        enToTrDict.put("Bad", "Kötü");
        enToTrDict.put("Beautiful", "Güzel");
        enToTrDict.put("Fast", "Hızlı");
        enToTrDict.put("Slow", "Yavaş");
        enToTrDict.put("Happy", "Mutlu");
        enToTrDict.put("Sad", "Üzgün");
        enToTrDict.put("Easy", "Kolay");
        enToTrDict.put("Difficult", "Zor");
        enToTrDict.put("Expensive", "Pahalı");
        enToTrDict.put("Cheap", "Ucuz");

       
        enToTrDict.put("Monday", "Pazartesi");
        enToTrDict.put("Tuesday", "Salı");
        enToTrDict.put("Wednesday", "Çarşamba");
        enToTrDict.put("Thursday", "Perşembe");
        enToTrDict.put("Friday", "Cuma");
        enToTrDict.put("Saturday", "Cumartesi");
        enToTrDict.put("Sunday", "Pazar");

        
        enToTrDict.put("Today", "Bugün");
        enToTrDict.put("Tomorrow", "Yarın");
        enToTrDict.put("Yesterday", "Dün");
        enToTrDict.put("Morning", "Sabah");
        enToTrDict.put("Evening", "Akşam");
        enToTrDict.put("Night", "Gece");
        enToTrDict.put("Now", "Şimdi");

        
        enToTrDict.put("Book", "Kitap");
        enToTrDict.put("Pen", "Kalem");
        enToTrDict.put("Table", "Masa");
        enToTrDict.put("Chair", "Sandalye");
        enToTrDict.put("Door", "Kapı");
        enToTrDict.put("Window", "Pencere");
        enToTrDict.put("Phone", "Telefon");
        enToTrDict.put("Computer", "Bilgisayar");
        enToTrDict.put("Car", "Araba");
        enToTrDict.put("Bus", "Otobüs");
        enToTrDict.put("Key", "Anahtar");
        enToTrDict.put("Bag", "Çanta");
        enToTrDict.put("Money", "Para");
        enToTrDict.put("Clock", "Saat");

       
        enToTrDict.put("Sun", "Güneş");
        enToTrDict.put("Moon", "Ay");
        enToTrDict.put("Star", "Yıldız");
        enToTrDict.put("Rain", "Yağmur");
        enToTrDict.put("Snow", "Kar");
        enToTrDict.put("Wind", "Rüzgar");
        enToTrDict.put("Tree", "Ağaç");
        enToTrDict.put("Flower", "Çiçek");
        enToTrDict.put("Sky", "Gökyüzü");
        enToTrDict.put("Fire", "Ateş");

      
        enToTrDict.put("I love you", "Seni seviyorum");
        enToTrDict.put("I don't understand", "Anlamıyorum");
        enToTrDict.put("I am hungry", "Açım");
        enToTrDict.put("I am tired", "Yorgunum");
        enToTrDict.put("Where is", "Nerede");
        enToTrDict.put("How much", "Ne kadar");
        enToTrDict.put("Happy birthday", "Doğum günün kutlu olsun");
        enToTrDict.put("Good luck", "İyi şanslar");
        enToTrDict.put("Congratulations", "Tebrikler");
        enToTrDict.put("No problem", "Sorun değil");

        
        for (Map.Entry<String, String> entry : enToTrDict.entrySet()) {
            trToEnDict.put(entry.getValue(), entry.getKey());
        }
    }
}