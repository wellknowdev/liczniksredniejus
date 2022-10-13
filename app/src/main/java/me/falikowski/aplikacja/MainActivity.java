package me.falikowski.aplikacja;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

import org.jsoup.nodes.Element;


public class MainActivity extends AppCompatActivity {

    public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    private EditText identifer, password;
    private View progresBtn;
    private ProgressBar progressBar;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identifer = findViewById(R.id.identifer_tfield);
        password = findViewById(R.id.password_tfield);
        progresBtn = findViewById(R.id.progress_login_btn);
        progressBar = findViewById(R.id.progressbar_progress);
        textView = findViewById(R.id.testview_progress);

        progresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = identifer.getText().toString();
                String password_ = password.getText().toString();
                if(progressBar.getVisibility() == View.GONE) {
                if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(password_)) {
                    Toast.makeText(MainActivity.this, "Wprowadź hasło oraz identyfikator!", Toast.LENGTH_SHORT).show();
                } else {
                    buttonActivated();
                    login(userName, password_);
                }
                }
            }
        });
    }


    public void login(String login, String pass) {
        new loginHandler().execute(login, pass);
    }

    public class loginHandler extends AsyncTask<String, Void, Void> {

        private Semester se = new Semester();

        @Override
        protected Void doInBackground(String... credentials) {
            String userName = credentials[0];
            String password = credentials[1];
            Response response2 = null;

            try {
                Response response = Jsoup.connect("https://usosweb.us.edu.pl/kontroler.php?_action=logowaniecas/index")
                        .method(Connection.Method.POST)
                        .timeout(10 * 1000)
                        .execute();

                Document responseDocument = response.parse();

                Element _execution = responseDocument.select("input[name=execution]").first();
                Element _eventId = responseDocument.select("input[name=_eventId]").first();
                Element _submit = responseDocument.select("input[name=submit]").first();

                response2 = Jsoup.connect("https://logowanie.us.edu.pl/cas/login?service=https%3A%2F%2Fusosweb.us.edu.pl%2Fkontroler.php%3F_action%3Dlogowaniecas%2Findex&locale=pl")
                        .data("username", userName)
                        .data("password", password)
                        .data("execution", _execution.attr("value"))
                        .data("_eventId", _eventId.attr("value"))
                        .data("submit", _submit.attr("value"))
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                        .header("Host", "logowanie.us.edu.pl")
                        .header("Origin", "https://logowanie.us.edu.pl")
                        .timeout(30000)
                        .method(Connection.Method.POST)
                        .ignoreHttpErrors(true)
                        .userAgent(userAgent)
                        .execute();

                Document home = Jsoup.connect("https://usosweb.us.edu.pl/kontroler.php?_action=home/index")
                        .cookies(response2.cookies())
                        .ignoreHttpErrors(true)
                        .userAgent(userAgent)
                        .get();

                String _username = home.select("cas-bar[id=layout-cas-bar]").first().attr("logged-user");

                if (response2.statusCode() == 500) {
                    Map<String, Float> semestersandmeans = se.loadSemesters(response2.cookies());
                    String[] semesters = new String[semestersandmeans.size()];
                    for(int i=0;i<semesters.length;i++)
                    {
                        semesters[i] = String.valueOf(semestersandmeans.keySet().toArray()[i]);
                    }
                    startMenu(_username, semestersandmeans);
                    runToastInAsync("Zalogowano pomyślnie");
                } else {
                    runToastInAsync("Błąd logowania!");
                    buttonFinished();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void startMenu(String _username, Map<String, Float> semesters_items){
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        String[] semesters = new String[semesters_items.size()];
        String[] means = new String[semesters_items.size()];
        for(int j=0;j<semesters.length;j++)
        {
            semesters[j] = String.valueOf(semesters_items.keySet().toArray()[j]);
            means[j] = String.valueOf(semesters_items.values().toArray()[j]);
        }
        i.putExtra("username", _username);
        i.putExtra("semesters", semesters);
        i.putExtra("means", means);
        startActivity(i);
    }


    private void runToastInAsync(String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void buttonActivated() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Logowanie ...");
    }
    private void buttonFinished(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                textView.setText(R.string.zaloguj);
            }
        });
    }

}