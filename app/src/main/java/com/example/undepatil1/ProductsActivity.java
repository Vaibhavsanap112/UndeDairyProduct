package com.example.undepatil1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private GridView gridView;
    private EditText searchInput;
    private Button btnSearch, btnGenerateBill, btnAdminPanel;

    private ArrayList<String> productList;
    private ArrayList<String> filteredProducts;
    private ArrayList<String> selectedProducts = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        gridView = findViewById(R.id.productGridView);
        searchInput = findViewById(R.id.searchInput);
        btnSearch = findViewById(R.id.btnSearch);
        btnGenerateBill = findViewById(R.id.btnGenerateBill); // New Button for generating bill
        btnAdminPanel = findViewById(R.id.btnAdminPanel);

        productList = new ArrayList<>();
        productList.add("Shreekhand - Price: ₹100");
        productList.add("Lassi - Price: ₹80");
        productList.add("Paneer - Price: ₹120");

        filteredProducts = new ArrayList<>(productList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredProducts);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE); // Allow multiple selection
        gridView.setAdapter(adapter);

        // Search functionality
        btnSearch.setOnClickListener(v -> {
            String query = searchInput.getText().toString().toLowerCase();
            filterProducts(query);
        });

        // Go to Admin Panel
        btnAdminPanel.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, AdminPanelActivity.class);
            startActivity(intent);
        });

        // Handle multi-selection of products
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = filteredProducts.get(position);
            if (selectedProducts.contains(selectedProduct)) {
                selectedProducts.remove(selectedProduct); // Deselect product
            } else {
                selectedProducts.add(selectedProduct); // Select product
            }
        });

        // Generate bill button click
        btnGenerateBill.setOnClickListener(v -> {
            if (!selectedProducts.isEmpty()) {
                Intent intent = new Intent(ProductsActivity.this, BillActivity.class);
                intent.putStringArrayListExtra("selectedProducts", selectedProducts);
                startActivity(intent);
            } else {
                Toast.makeText(ProductsActivity.this, "Please select at least one product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        for (String product : productList) {
            if (product.toLowerCase().contains(query)) {
                filteredProducts.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
