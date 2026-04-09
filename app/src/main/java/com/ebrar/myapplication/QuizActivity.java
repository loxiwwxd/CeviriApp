package com.ebrar.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final int QUESTIONS_PER_QUIZ = 10;

    private TextView tvQuestion, tvTurkishMeaning, tvScore, tvProgress;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private ProgressBar progressBarQuiz;

    private List<Question> allQuestions = new ArrayList<>();
    private List<Question> currentQuiz = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();
        loadAllQuestions();
        startNewQuiz();
    }

    private void startNewQuiz() {
        currentIndex = 0;
        score = 0;
        answered = false;

        
        Collections.shuffle(allQuestions);
        currentQuiz.clear();
        for (int i = 0; i < Math.min(QUESTIONS_PER_QUIZ, allQuestions.size()); i++) {
            currentQuiz.add(allQuestions.get(i));
        }

        loadQuestion();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTurkishMeaning = findViewById(R.id.tvTurkishMeaning);
        tvScore = findViewById(R.id.tvScore);
        tvProgress = findViewById(R.id.tvProgress);
        progressBarQuiz = findViewById(R.id.progressBarQuiz);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnNext = findViewById(R.id.btnNext);

        View.OnClickListener optionClick = v -> {
            if (!answered) {
                checkAnswer((Button) v);
            }
        };
        btnOption1.setOnClickListener(optionClick);
        btnOption2.setOnClickListener(optionClick);
        btnOption3.setOnClickListener(optionClick);
        btnOption4.setOnClickListener(optionClick);

        btnNext.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < currentQuiz.size()) {
                loadQuestion();
            } else {
                showFinalScore();
            }
        });
    }

    private void loadQuestion() {
        answered = false;
        btnNext.setVisibility(View.GONE);

        Question q = currentQuiz.get(currentIndex);

        tvQuestion.setText(q.sentence);
        tvTurkishMeaning.setText("Türkçesi: " + q.turkishMeaning);

        tvProgress.setText("Soru: " + (currentIndex + 1) + "/" + QUESTIONS_PER_QUIZ);
        tvScore.setText("Skor: " + score);
        int progressPercent = (int) ((currentIndex * 100.0) / QUESTIONS_PER_QUIZ);
        progressBarQuiz.setProgress(progressPercent);

        
        List<String> options = new ArrayList<>(q.wrongAnswers);
        options.add(q.correctAnswer);
        Collections.shuffle(options);

        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));
        btnOption4.setText(options.get(3));

        resetButtonColors();
    }

    private void checkAnswer(Button selected) {
        answered = true;
        Question q = currentQuiz.get(currentIndex);
        String selectedText = selected.getText().toString();

        if (selectedText.equals(q.correctAnswer)) {
            score++;
            selected.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        } else {
            selected.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
            highlightCorrect(q.correctAnswer);
        }

        tvScore.setText("Skor: " + score);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void highlightCorrect(String correct) {
        if (btnOption1.getText().toString().equals(correct))
            btnOption1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        if (btnOption2.getText().toString().equals(correct))
            btnOption2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        if (btnOption3.getText().toString().equals(correct))
            btnOption3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        if (btnOption4.getText().toString().equals(correct))
            btnOption4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
    }

    private void resetButtonColors() {
        int color = 0xFF1565C0;
        btnOption1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        btnOption2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        btnOption3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        btnOption4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
    }

    private void showFinalScore() {
        progressBarQuiz.setProgress(100);

        String message;
        if (score == QUESTIONS_PER_QUIZ) {
            message = "Mükemmel! Tüm soruları doğru bildiniz! 🎉";
        } else if (score >= QUESTIONS_PER_QUIZ * 0.8) {
            message = "Harika! Çoğunu doğru bildiniz! 👏";
        } else if (score >= QUESTIONS_PER_QUIZ * 0.5) {
            message = "Fena değil, ama daha çok çalışmalısın! 💪";
        } else {
            message = "Daha çok pratik yapmalısın! 📚";
        }

        new AlertDialog.Builder(this)
                .setTitle("Quiz Bitti!")
                .setMessage("Skorunuz: " + score + "/" + QUESTIONS_PER_QUIZ + "\n\n" + message)
                .setPositiveButton("Tekrar Oyna", (d, w) -> startNewQuiz())
                .setNegativeButton("Çıkış", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    // ============================================================
    // 200 SORU HAVUZU
    // ============================================================
    private void loadAllQuestions() {

        // ==========================================
        // 1. BE FİİLİ (am/is/are)
        // ==========================================
        allQuestions.add(new Question("She ___ a teacher.", "O bir öğretmendir.", "is", new String[]{"am", "are", "be"}));
        allQuestions.add(new Question("They ___ happy today.", "Onlar bugün mutlu.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("I ___ a student.", "Ben bir öğrenciyim.", "am", new String[]{"is", "are", "be"}));
        allQuestions.add(new Question("We ___ at school.", "Biz okuldayız.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("He ___ my brother.", "O benim kardeşim.", "is", new String[]{"am", "are", "were"}));
        allQuestions.add(new Question("It ___ a beautiful day.", "Güzel bir gün.", "is", new String[]{"am", "are", "were"}));
        allQuestions.add(new Question("You ___ very tall.", "Sen çok uzunsun.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("The children ___ in the park.", "Çocuklar parkta.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("My name ___ Ali.", "Benim adım Ali.", "is", new String[]{"am", "are", "be"}));
        allQuestions.add(new Question("I ___ from Turkey.", "Ben Türkiye'denim.", "am", new String[]{"is", "are", "was"}));

        // ==========================================
        // 2. HAVE / HAS
        // ==========================================
        allQuestions.add(new Question("I ___ a cat.", "Benim bir kedim var.", "have", new String[]{"has", "am", "is"}));
        allQuestions.add(new Question("She ___ two sisters.", "Onun iki kız kardeşi var.", "has", new String[]{"have", "had", "is"}));
        allQuestions.add(new Question("They ___ a big house.", "Onların büyük bir evi var.", "have", new String[]{"has", "is", "are"}));
        allQuestions.add(new Question("He ___ a red car.", "Onun kırmızı bir arabası var.", "has", new String[]{"have", "had", "is"}));
        allQuestions.add(new Question("We ___ three dogs.", "Bizim üç köpeğimiz var.", "have", new String[]{"has", "is", "are"}));
        allQuestions.add(new Question("The cat ___ green eyes.", "Kedinin yeşil gözleri var.", "has", new String[]{"have", "had", "is"}));
        allQuestions.add(new Question("I ___ a new phone.", "Benim yeni bir telefonum var.", "have", new String[]{"has", "am", "is"}));
        allQuestions.add(new Question("My father ___ a bicycle.", "Babamın bir bisikleti var.", "has", new String[]{"have", "had", "is"}));

        // ==========================================
        // 3. SIMPLE PRESENT
        // ==========================================
        allQuestions.add(new Question("I ___ coffee every morning.", "Her sabah kahve içerim.", "drink", new String[]{"drinks", "drank", "drinking"}));
        allQuestions.add(new Question("She ___ to school by bus.", "O okula otobüsle gider.", "goes", new String[]{"go", "going", "went"}));
        allQuestions.add(new Question("We ___ English at school.", "Okulda İngilizce öğreniyoruz.", "learn", new String[]{"learns", "learned", "learning"}));
        allQuestions.add(new Question("He ___ football on Sundays.", "O Pazar günleri futbol oynar.", "plays", new String[]{"play", "played", "playing"}));
        allQuestions.add(new Question("The cat ___ on the sofa.", "Kedi koltukta uyur.", "sleeps", new String[]{"sleep", "slept", "sleeping"}));
        allQuestions.add(new Question("My mother ___ delicious food.", "Annem lezzetli yemek pişirir.", "cooks", new String[]{"cook", "cooked", "cooking"}));
        allQuestions.add(new Question("He ___ his homework after school.", "O okuldan sonra ödevini yapar.", "does", new String[]{"do", "did", "doing"}));
        allQuestions.add(new Question("She ___ books every day.", "O her gün kitap okur.", "reads", new String[]{"read", "readed", "reading"}));
        allQuestions.add(new Question("The dog ___ very fast.", "Köpek çok hızlı koşar.", "runs", new String[]{"run", "ran", "running"}));
        allQuestions.add(new Question("My sister ___ beautiful songs.", "Kız kardeşim güzel şarkılar söyler.", "sings", new String[]{"sing", "sang", "singing"}));
        allQuestions.add(new Question("He ___ to work at 8 am.", "O sabah 8'de işe gider.", "goes", new String[]{"go", "went", "going"}));
        allQuestions.add(new Question("I ___ my teeth twice a day.", "Günde iki kez dişlerimi fırçalarım.", "brush", new String[]{"brushes", "brushed", "brushing"}));
        allQuestions.add(new Question("She ___ TV in the evening.", "O akşamları TV izler.", "watches", new String[]{"watch", "watched", "watching"}));
        allQuestions.add(new Question("They ___ in a restaurant.", "Onlar bir restoranda yemek yer.", "eat", new String[]{"eats", "ate", "eating"}));
        allQuestions.add(new Question("He ___ the piano very well.", "O pianoyu çok iyi çalar.", "plays", new String[]{"play", "played", "playing"}));

        // ==========================================
        // 4. ARTICLES (a/an/the)
        // ==========================================
        allQuestions.add(new Question("I have ___ apple.", "Benim bir elmam var.", "an", new String[]{"a", "the", "some"}));
        allQuestions.add(new Question("There is ___ book on the table.", "Masanın üstünde bir kitap var.", "a", new String[]{"an", "the", "some"}));
        allQuestions.add(new Question("___ sun is shining.", "Güneş parlıyor.", "The", new String[]{"A", "An", "Some"}));
        allQuestions.add(new Question("She is ___ engineer.", "O bir mühendistir.", "an", new String[]{"a", "the", "some"}));
        allQuestions.add(new Question("I need ___ umbrella.", "Bir şemsiyeye ihtiyacım var.", "an", new String[]{"a", "the", "some"}));
        allQuestions.add(new Question("He bought ___ new car.", "O yeni bir araba aldı.", "a", new String[]{"an", "the", "some"}));
        allQuestions.add(new Question("___ moon is bright tonight.", "Ay bu gece parlak.", "The", new String[]{"A", "An", "Some"}));
        allQuestions.add(new Question("She ate ___ orange.", "O bir portakal yedi.", "an", new String[]{"a", "the", "some"}));

        // ==========================================
        // 5. PREPOSITIONS (in/on/at/under/between)
        // ==========================================
        allQuestions.add(new Question("The cat is ___ the table.", "Kedi masanın altında.", "under", new String[]{"on", "in", "at"}));
        allQuestions.add(new Question("I live ___ Istanbul.", "İstanbul'da yaşıyorum.", "in", new String[]{"on", "at", "to"}));
        allQuestions.add(new Question("The picture is ___ the wall.", "Resim duvarda.", "on", new String[]{"in", "at", "under"}));
        allQuestions.add(new Question("She is waiting ___ the bus stop.", "O otobüs durağında bekliyor.", "at", new String[]{"in", "on", "to"}));
        allQuestions.add(new Question("The ball is ___ the box.", "Top kutunun içinde.", "in", new String[]{"on", "at", "under"}));
        allQuestions.add(new Question("He sits ___ me in class.", "Sınıfta yanımda oturur.", "next to", new String[]{"under", "on", "in"}));
        allQuestions.add(new Question("We arrived ___ 3 o'clock.", "Saat 3'te vardık.", "at", new String[]{"in", "on", "to"}));
        allQuestions.add(new Question("My birthday is ___ July.", "Doğum günüm Temmuz'da.", "in", new String[]{"on", "at", "to"}));
        allQuestions.add(new Question("The keys are ___ the table.", "Anahtarlar masanın üstünde.", "on", new String[]{"in", "at", "under"}));
        allQuestions.add(new Question("She was born ___ 1990.", "1990'da doğdu.", "in", new String[]{"on", "at", "to"}));

        // ==========================================
        // 6. PRONOUNS
        // ==========================================
        allQuestions.add(new Question("___ is my best friend.", "O benim en iyi arkadaşım.", "He", new String[]{"Him", "His", "Her"}));
        allQuestions.add(new Question("I gave ___ a present.", "Ona bir hediye verdim.", "her", new String[]{"she", "hers", "his"}));
        allQuestions.add(new Question("This book is ___.", "Bu kitap benimdir.", "mine", new String[]{"my", "me", "I"}));
        allQuestions.add(new Question("___ are going to the park.", "Biz parka gidiyoruz.", "We", new String[]{"Us", "Our", "Ours"}));
        allQuestions.add(new Question("She washed ___ hands.", "O ellerini yıkadı.", "her", new String[]{"she", "hers", "him"}));
        allQuestions.add(new Question("The cat licked ___ paw.", "Kedi patisini yaladı.", "its", new String[]{"it", "his", "her"}));
        allQuestions.add(new Question("Give ___ the ball.", "Bana topu ver.", "me", new String[]{"I", "my", "mine"}));
        allQuestions.add(new Question("___ lives in Ankara.", "O Ankara'da yaşıyor.", "She", new String[]{"Her", "Hers", "Him"}));

        // ==========================================
        // 7. CAN / CAN'T
        // ==========================================
        allQuestions.add(new Question("Birds ___ fly.", "Kuşlar uçabilir.", "can", new String[]{"can't", "is", "do"}));
        allQuestions.add(new Question("Fish ___ walk.", "Balıklar yürüyemez.", "can't", new String[]{"can", "don't", "isn't"}));
        allQuestions.add(new Question("I ___ speak English.", "İngilizce konuşabilirim.", "can", new String[]{"can't", "am", "do"}));
        allQuestions.add(new Question("Babies ___ drive a car.", "Bebekler araba süremez.", "can't", new String[]{"can", "don't", "aren't"}));
        allQuestions.add(new Question("She ___ play the guitar.", "O gitar çalabilir.", "can", new String[]{"can't", "is", "does"}));
        allQuestions.add(new Question("We ___ see in the dark.", "Karanlıkta göremeyiz.", "can't", new String[]{"can", "don't", "aren't"}));
        allQuestions.add(new Question("He ___ swim very well.", "O çok iyi yüzebilir.", "can", new String[]{"can't", "is", "does"}));
        allQuestions.add(new Question("A cat ___ bark like a dog.", "Bir kedi köpek gibi havlayamaz.", "can't", new String[]{"can", "don't", "isn't"}));

        // ==========================================
        // 8. PAST SIMPLE
        // ==========================================
        allQuestions.add(new Question("I ___ to the park yesterday.", "Dün parka gittim.", "went", new String[]{"go", "goes", "going"}));
        allQuestions.add(new Question("She ___ a book last night.", "Dün gece bir kitap okudu.", "read", new String[]{"reads", "readed", "reading"}));
        allQuestions.add(new Question("We ___ a great movie last week.", "Geçen hafta harika bir film izledik.", "watched", new String[]{"watch", "watches", "watching"}));
        allQuestions.add(new Question("They ___ football yesterday.", "Dün futbol oynadılar.", "played", new String[]{"play", "plays", "playing"}));
        allQuestions.add(new Question("He ___ his lunch at 12.", "Öğle yemeğini 12'de yedi.", "ate", new String[]{"eat", "eats", "eating"}));
        allQuestions.add(new Question("I ___ to music last night.", "Dün gece müzik dinledim.", "listened", new String[]{"listen", "listens", "listening"}));
        allQuestions.add(new Question("She ___ a letter to her friend.", "Arkadaşına bir mektup yazdı.", "wrote", new String[]{"write", "writes", "writing"}));
        allQuestions.add(new Question("We ___ to the beach last summer.", "Geçen yaz sahile gittik.", "went", new String[]{"go", "goes", "going"}));
        allQuestions.add(new Question("He ___ a new bicycle.", "O yeni bir bisiklet aldı.", "bought", new String[]{"buy", "buys", "buying"}));
        allQuestions.add(new Question("They ___ home at 6 pm.", "Akşam 6'da eve geldiler.", "came", new String[]{"come", "comes", "coming"}));
        allQuestions.add(new Question("I ___ my room yesterday.", "Dün odamı temizledim.", "cleaned", new String[]{"clean", "cleans", "cleaning"}));
        allQuestions.add(new Question("She ___ in the sea last summer.", "Geçen yaz denizde yüzdü.", "swam", new String[]{"swim", "swims", "swimming"}));

        // ==========================================
        // 9. THERE IS / THERE ARE
        // ==========================================
        allQuestions.add(new Question("There ___ a cat in the garden.", "Bahçede bir kedi var.", "is", new String[]{"are", "am", "be"}));
        allQuestions.add(new Question("There ___ many students in the class.", "Sınıfta çok öğrenci var.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("There ___ some milk in the fridge.", "Buzdolabında biraz süt var.", "is", new String[]{"are", "am", "be"}));
        allQuestions.add(new Question("There ___ five books on the shelf.", "Rafta beş kitap var.", "are", new String[]{"is", "am", "was"}));
        allQuestions.add(new Question("There ___ a hospital near here.", "Yakınlarda bir hastane var.", "is", new String[]{"are", "am", "be"}));
        allQuestions.add(new Question("There ___ two parks in my city.", "Şehrimde iki park var.", "are", new String[]{"is", "am", "was"}));

        // ==========================================
        // 10. COMPARATIVES / SUPERLATIVES
        // ==========================================
        allQuestions.add(new Question("An elephant is ___ than a cat.", "Bir fil kediden büyüktür.", "bigger", new String[]{"big", "biggest", "more big"}));
        allQuestions.add(new Question("Summer is ___ than winter.", "Yaz kıştan daha sıcaktır.", "hotter", new String[]{"hot", "hottest", "more hot"}));
        allQuestions.add(new Question("A car is ___ than a bicycle.", "Araba bisikletten daha hızlıdır.", "faster", new String[]{"fast", "fastest", "more fast"}));
        allQuestions.add(new Question("This book is ___ than that one.", "Bu kitap ondan daha ilginçtir.", "more interesting", new String[]{"interesting", "interestinger", "most interesting"}));
        allQuestions.add(new Question("Mount Everest is the ___ mountain.", "Everest dünyanın en yüksek dağıdır.", "highest", new String[]{"higher", "high", "more high"}));
        allQuestions.add(new Question("A turtle is ___ than a rabbit.", "Kaplumbağa tavşandan yavaştır.", "slower", new String[]{"slow", "slowest", "more slow"}));
        allQuestions.add(new Question("Gold is ___ than silver.", "Altın gümüşten daha pahalıdır.", "more expensive", new String[]{"expensive", "expensiver", "most expensive"}));
        allQuestions.add(new Question("She is the ___ girl in the class.", "O sınıftaki en uzun kız.", "tallest", new String[]{"taller", "tall", "more tall"}));

        // ==========================================
        // 11. QUESTION WORDS
        // ==========================================
        allQuestions.add(new Question("___ is your name?", "Senin adın ne?", "What", new String[]{"Who", "Where", "When"}));
        allQuestions.add(new Question("___ do you live?", "Nerede yaşıyorsun?", "Where", new String[]{"What", "Who", "When"}));
        allQuestions.add(new Question("___ old are you?", "Kaç yaşındasın?", "How", new String[]{"What", "Who", "Where"}));
        allQuestions.add(new Question("___ is your birthday?", "Doğum günün ne zaman?", "When", new String[]{"What", "Who", "Where"}));
        allQuestions.add(new Question("___ is calling?", "Kim arıyor?", "Who", new String[]{"What", "Where", "When"}));
        allQuestions.add(new Question("___ did you choose that?", "Neden onu seçtin?", "Why", new String[]{"What", "Where", "When"}));
        allQuestions.add(new Question("___ bag is this?", "Bu kimin çantası?", "Whose", new String[]{"Who", "What", "Which"}));
        allQuestions.add(new Question("___ color do you prefer?", "Hangi rengi tercih edersin?", "Which", new String[]{"What", "Who", "Where"}));

        // ==========================================
        // 12. DO / DOES
        // ==========================================
        allQuestions.add(new Question("___ you like pizza?", "Pizza sever misin?", "Do", new String[]{"Does", "Did", "Are"}));
        allQuestions.add(new Question("___ she speak English?", "O İngilizce konuşur mu?", "Does", new String[]{"Do", "Did", "Is"}));
        allQuestions.add(new Question("___ they live in Ankara?", "Onlar Ankara'da mı yaşıyor?", "Do", new String[]{"Does", "Did", "Are"}));
        allQuestions.add(new Question("___ he play basketball?", "O basketbol oynar mı?", "Does", new String[]{"Do", "Did", "Is"}));
        allQuestions.add(new Question("___ you have a pet?", "Evcil hayvanın var mı?", "Do", new String[]{"Does", "Did", "Are"}));
        allQuestions.add(new Question("___ she like chocolate?", "O çikolata sever mi?", "Does", new String[]{"Do", "Did", "Is"}));
        allQuestions.add(new Question("___ they go to school?", "Onlar okula gider mi?", "Do", new String[]{"Does", "Did", "Are"}));
        allQuestions.add(new Question("___ it rain a lot here?", "Burada çok yağmur yağar mı?", "Does", new String[]{"Do", "Did", "Is"}));

        // ==========================================
        // 13. PRESENT CONTINUOUS
        // ==========================================
        allQuestions.add(new Question("I am ___ a book right now.", "Şu anda bir kitap okuyorum.", "reading", new String[]{"read", "reads", "readed"}));
        allQuestions.add(new Question("She is ___ to music.", "O müzik dinliyor.", "listening", new String[]{"listen", "listens", "listened"}));
        allQuestions.add(new Question("They are ___ football.", "Onlar futbol oynuyor.", "playing", new String[]{"play", "plays", "played"}));
        allQuestions.add(new Question("He is ___ his homework.", "O ödevini yapıyor.", "doing", new String[]{"do", "does", "did"}));
        allQuestions.add(new Question("We are ___ dinner now.", "Şimdi akşam yemeği yiyoruz.", "eating", new String[]{"eat", "eats", "ate"}));
        allQuestions.add(new Question("She is ___ a picture.", "O bir resim çiziyor.", "drawing", new String[]{"draw", "draws", "drew"}));
        allQuestions.add(new Question("I am ___ for the bus.", "Otobüsü bekliyorum.", "waiting", new String[]{"wait", "waits", "waited"}));
        allQuestions.add(new Question("He is ___ to his friend.", "O arkadaşıyla konuşuyor.", "talking", new String[]{"talk", "talks", "talked"}));
        allQuestions.add(new Question("They are ___ in the pool.", "Onlar havuzda yüzüyor.", "swimming", new String[]{"swim", "swims", "swam"}));
        allQuestions.add(new Question("She is ___ a cake.", "O bir pasta pişiriyor.", "baking", new String[]{"bake", "bakes", "baked"}));

        // ==========================================
        // 14. KELIME BİLGİSİ - ZITLIKLAR
        // ==========================================
        allQuestions.add(new Question("The opposite of 'hot' is ___.", "'Sıcak'ın zıttı nedir?", "cold", new String[]{"warm", "cool", "big"}));
        allQuestions.add(new Question("The opposite of 'big' is ___.", "'Büyük'ün zıttı nedir?", "small", new String[]{"tall", "long", "fast"}));
        allQuestions.add(new Question("The opposite of 'happy' is ___.", "'Mutlu'nun zıttı nedir?", "sad", new String[]{"angry", "tired", "hungry"}));
        allQuestions.add(new Question("The opposite of 'fast' is ___.", "'Hızlı'nın zıttı nedir?", "slow", new String[]{"quick", "big", "long"}));
        allQuestions.add(new Question("The opposite of 'old' is ___.", "'Eski/Yaşlı'nın zıttı nedir?", "new", new String[]{"young", "big", "tall"}));
        allQuestions.add(new Question("The opposite of 'long' is ___.", "'Uzun'un zıttı nedir?", "short", new String[]{"tall", "big", "small"}));
        allQuestions.add(new Question("The opposite of 'easy' is ___.", "'Kolay'ın zıttı nedir?", "difficult", new String[]{"simple", "fast", "short"}));
        allQuestions.add(new Question("The opposite of 'clean' is ___.", "'Temiz'in zıttı nedir?", "dirty", new String[]{"tidy", "neat", "clear"}));
        allQuestions.add(new Question("The opposite of 'rich' is ___.", "'Zengin'in zıttı nedir?", "poor", new String[]{"weak", "thin", "low"}));
        allQuestions.add(new Question("The opposite of 'light' is ___.", "'Hafif/Açık'ın zıttı nedir?", "dark", new String[]{"bright", "heavy", "thin"}));

        // ==========================================
        // 15. KELIME BİLGİSİ - GENEL
        // ==========================================
        allQuestions.add(new Question("The color of snow is ___.", "Karın rengi nedir?", "white", new String[]{"blue", "red", "black"}));
        allQuestions.add(new Question("A ___ gives us milk.", "Bize süt veren hayvan nedir?", "cow", new String[]{"cat", "bird", "fish"}));
        allQuestions.add(new Question("We use ___ to write.", "Yazmak için ne kullanırız?", "pen", new String[]{"cup", "key", "hat"}));
        allQuestions.add(new Question("There are seven days in a ___.", "Bir haftada yedi gün var.", "week", new String[]{"month", "year", "day"}));
        allQuestions.add(new Question("We see with our ___.", "Gözlerimizle görürüz.", "eyes", new String[]{"ears", "hands", "feet"}));
        allQuestions.add(new Question("Water freezes at ___ degrees.", "Su sıfır derecede donar.", "zero", new String[]{"ten", "fifty", "hundred"}));
        allQuestions.add(new Question("The capital of Turkey is ___.", "Türkiye'nin başkenti Ankara'dır.", "Ankara", new String[]{"Istanbul", "Izmir", "Antalya"}));
        allQuestions.add(new Question("A doctor works in a ___.", "Doktor hastanede çalışır.", "hospital", new String[]{"school", "market", "park"}));
        allQuestions.add(new Question("We hear with our ___.", "Kulaklarımızla duyarız.", "ears", new String[]{"eyes", "mouth", "nose"}));
        allQuestions.add(new Question("A ___ lives in the water.", "Su içinde yaşayan hayvan nedir?", "fish", new String[]{"bird", "cat", "dog"}));
        allQuestions.add(new Question("There are ___ months in a year.", "Bir yılda 12 ay var.", "twelve", new String[]{"ten", "eleven", "thirteen"}));
        allQuestions.add(new Question("The opposite color of white is ___.", "Beyazın zıt rengi nedir?", "black", new String[]{"red", "blue", "green"}));
        allQuestions.add(new Question("Bread is made from ___.", "Ekmek undan yapılır.", "flour", new String[]{"sugar", "salt", "butter"}));
        allQuestions.add(new Question("We breathe with our ___.", "Ciğerlerimizle nefes alırız.", "lungs", new String[]{"heart", "brain", "stomach"}));
        allQuestions.add(new Question("The ___ is the king of the jungle.", "Ormanın kralı nedir?", "lion", new String[]{"tiger", "bear", "wolf"}));

        // ==========================================
        // 16. SOME / ANY
        // ==========================================
        allQuestions.add(new Question("Is there ___ milk?", "Hiç süt var mı?", "any", new String[]{"some", "a", "the"}));
        allQuestions.add(new Question("I have ___ friends.", "Bazı arkadaşlarım var.", "some", new String[]{"any", "no", "the"}));
        allQuestions.add(new Question("She doesn't have ___ money.", "Onun hiç parası yok.", "any", new String[]{"some", "a", "the"}));
        allQuestions.add(new Question("There are ___ apples on the table.", "Masada birkaç elma var.", "some", new String[]{"any", "no", "the"}));
        allQuestions.add(new Question("Do you have ___ questions?", "Hiç sorunuz var mı?", "any", new String[]{"some", "a", "the"}));
        allQuestions.add(new Question("I bought ___ bread.", "Biraz ekmek aldım.", "some", new String[]{"any", "no", "a"}));

        // ==========================================
        // 17. MUST / MUSTN'T
        // ==========================================
        allQuestions.add(new Question("You ___ wear a seatbelt.", "Emniyet kemeri takmalısın.", "must", new String[]{"mustn't", "can", "can't"}));
        allQuestions.add(new Question("You ___ use your phone in class.", "Sınıfta telefonunu kullanmamalısın.", "mustn't", new String[]{"must", "can", "should"}));
        allQuestions.add(new Question("Students ___ be on time.", "Öğrenciler zamanında olmalıdır.", "must", new String[]{"mustn't", "can't", "shouldn't"}));
        allQuestions.add(new Question("You ___ run in the hospital.", "Hastanede koşmamalısın.", "mustn't", new String[]{"must", "can", "should"}));

        // ==========================================
        // 18. FUTURE (will/going to)
        // ==========================================
        allQuestions.add(new Question("I ___ help you tomorrow.", "Yarın sana yardım edeceğim.", "will", new String[]{"am", "do", "did"}));
        allQuestions.add(new Question("She is ___ to visit her grandma.", "Büyükannesini ziyaret edecek.", "going", new String[]{"will", "go", "went"}));
        allQuestions.add(new Question("It ___ rain tomorrow.", "Yarın yağmur yağacak.", "will", new String[]{"is", "does", "did"}));
        allQuestions.add(new Question("We are ___ to have a party.", "Bir parti vereceğiz.", "going", new String[]{"will", "go", "went"}));
        allQuestions.add(new Question("He ___ be a doctor in the future.", "O gelecekte doktor olacak.", "will", new String[]{"is", "does", "was"}));
        allQuestions.add(new Question("They are ___ to move next month.", "Gelecek ay taşınacaklar.", "going", new String[]{"will", "go", "went"}));

        // ==========================================
        // 19. COUNTABLE / UNCOUNTABLE
        // ==========================================
        allQuestions.add(new Question("How ___ water do you drink?", "Ne kadar su içersin?", "much", new String[]{"many", "some", "few"}));
        allQuestions.add(new Question("How ___ books do you have?", "Kaç kitabın var?", "many", new String[]{"much", "some", "little"}));
        allQuestions.add(new Question("I don't have ___ money.", "Çok param yok.", "much", new String[]{"many", "few", "several"}));
        allQuestions.add(new Question("There are ___ students in the school.", "Okulda çok öğrenci var.", "many", new String[]{"much", "little", "any"}));
        allQuestions.add(new Question("How ___ sugar do you want?", "Ne kadar şeker istiyorsun?", "much", new String[]{"many", "few", "several"}));
        allQuestions.add(new Question("How ___ brothers do you have?", "Kaç erkek kardeşin var?", "many", new String[]{"much", "little", "any"}));

        // ==========================================
        // 20. KELIME - HAYVANLAR
        // ==========================================
        allQuestions.add(new Question("A ___ can fly.", "Uçabilen hayvan nedir?", "bird", new String[]{"fish", "dog", "cat"}));
        allQuestions.add(new Question("The ___ is the biggest animal.", "En büyük hayvan nedir?", "whale", new String[]{"elephant", "lion", "bear"}));
        allQuestions.add(new Question("A ___ has eight legs.", "Sekiz bacağı olan hayvan nedir?", "spider", new String[]{"ant", "bee", "fly"}));
        allQuestions.add(new Question("A ___ says 'meow'.", "'Miyav' diyen hayvan nedir?", "cat", new String[]{"dog", "bird", "cow"}));
        allQuestions.add(new Question("A ___ lives in the ocean.", "Okyanusta yaşayan hayvan nedir?", "dolphin", new String[]{"rabbit", "fox", "sheep"}));

        // ==========================================
        // 21. KELIME - MEKANLAR
        // ==========================================
        allQuestions.add(new Question("We buy food at the ___.", "Yiyecekleri nereden alırız?", "market", new String[]{"hospital", "school", "park"}));
        allQuestions.add(new Question("We learn at ___.", "Nerede öğreniriz?", "school", new String[]{"hospital", "market", "bank"}));
        allQuestions.add(new Question("We borrow books from the ___.", "Kitapları nereden ödünç alırız?", "library", new String[]{"market", "hospital", "bank"}));
        allQuestions.add(new Question("Planes take off at the ___.", "Uçaklar nereden kalkar?", "airport", new String[]{"station", "port", "park"}));
        allQuestions.add(new Question("We keep money in a ___.", "Parayı nerede saklarız?", "bank", new String[]{"school", "park", "market"}));

        // ==========================================
        // 22. KELIME - MESLEKLER
        // ==========================================
        allQuestions.add(new Question("A ___ teaches students.", "Öğrencilere öğreten kişi kimdir?", "teacher", new String[]{"doctor", "driver", "cook"}));
        allQuestions.add(new Question("A ___ flies airplanes.", "Uçak uçuran kişi kimdir?", "pilot", new String[]{"driver", "captain", "soldier"}));
        allQuestions.add(new Question("A ___ puts out fires.", "Yangın söndüren kişi kimdir?", "firefighter", new String[]{"police", "doctor", "soldier"}));
        allQuestions.add(new Question("A ___ cooks food in a restaurant.", "Restoranda yemek pişiren kişi kimdir?", "chef", new String[]{"waiter", "driver", "teacher"}));
        allQuestions.add(new Question("A ___ takes care of sick people.", "Hasta insanlara bakan kişi kimdir?", "nurse", new String[]{"teacher", "driver", "farmer"}));

        // ==========================================
        // 23. KELIME - VÜCUT
        // ==========================================
        allQuestions.add(new Question("We think with our ___.", "Beynimizle düşünürüz.", "brain", new String[]{"heart", "hand", "foot"}));
        allQuestions.add(new Question("We eat with our ___.", "Ağzımızla yeriz.", "mouth", new String[]{"nose", "ear", "eye"}));
        allQuestions.add(new Question("We walk with our ___.", "Ayaklarımızla yürürüz.", "feet", new String[]{"hands", "eyes", "ears"}));
        allQuestions.add(new Question("We smell with our ___.", "Burnumuzla koklarız.", "nose", new String[]{"mouth", "ear", "eye"}));

        // ==========================================
        // 24. KELIME - GÜNLER / ZAMAN
        // ==========================================
        allQuestions.add(new Question("The day after Monday is ___.", "Pazartesiden sonraki gün nedir?", "Tuesday", new String[]{"Wednesday", "Sunday", "Friday"}));
        allQuestions.add(new Question("The last day of the week is ___.", "Haftanın son günü nedir?", "Sunday", new String[]{"Saturday", "Friday", "Monday"}));
        allQuestions.add(new Question("The day before Friday is ___.", "Cumadan önceki gün nedir?", "Thursday", new String[]{"Wednesday", "Saturday", "Monday"}));
        allQuestions.add(new Question("The first month of the year is ___.", "Yılın ilk ayı nedir?", "January", new String[]{"February", "March", "December"}));
        allQuestions.add(new Question("The last month of the year is ___.", "Yılın son ayı nedir?", "December", new String[]{"November", "January", "October"}));
    }

    
    private static class Question {
        String sentence;
        String turkishMeaning;
        String correctAnswer;
        List<String> wrongAnswers;

        Question(String sentence, String turkishMeaning, String correctAnswer, String[] wrongAnswers) {
            this.sentence = sentence;
            this.turkishMeaning = turkishMeaning;
            this.correctAnswer = correctAnswer;
            this.wrongAnswers = new ArrayList<>();
            for (String w : wrongAnswers) {
                this.wrongAnswers.add(w);
            }
        }
    }
}