package ahto.yellowduck;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class CreateSound extends AppCompatActivity {
    static int vibrate;
    public static int vol;
    static String nimi;
    static String musicN;
    public SeekBar volume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound);
        if(getIntent().hasExtra("vol")){
            nimi = getIntent().getExtras().getString("path");
            musicN = getIntent().getExtras().getString("nimi");
            vol = getIntent().getExtras().getInt("vol");
            vibrate = getIntent().getExtras().getInt("vib");
        }
        if(getIntent().hasExtra("path")){
            nimi = getIntent().getExtras().getString("path");
            musicN = getIntent().getExtras().getString("nimi");
        }
        TextView tv = (TextView) findViewById(R.id.name);
        tv.setTextColor(getResources().
                getColor(R.color.colorAccent));
        tv.setSelected(true);
        if (musicN == null){
            tv.setText("SILENT");
        }else{
            tv.setText(musicN);
        }
        //Nupp, mis avab mp3 ja ringtone failide kausta
        Button music = (Button) findViewById(R.id.MUSIC);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateSound.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                else {
                    Intent tomp3 = new Intent(CreateSound.this, Musiclists.class);
                    startActivity(tomp3);
                }
            }
        });
        //Switch, mis kontrollib, kas telefon väriseb, kui äratab
        final Switch vib = (Switch) findViewById(R.id.vibratsioon);
        vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (vib.isChecked()){
                    vibrate = 1;
                } else vibrate = 0;
            }
        });
        //Seekbar, saab äratuse helitugevust reguleerida
        volume = (SeekBar) findViewById(R.id.seekBar3);
        if(getIntent().hasExtra("vol")){
          volume.setProgress(getIntent().getExtras().getInt("vol"));
        }

        Button back = (Button) findViewById(R.id.done1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vol = volume.getProgress();
                Intent back = new Intent(CreateSound.this, AlarmMaker.class);
                back.putExtra("vib", vibrate);
                back.putExtra("vol", vol);
                if (nimi != null){
                    back.putExtra("path", nimi);
                    back.putExtra("nimi", musicN);
                }
                startActivity(back);

            }
        });




    }
    public void onBackPressed(){
        vol = volume.getProgress();
        Intent back = new Intent(CreateSound.this, AlarmMaker.class);
        back.putExtra("vib", vibrate);
        back.putExtra("vol", vol);
        if (nimi != null){
            back.putExtra("path", nimi);
            back.putExtra("nimi", musicN);
        }
        startActivity(back);
    }
}
