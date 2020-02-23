package ahto.yellowduck;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetMessage extends AppCompatActivity {
    static String number;
    static String name;
    static String SMS;
    public EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessageclass);

        if (getIntent().hasExtra("num")){
            number = getIntent().getExtras().getString("num");
            name = getIntent().getExtras().getString("name");
        }
        if (getIntent().hasExtra("SMS")){
            number = getIntent().getExtras().getString("num");
            name = getIntent().getExtras().getString("name");
            SMS = getIntent().getExtras().getString("SMS");
        }
        et = (EditText) findViewById(R.id.editText4);
        TextView Num = (TextView) findViewById(R.id.textView2);
        if(number != null){
            Num.setText(name + "\n" + number);
            et.setText(SMS);

        }
        Button back = (Button) findViewById(R.id.done2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toset = new Intent(SetMessage.this, AlarmMaker.class);
                toset.putExtra("num", number);
                toset.putExtra("name", name);
                toset.putExtra("SMS", et.getText().toString());
                startActivity(toset);
            }
        });

        Button Contacts = (Button) findViewById(R.id.ContactsList);
        Contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SetMessage.this,
                            new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }else{
                    startActivity(new Intent(SetMessage.this, ContactsList.class));
                    SMS = et.getText().toString();
                }
            }
        });

    }
    public void onBackPressed(){
        Intent toset = new Intent(SetMessage.this, AlarmMaker.class);
        toset.putExtra("num", number);
        toset.putExtra("name", name);
        toset.putExtra("SMS", et.getText().toString());
        startActivity(toset);
    }
}
