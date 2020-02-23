package ahto.yellowduck;


import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;


public class RingtoneList extends AppCompatActivity {
    List<String> laulud = new ArrayList<>();
    List<String> nimed = new ArrayList<>();
    MediaPlayer mp = new MediaPlayer();
    public Uri myUri;
    public String sName;
    public String nimi;
    public String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmmusiclayout);
        ListView lv = (ListView) findViewById(R.id.list);
        scanDeviceForRingtones(laulud, nimed);
        ArrayAdapter<String> songlist = new ArrayAdapter<>(this, R.layout.test, nimed);
        lv.setAdapter(songlist);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)  {
                myUri = Uri.parse(laulud.get(i));

                name = nimed.get(i);
                nimi = myUri.toString();

                sName = nimed.get(i);
                if(mp.isPlaying()) {
                    mp.reset();
                }
                mp = MediaPlayer.create(getApplicationContext(), myUri);
                mp.start();
            }
        });
        Button selectSong = (Button) findViewById(R.id.SongSelected);
        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                startActivity(new Intent(getApplicationContext(), CreateSound.class).putExtra("path", nimi).putExtra("nimi", name));
            }
        });


    }

    private void scanDeviceForRingtones(List<String> laulud, List<String> nimed) {
        RingtoneManager rm = new RingtoneManager(this);
        rm.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = rm.getCursor();

        while(cursor.moveToNext()){
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);

            laulud.add(uri);
            nimed.add(title);

        }
        cursor.close();
    }
    public void onBackPressed(){
        if(mp.isPlaying()){
            mp.stop();
            mp.release();
            mp = null;
        }
        startActivity(new Intent(getApplicationContext(), Musiclists.class));
    }
    public void onStop(){
        super.onStop();
        if(mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }
    }

}
