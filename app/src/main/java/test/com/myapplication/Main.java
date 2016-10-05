package test.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by yoann on 2016-04-28.
 * Have fun!
 */
public class Main extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button client, mecano;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //setup buttons
        client = (Button)findViewById(R.id.client);
        mecano = (Button)findViewById(R.id.mecano);

        //register listeners
        client.setOnClickListener(this);
        mecano.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test, menu);

        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.client:
                Intent i1 = new Intent(this, Login.class);
                startActivity(i1);
                break;
            case R.id.mecano:
                Intent i2 = new Intent(this, LoginMecano.class);
                startActivity(i2);
                break;

            default:
                break;
        }
    }
}
