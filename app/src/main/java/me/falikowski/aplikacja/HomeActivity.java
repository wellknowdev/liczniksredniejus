package me.falikowski.aplikacja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;


public class HomeActivity extends AppCompatActivity {

    private TextView name;
    private TextView mean;
    private Spinner dropdown;

    private View logoutBtn;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        name = findViewById(R.id.name);
        mean = findViewById(R.id.mean);
        dropdown = findViewById(R.id.spinner1);

        logoutBtn = findViewById(R.id.logout_button);
        textView = findViewById(R.id.testview_progress);
        textView.setText("Wyloguj");

        String name_ = getIntent().getStringExtra("username");
        String[] means = getIntent().getStringArrayExtra("means");
        String[] semesters = getIntent().getStringArrayExtra("semesters");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, semesters);

        name.setText(name_);
        mean.setVisibility(View.INVISIBLE);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String mean_;
                DecimalFormat df = new DecimalFormat("0.00");
                if(means[i].equals("NaN")) {
                    mean_ = "-";
                    runToastInAsync("Wszystkie przedmioty podpięte? Oceny są?");
                } else {
                    mean_ = df.format(Float.valueOf(means[i]));
                }
                mean.setText(mean_);
                mean.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Toast.makeText(HomeActivity.this, "Wylogowano!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
        });
    }
    private void runToastInAsync(String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(HomeActivity.this, text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}