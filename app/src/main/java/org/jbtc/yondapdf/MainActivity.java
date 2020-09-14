package org.jbtc.yondapdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.ActivityMainBinding;
import org.jbtc.yondapdf.entidad.Book;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY = "primary";
    private static final String dbName = "bookslightnovel";

    NavController navController;
    AssetManager assetManager;
    RoomDatabaseBooksLN rdb;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NavigationView navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);


        rdb = Room.databaseBuilder(getApplicationContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.findItem(R.id.action_botspeak).setVisible(false);
        Log.i("as21", "onCreateOptionsMenu: paso por menu del mainactivity");
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTextSizeToolbar(float sp){
        Toolbar toolbar = findViewById(R.id.toolbar);
        //((TextView)toolbar.getChildAt(0)).setTypeface(typeFace);
        ((TextView) toolbar.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_PX,sp);
    }



    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }

    private void setup() {
        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getApplicationContext());
        // Find the root of the external storage.
        //root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        assetManager = getAssets();
        // Need to ask for write permissions on SDK 23 and up, this is ignored on older versions
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public ActivityMainBinding getActivityMainBinding(){
        return binding;
    }

}