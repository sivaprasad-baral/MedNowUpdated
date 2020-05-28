package com.example.mednow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mednow.adapter.MessageAdapter;
import com.example.mednow.model.Chat;
import com.example.mednow.model.Partner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChemistChat extends AppCompatActivity {

    String chemistUserId;
    List<Chat> chats;

    TextView textViewPharmacyName;
    EditText editTextMsg;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReferencePartners,databaseReferenceChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemist_chat);

        textViewPharmacyName = findViewById(R.id.chemist_chat_text_view_pharmacy_name);
        editTextMsg = findViewById(R.id.chemist_chat_edit_text_message);
        recyclerView = findViewById(R.id.chemist_chat_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChemistChat.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MessageAdapter(ChemistChat.this,new ArrayList<Chat>()));

        setSupportActionBar((Toolbar) findViewById(R.id.customer_chat_toolbar));
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chemistUserId = getIntent().getStringExtra("partnerUserId");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferencePartners = FirebaseDatabase.getInstance().getReference().child("Partners").child(chemistUserId);
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child("Chats");
        databaseReferencePartners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Partner partner = dataSnapshot.getValue(Partner.class);
                databaseReferencePartners.removeEventListener(this);
                textViewPharmacyName.setText(Objects.requireNonNull(partner).getPharmacyName());
                readMsg();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
                Toast.makeText(ChemistChat.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMsgBtn(View view) {
        String msg = editTextMsg.getText().toString();
        if(!msg.isEmpty()) {
            Chat chat = new Chat(firebaseUser.getUid(),chemistUserId,msg);
            databaseReferenceChats.push().setValue(chat);
        } else {
            editTextMsg.requestFocus();
            Toast.makeText(ChemistChat.this,"Cannot send empty message",Toast.LENGTH_SHORT).show();
        }
        editTextMsg.setText(null);
    }

    public void readMsg() {
        chats = new ArrayList<>();
        databaseReferenceChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(Objects.requireNonNull(chat).getReceiverId().equals(chemistUserId) && chat.getSenderId().equals(firebaseUser.getUid()) || chat.getSenderId().equals(chemistUserId) && chat.getReceiverId().equals(firebaseUser.getUid())) {
                        chats.add(chat);
                    }
                    recyclerView.setAdapter(new MessageAdapter(ChemistChat.this,chats));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChemistChat.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
