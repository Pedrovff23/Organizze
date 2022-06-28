package com.example.organizze.config;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.organizze.activity.CadastrarActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    //FirebaseAuth
    private static FirebaseAuth autenticacao;

    //FirebaseDataBase
    private static DatabaseReference databaseReference;




    public static DatabaseReference getdatabase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

}
