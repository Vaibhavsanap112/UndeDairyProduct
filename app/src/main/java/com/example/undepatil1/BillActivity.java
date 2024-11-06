package com.example.undepatil1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {

    private TextView subtotalView, gstView, totalView;
    private LinearLayout productsContainer;

    private ArrayList<String> selectedProducts;
    private ArrayList<EditText> quantityInputs = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();


        setContentView(R.layout.activity_bill);

        productsContainer = findViewById(R.id.productsContainer); // Ensure this ID exists
        subtotalView = findViewById(R.id.subtotal);
        gstView = findViewById(R.id.gstAmount);
        totalView = findViewById(R.id.totalAmount);

        // Get selected products from intent
        selectedProducts = getIntent().getStringArrayListExtra("selectedProducts");

        // Dynamically create quantity inputs for each selected product
        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            for (String product : selectedProducts) {
                addProductToBill(product);
            }
        } else {
            Toast.makeText(this, "No products selected.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no products were selected
        }

        // Print button functionality
        Button printButton = findViewById(R.id.btnPrint);
        printButton.setOnClickListener(v -> printBill());
    }

    private void addProductToBill(String product) {
        View productView = getLayoutInflater().inflate(R.layout.product_item, productsContainer, false);

        TextView productName = productView.findViewById(R.id.productName);
        EditText productQuantity = productView.findViewById(R.id.productQuantity);

        productName.setText(product);
        quantityInputs.add(productQuantity);

        productsContainer.addView(productView);

        productQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateTotal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void updateTotal() {
        double subtotal = 0.0;




        for (int i = 0; i < selectedProducts.size(); i++) {
            String qtyText = quantityInputs.get(i).getText().toString();
            if (!qtyText.isEmpty()) {
                try {
                    double quantity = Double.parseDouble(qtyText);
                    double price = getPrice(selectedProducts.get(i));
                    subtotal += quantity * price;
                } catch (NumberFormatException e) {
                    // Handle potential parsing issues
                    Toast.makeText(this, "Invalid quantity for " + selectedProducts.get(i), Toast.LENGTH_SHORT).show();
                }
            }
        }

        double gst = subtotal * 0.05; // 5% GST
        double total = subtotal + gst;

        // Update UI
        subtotalView.setText("Subtotal: " + String.format("%.2f", subtotal) + " INR");
        gstView.setText("GST (5%): " + String.format("%.2f", gst) + " INR");
        totalView.setText("Total: " + String.format("%.2f", total) + " INR");
    }

    private double getPrice(String product) {
        if (product.contains("Shreekhand")) return 100;
        if (product.contains("Lassi")) return 80;
        if (product.contains("Paneer")) return 120;
        return 0; // Default price if not found
    }
    private void printBill() {
        // Create a bitmap for the image
        int width = 600;  // Set width of the image
        int height = 800; // Set height of the image
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        // Draw a white background
        canvas.drawColor(Color.WHITE);

        // Draw bill content
        canvas.drawText("Bill Details", 50, 50, paint);
        canvas.drawText("--------------------------------", 50, 80, paint);
        canvas.drawText("Shop Name: Your Shop Name", 50, 110, paint);
        canvas.drawText("Address: Your Shop Address", 50, 130, paint);
        canvas.drawText("Mobile: 123-456-7890", 50, 150, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.drawText("Date: " + LocalDate.now(), 50, 170, paint);
        }
        canvas.drawText("--------------------------------", 50, 200, paint);
        canvas.drawText("Customer Details", 50, 230, paint);
        canvas.drawText("Name: " + ((EditText) findViewById(R.id.customerName)).getText().toString(), 50, 250, paint);
        canvas.drawText("Mobile: " + ((EditText) findViewById(R.id.customerMobile)).getText().toString(), 50, 270, paint);
        canvas.drawText("--------------------------------", 50, 300, paint);
        canvas.drawText("Products:", 50, 330, paint);

        // Add products and their details
        for (int i = 0; i < selectedProducts.size(); i++) {
            String productName = selectedProducts.get(i);
            String qtyText = quantityInputs.get(i).getText().toString();
            if (!qtyText.isEmpty()) {
                canvas.drawText(productName + " - Quantity: " + qtyText + " - Price: " + getPrice(productName) + " INR", 50, 350 + (i * 20), paint);
            }
        }

        // Add subtotal, GST, and total
        canvas.drawText("--------------------------------", 50, 400 + (selectedProducts.size() * 20), paint);
        canvas.drawText(subtotalView.getText().toString(), 50, 420 + (selectedProducts.size() * 20), paint);
        canvas.drawText(gstView.getText().toString(), 50, 440 + (selectedProducts.size() * 20), paint);
        canvas.drawText(totalView.getText().toString(), 50, 460 + (selectedProducts.size() * 20), paint);
        canvas.drawText("--------------------------------", 50, 480 + (selectedProducts.size() * 20), paint);
        canvas.drawText("Thank you for shopping with us!", 50, 500 + (selectedProducts.size() * 20), paint);

        // Save the bitmap to the MediaStore
        String imageFileName = "Bill_" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // Use Pictures directory

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress and save the image
                Toast.makeText(this, "Bill saved as image in Pictures: " + imageFileName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }




}
