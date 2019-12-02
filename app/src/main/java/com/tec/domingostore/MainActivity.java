package com.tec.domingostore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.tec.domingostore.DB.AuthFirebase;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.Main.Payment.PaymentFragment;
import com.tec.domingostore.Main.Products.ProductsFragment;
import com.tec.domingostore.Main.Customer.CustomerFragment;
import com.tec.domingostore.Main.profile.ProfileFragment;
import com.tec.domingostore.Main.Sale.SalesFragment;
import com.tec.domingostore.Main.Seller.SellerFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthFirebase.setup();

//        if (!AuthFirebase.isLogged(getApplicationContext())){
//            Intent intent   = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
            initComponents();
//        }

    }

    private void initComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SyncFirebase.setup(getApplicationContext());


         drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_products, R.id.nav_seller, R.id.nav_customer,
//                R.id.nav_sale, R.id.nav_profile, R.id.nav_logout)
//                .setDrawerLayout(drawer)
//                .build();


//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_products:
                        fragment = new ProductsFragment();
                        break;
                    case R.id.nav_seller:
                        fragment = new SellerFragment();
                        break;
                    case R.id.nav_customer:
                        fragment = new CustomerFragment();
                        break;
                    case R.id.nav_sale:
                        fragment = new SalesFragment();
                        break;
                    case R.id.nav_payment:
                        fragment = new PaymentFragment();
                        break;
//                    case R.id.nav_profile:
//                        fragment = new ProfileFragment();
//                        break;
//                    case R.id.nav_logout:
//                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.clear();
//                        editor.apply();
//
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                        finish();
//                        break;
                }

                drawer.closeDrawers();

                if (fragment != null) {
                    changeFragment(fragment);
                }

                return true;
            }
        });

        changeFragment(new ProductsFragment());


//        if (getApplicationContext() != null)
//            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(message,
//                    new IntentFilter(SyncFirebase.UPDATED_SELLERS));
    }

//    private BroadcastReceiver message = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            QueryBuilder<Seller> queryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
//            List<Seller> sellerList = queryBuilder.list();
//
//            for (Seller seller: sellerList)
//                Toast.makeText(context,seller.getName(),Toast.LENGTH_LONG).show();
//        }
//    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.commit();
    }
}
