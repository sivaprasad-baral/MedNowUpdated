package com.example.mednow.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednow.R;
import com.example.mednow.adapter.OrderHistoryAdapter;
import com.example.mednow.model.Order;
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

public class HistoryFragment extends Fragment {

    private List<Order> orders;
    private ViewGroup container;

    private RecyclerView recyclerView;
    private OrderHistoryAdapter historyAdapter;
    private TextView textViewNoOrderMsg;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        this.container = container;
        recyclerView = view.findViewById(R.id.history_fragment_recycler_view);
        textViewNoOrderMsg = view.findViewById(R.id.history_fragment_no_order_msg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(container).getContext()));
        historyAdapter = new OrderHistoryAdapter(container.getContext(),new ArrayList<Order>());
        recyclerView.setAdapter(historyAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        getOrderList();

        return view;
    }

    private void getOrderList() {
        orders = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders.clear();
                databaseReference.removeEventListener(this);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if(Objects.requireNonNull(order).getCustomerId().equals(firebaseUser.getUid())) {
                        orders.add(order);
                    }
                }
                if(orders.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    textViewNoOrderMsg.setVisibility(View.VISIBLE);
                } else {
                    historyAdapter = new OrderHistoryAdapter(container.getContext(),orders);
                    recyclerView.setAdapter(historyAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(container.getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
