package com.dam2.trabajofinal1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        setSupportActionBar(toolbar);

        FirebaseStorage.getInstance().getReference().child("images/").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                ArrayList<Image> lista = new ArrayList<>();
                AdaptadorImagen adaptador= new AdaptadorImagen(MainActivity2.this,lista);
                adaptador.setOnItemClickListener(new AdaptadorImagen.OnItemClickListener() {
                    @Override
                    public void onClick(Image image) {
                        Intent intent= new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(image.getUrl()),"image/*");
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adaptador);
                listResult.getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        Image image =new Image();
                        image.setName(storageReference.getName());
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    Uri uri = task.getResult();
                                    String url = uri.getScheme() + "://" + uri.getEncodedAuthority() + uri.getEncodedPath();
                                    String token = uri.getQueryParameter("token");

                                    if (token != null) {
                                        url += "?alt=media&token=" + token;
                                    }

                                    image.setUrl(url);
                                    lista.add(image);
                                    adaptador.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MainActivity2.this, "Error al obtener la URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity2.this, "se provoco un error", Toast.LENGTH_SHORT).show();
            }
        });







    }
}