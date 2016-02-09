package ca.androidtutorial.advancedhelloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cheescakeButton = (Button) findViewById(R.id.button);
        Button lavacakeButton = (Button) findViewById(R.id.button2);

        cheescakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),  "I want Cheescake!!!", Toast.LENGTH_LONG).show();
            }
        });

        lavacakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "I want Lava Cake!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
