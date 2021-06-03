package heli.mrc.smartindicator;

import android.content.res.TypedArray;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import heli.mrc.smartIndicator.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SmartIndicator)findViewById(R.id.smartIndicator2)).setComponentClickListener(new SmartIndicator.OnComponentClickListener() {
            @Override
            public void onComponentClicked(ViewGroup parent, int index) {
                Toast.makeText(getApplication(),"component" + index+ " checked ",Toast.LENGTH_SHORT).show();
            }
        });
    }


}