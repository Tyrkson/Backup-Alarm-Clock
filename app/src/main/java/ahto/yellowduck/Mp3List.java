package ahto.yellowduck;


import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Mp3List extends AppCompatActivity{
    List<String> laulud = new ArrayList<>();
    List<String> nimed = new ArrayList<>();
    MediaPlayer mp = new MediaPlayer();
    public Uri myUri;
    public String sName;
    public String nimi;
    public String name;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclist);
        ListView lv = (ListView) findViewById(R.id.list);
        scanDeviceForMpFiles(laulud, nimed);
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
        Button selectSong = (Button) findViewById(R.id.rename);
        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                startActivity(new Intent(getApplicationContext(), CreateSound.class).putExtra("path", nimi).putExtra("nimi", name));
            }
        });
    }

    private void scanDeviceForMpFiles(List<String> mp3Files, List<String> mp3names) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " ASC";
        Cursor cursor;
        Uri uri;
        for(int i = 0; i<2; i++) {
            if(i == 0){
                uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            }else {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(0);
                    String path = cursor.getString(2);
                    cursor.moveToNext();
                    if (path != null && path.endsWith(".mp3")) {
                        mp3Files.add(path);
                        mp3names.add(title);
                    }
                }
            }
            for (String file : mp3Files) {
                Log.i("TAG", file);
            }

            if (cursor != null) {
                cursor.close();
            }
        }

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
