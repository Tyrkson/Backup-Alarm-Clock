package ahto.yellowduck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Musiclists extends AppCompatActivity {
    Button toMusic;
    Button toRingtone;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclists);

        toMusic = (Button) findViewById(R.id.nimetu1);
        toMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RingtoneList.class));
            }
        });
        toRingtone = (Button) findViewById(R.id.nimetu2);
        toRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Mp3List.class));
            }
        });

    }
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), CreateSound.class));
    }
}
