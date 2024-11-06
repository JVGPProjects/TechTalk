package com.example.techtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.Manifest;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView profileUsernameTextView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView profileImageButton;
    private Uri profileImageUri; // Store the selected image URI
    private Bitmap profileImageBitmap; // Store the captured image bitmap
    private String previousProfileImageUrl; // Store the previous profile image URL
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage
        storageRef = storage.getReference(); // Get a reference to the storage

        profileImageButton = findViewById(R.id.profileImageButton);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        profileUsernameTextView = findViewById(R.id.profileUsernameTextView);
        Button logoutButton = findViewById(R.id.logoutButton);


        usernameTextView.setOnClickListener(v -> showInputDialog("Change Username", usernameTextView.getText().toString(), "username"));
        profileUsernameTextView.setOnClickListener(v -> showInputDialog("Change Profile Username", profileUsernameTextView.getText().toString(), "profileUsername"));



        profileImageButton.setOnClickListener(v -> showImageSelectionDialog());

        requestPermissions();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fetchUserData();
        setTextViewClickListeners();
    }

    private void showInputDialog(String title, String currentValue, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Set up the input field with margins
        final EditText input = new EditText(this);
        input.setText(currentValue);
        input.setPadding(30, 30, 30, 30); // Adds padding if needed

        // Wrap the EditText in a FrameLayout to apply margins
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 5, 50, 5); // Set left and right margins
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton("Save", null); // Set null here to override later
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            // Get the "Save" button
            final Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setEnabled(input.getText().toString().trim().length() <= 7); // Initial state

            // Add a TextWatcher to enable/disable Save button based on input length
            input.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Enable Save button only if username is 7 characters or fewer
                    saveButton.setEnabled(s.toString().trim().length() <= 7);
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });

            // Set the click listener for the "Save" button
            saveButton.setOnClickListener(v -> {
                String newValue = input.getText().toString().trim();
                if (!newValue.isEmpty() && newValue.length() <= 7) {
                    updateUserDataInFirebase(field, newValue);
                    dialog.dismiss(); // Close dialog after saving
                }
            });
        });

        dialog.show();
    }


    // Handle clicks on the TextViews
    private void setTextViewClickListeners() {
        usernameTextView.setOnClickListener(v -> showInputDialog("Edit Nickname", usernameTextView.getText().toString(), "username"));
        profileUsernameTextView.setOnClickListener(v -> showInputDialog("Edit Nickname", profileUsernameTextView.getText().toString(), "profileUsername"));
    }

    // Update user data in Firebase
    private void updateUserDataInFirebase(String field, String newValue) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).update(field, newValue)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AccountActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    // Update the TextView to reflect the change
                    switch (field) {
                        case "username":
                            usernameTextView.setText(newValue);
                            profileUsernameTextView.setText(newValue); // Assuming profileUsername is the same as username
                            break;
                        case "profileUsername":
                            profileUsernameTextView.setText(newValue);
                            break;
                        case "email":
                            emailTextView.setText(newValue);
                            break;
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showImageSelectionDialog() {
        String[] options = {"Capture Photo", "Choose from Gallery", "Select a Profile"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Image")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Capture Photo
                            if (hasCameraPermission()) {
                                capturePhoto();
                            } else {
                                requestCameraPermission();
                            }
                            break;
                        case 1: // Choose from Gallery
                            if (hasReadStoragePermission()) {
                                pickImageFromGallery();
                            } else {
                                requestReadStoragePermission();
                            }
                            break;
                        case 2: // Select from App's Profiles
                            selectProfileFromApp();
                            break;
                    }
                });
        builder.show();
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void selectProfileFromApp() {
        int[] predefinedProfiles = {R.drawable.pfp2, R.drawable.pfp3, R.drawable.pfp4, R.drawable.pfp5, R.drawable.pfp6, R.drawable.pfp7};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Profile Picture");

        // Set up the custom layout for the dialog (GridView with ImageViews)
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_predefined_profiles, null);
        builder.setView(dialogView);

        GridView gridView = dialogView.findViewById(R.id.profileGridView);
        ProfileImageAdapter adapter = new ProfileImageAdapter(this, predefinedProfiles);
        gridView.setAdapter(adapter);

        // Handle the selection of a profile image
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedProfileImage = predefinedProfiles[position];

            // Immediately load the selected image
            loadProfileImage(selectedProfileImage); // Update the current profile image
            showSaveChangesDialogForPredefinedProfile("Do you want to save this profile image?", selectedProfileImage);

            // Dismiss the dialog after selection
            builder.create().dismiss();
        });

        // Show the dialog
        builder.show();
    }



    private void showSaveChangesDialogForPredefinedProfile(String message, int drawable) {
        new AlertDialog.Builder(this)
                .setTitle("Save Changes")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> savePredefinedProfileToFirebase(drawable))
                .setNegativeButton("No", (dialog, which) -> revertProfileImage())
                .show();
    }

    private void savePredefinedProfileToFirebase(int drawable) {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference profileImageRef = storageRef.child("profileImages/" + userId + ".jpg");

        // Convert the drawable to a Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        Uri imageUri = getImageUri(bitmap);

        // Upload the image
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(this::updateProfileImageUrlInFirestore)
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show()));
        Toast.makeText(AccountActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the latest user data when activity resumes
        fetchUserData();
    }


    private void loadProfileImage(int drawable) {
        Glide.with(this)
                .load(drawable)
                .circleCrop()
                .into(profileImageButton);
        previousProfileImageUrl = ""; // Reset previous URL when loading from drawable
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    profileImageBitmap = (Bitmap) extras.get("data");
                    // Use Glide to load the bitmap
                    Glide.with(this)
                            .load(profileImageBitmap)
                            .circleCrop()
                            .into(profileImageButton);
                    showSaveChangesDialog("Do you want to save this image?", profileImageBitmap);
                }
            } else if (requestCode == PICK_IMAGE) {
                profileImageUri = data.getData(); // Get the URI of the selected image
                Glide.with(this)
                        .load(profileImageUri)
                        .circleCrop()
                        .into(profileImageButton);
                showSaveChangesDialog("Do you want to save this image?", profileImageUri);
            }
        }
    }

    private void showSaveChangesDialog(String message, Bitmap bitmap) {
        new AlertDialog.Builder(this)
                .setTitle("Save Changes")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> saveProfileImageToFirebase(bitmap))
                .setNegativeButton("No", (dialog, which) -> revertProfileImage())
                .show();
    }

    private void showSaveChangesDialog(String message, Uri uri) {
        new AlertDialog.Builder(this)
                .setTitle("Save Changes")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> saveProfileImageToFirebase(uri))
                .setNegativeButton("No", (dialog, which) -> revertProfileImage())
                .show();
    }

    private void revertProfileImage() {
        // Revert to the previous profile image
        if (previousProfileImageUrl != null && !previousProfileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(previousProfileImageUrl)
                    .circleCrop()
                    .into(profileImageButton);
        } else {
            // If there's no previous image, set to default or clear the image
            profileImageButton.setImageResource(R.drawable.pfp1); // Set a default image or clear
        }
    }

    private void saveProfileImageToFirebase(Bitmap bitmap) {
        // Convert Bitmap to Uri and then upload
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference profileImageRef = storageRef.child("profileImages/" + userId + ".jpg");

        profileImageRef.putFile(getImageUri(bitmap))
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(this::updateProfileImageUrlInFirestore))
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfileImageToFirebase(Uri uri) {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference profileImageRef = storageRef.child("profileImages/" + userId + ".jpg");

        profileImageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(this::updateProfileImageUrlInFirestore))
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Convert bitmap to Uri
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Profile Image", null);
        return Uri.parse(path);
    }

    private void updateProfileImageUrlInFirestore(Uri uri) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).update("profileImageUrl", uri.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AccountActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                    previousProfileImageUrl = uri.toString(); // Update the previous image URL
                })
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Failed to update profile image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void requestPermissions() {
        if (!hasCameraPermission() || !hasReadStoragePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void requestReadStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    emailTextView.setText(mAuth.getCurrentUser().getEmail());
                    usernameTextView.setText(documentSnapshot.getString("username"));
                    profileUsernameTextView.setText(documentSnapshot.getString("username"));
                    previousProfileImageUrl = documentSnapshot.getString("profileImageUrl");
                    if (previousProfileImageUrl != null && !previousProfileImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(previousProfileImageUrl)
                                .circleCrop()
                                .into(profileImageButton);
                    } else {
                        profileImageButton.setImageResource(R.drawable.pfp1); // Set a default image if none exists
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Back button click handler
    public void onBackButtonClick(View view) {
        // Close the current activity and return to the previous one
        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStoragePermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (!cameraPermissionGranted) {
                    Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
                }
                if (!readStoragePermissionGranted) {
                    Toast.makeText(this, "Read storage permission is required to select images.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
