package org.jbtc.yondapdf;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.util.TypedValue;

import android.view.View;
import android.widget.TextView;

import org.jbtc.yondapdf.database.RoomDatabaseBooksLN;
import org.jbtc.yondapdf.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY = "primary";
    private static final String dbName = "bookslightnovel";
    private static final String TAG = "iMain";

    private NavController navController;
    private AssetManager assetManager;
    private RoomDatabaseBooksLN rdb;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);

        rdb = Room.databaseBuilder(getApplicationContext(),
                RoomDatabaseBooksLN.class, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .build();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupPDFBox();
    }

    private void setupPDFBox() {
        // Enable Android-style asset loading (highly recommended)
        PDFBoxResourceLoader.init(getApplicationContext());
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

    public void setActionBarTille(String title){
        getSupportActionBar().setTitle(title);
    }

    public void setTextSizeToolbar(float sp){
        Toolbar toolbar = findViewById(R.id.toolbar);
        for(int i=0;i<toolbar.getChildCount();i++) {
            String clase=toolbar.getChildAt(i).getClass().getName();
            if(clase.equals(AppCompatTextView.class.getName())){
                ((TextView) toolbar.getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX,sp);
                break;
            }
        }
    }

    public void setTextCurrentPage(int number){
        binding.tvMainPageText.setText(String.valueOf(number));
    }

    public void setIconTagPageOnClick(View.OnClickListener i){
        binding.flMainPageicon.setOnClickListener(i);
    }

    public void setVisibilityIconTagPage(int visibility){
        binding.flMainPageicon.setVisibility(visibility);
    }

}