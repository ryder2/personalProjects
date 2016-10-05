package test.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PortailUser extends Activity implements View.OnClickListener {

    Button consulterOffres, voirProfil;
    String username;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portail_user);

        Bundle b = getIntent().getExtras();

        username = b.getString("username");


        consulterOffres = (Button) findViewById(R.id.voirmesoffres);
        voirProfil = (Button) findViewById(R.id.consulterprofil);

        consulterOffres.setOnClickListener(this);
        voirProfil.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.voirmesoffres:
                Intent i = new Intent(PortailUser.this, ReadCommentsUser.class);
                Bundle b = new Bundle();
                b.putString("username", username);
                i.putExtras(b);
                finish();
                startActivity(i);
                break;

            case R.id.consulterprofil:
                Intent j = new Intent(PortailUser.this, ProfilUser.class);
                Bundle c = new Bundle();
                c.putString("username", username);
                j.putExtras(c);
                finish();
                startActivity(j);
                break;
        }
    }
}

