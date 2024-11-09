package com.dam2.trabajofinal1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SubirFotos extends AppCompatActivity {
StorageReference storageReference;
LinearProgressIndicator progress;
Uri imagen;
Button botonSeleccionar, botonSubir, botonDescargar;
ImageView imageView;
private final ActivityResultLauncher<Intent> activityResultLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
{
    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {

            if (result.getData() != null) {
                imagen = result.getData().getData();
                botonSubir.setEnabled(true);
                Glide.with(getApplicationContext()).load(imagen).into(imageView);
            }
            else {
                Toast.makeText(SubirFotos.this, "selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
});
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subir_fotos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(SubirFotos.this);
        storageReference= FirebaseStorage.getInstance().getReference();
        imageView=findViewById(R.id.imageView);
        botonSubir=findViewById(R.id.button_update);
        botonSeleccionar=findViewById(R.id.button_seleccionar);
        progress=findViewById(R.id.progress);
        botonDescargar=findViewById(R.id.buttonFotosNube);

        botonSeleccionar.setOnClickListener(new View. OnClickListener(){
            @Override
            public void onClick (View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
    botonDescargar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            verFotos(view);
        }
    });
    botonSubir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            subirImagen(imagen);
        }
    });



    }
        private void subirImagen(Uri imagen){
            StorageReference refenencia =storageReference.child("images/"+ UUID.randomUUID().toString());
            refenencia.putFile(imagen).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SubirFotos.this, "imagen subida", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SubirFotos.this, "no se logro subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progress.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                    progress.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
                }
            });
        }
    public void verFotos(View view){

        Intent i = new Intent(this, MainActivity2.class);
        startActivity(i);



    }
}
