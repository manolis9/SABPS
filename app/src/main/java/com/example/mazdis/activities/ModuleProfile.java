package com.example.mazdis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.mazdis.sabps.R;

public class ModuleProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_profile);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.7), (int) (height*0.4));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0,70,0,70);

        TextView title = (TextView) findViewById(R.id.title_textview);
        TextView address = (TextView) findViewById(R.id.address_textview);
        TextView price = (TextView) findViewById(R.id.price_textview);

        title.setText(getIntent().getStringExtra("title"));
        address.setText(getIntent().getStringExtra("address"));
        String rawPrice = getIntent().getStringExtra("price");
        price.setText('$' + rawPrice);

    }

    public void startReservedMap(View view){

        String reservedAddress = getIntent().getStringExtra("address");
        String reservedTitle = getIntent().getStringExtra("title");

        Intent intent = new Intent(this, ReservedMapsActivity.class);
        intent.putExtra("reservedAddress", reservedAddress);
        intent.putExtra("reservedTitle", reservedTitle);
        startActivity(intent);
        finish();

    }
}
