package com.example.undepatil1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText productNameInput;
    private EditText productPriceInput;
    private Button btnAddProduct;
    private ListView productListViewAdmin;

    private ArrayList<String> adminProductList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        productNameInput = findViewById(R.id.productNameInput);
        productPriceInput = findViewById(R.id.productPriceInput);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        productListViewAdmin = findViewById(R.id.productListViewAdmin);

        adminProductList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adminProductList);
        productListViewAdmin.setAdapter(adapter);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameInput.getText().toString();
                String productPrice = productPriceInput.getText().toString();
                if (!productName.isEmpty() && !productPrice.isEmpty()) {
                    String productDetails = productName + " - Price: ₹" + productPrice;
                    adminProductList.add(productDetails);
                    adapter.notifyDataSetChanged();
                    productNameInput.setText("");
                    productPriceInput.setText("");
                }
            }
        });
    }
}
