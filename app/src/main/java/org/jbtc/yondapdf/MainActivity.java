package org.jbtc.yondapdf;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.util.Log;
import android.util.TypedValue;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY = "primary";
    private static final String dbName = "bookslightnovel";
    private static final String TAG = "iMain";

    NavController navController;
    AssetManager assetManager;
    RoomDatabaseBooksLN rdb;
    ActivityMainBinding binding;
    //todo:xjava
    //todo:layoutu notification
    //todo:image
    //todo:progresbar
    //todo:click notification open book
    //todo:al dar click en play en fragmente q reabra el servicio si fue cerrado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //NavigationView navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
        //NavigationUI.setupWithNavController(navigationView, navController);


        rdb = Room.databaseBuilder(getApplicationContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

    }




    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: ");
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

                //menu.findItem(R.id.action_botspeak).setVisible(false);
        //Log.i("as21", "onCreateOptionsMenu: paso por menu del mainactivity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_novelas: {
                Log.i(TAG, "onOptionsItemSelected: action_novelas : de main activity");
                //Toast.makeText(this,"action_novelas : de main activity",Toast.LENGTH_LONG).show();
                break;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "onPrepareOptionsMenu: ");
        return super.onPrepareOptionsMenu(menu);
    }

    */



    public void setTextSizeToolbar(float sp, String source){
        Toolbar toolbar = findViewById(R.id.toolbar);
        //((TextView)toolbar.getChildAt(0)).setTypeface(typeFace);
        Log.i("TAG12", "setTextSizeToolbar: count: "+toolbar.getChildCount());
        for(int i=0;i<toolbar.getChildCount();i++) {
            String clase=toolbar.getChildAt(i).getClass().getName();
            Log.i("TAG12", "setTextSizeToolbar: "+source+" tipo: "+clase);
            if(clase.equals(AppCompatTextView.class.getName())){
                ((TextView) toolbar.getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX,sp);
                break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
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
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.MANAGE_DOCUMENTS}, 1);
        }
    }

    public ActivityMainBinding getActivityMainBinding(){
        return binding;
    }

}