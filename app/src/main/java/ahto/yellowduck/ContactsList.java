package ahto.yellowduck;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ContactsList extends AppCompatActivity {
    public String num;
    public String name;
    private void scanDeviceForContacts(ListView lv){
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter listadapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        lv.setAdapter(listadapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settextrecieverslayout);
        ListView lv = (ListView) findViewById(R.id.list);

        scanDeviceForContacts(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(android.R.id.text2);
                TextView tv2 = (TextView) view.findViewById(android.R.id.text1);
                Toast.makeText(getApplicationContext(), tv.getText(),
                        Toast.LENGTH_SHORT).show();
                num = (String) tv.getText();
                name =  (String) tv2.getText();
            }
        });
        Button backToCreateMsg = (Button) findViewById(R.id.selectContact);
        backToCreateMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num != null) {
                    startActivity(new Intent(getApplicationContext(), SetMessage.class).putExtra("num", num).putExtra("name", name));
                }else{
                    startActivity(new Intent(getApplicationContext(), SetMessage.class));
                }
            }
        });
    }
    public void onBackPressed(){
        if (num != null) {
            startActivity(new Intent(getApplicationContext(), SetMessage.class).putExtra("num", num).putExtra("name", name));
        }else{
            startActivity(new Intent(getApplicationContext(), SetMessage.class));
        }
    }
}
