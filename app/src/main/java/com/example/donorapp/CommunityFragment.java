package com.example.donorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


public class CommunityFragment extends Fragment {
    TextView textBlood4Life, textUtdJakarta;
    CardView cardBlood4Life, cardUTDJakarta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        textBlood4Life = view.findViewById(R.id.textBlood4LifeDesc);
        textBlood4Life.setText("Blood for Life Indonesia adalah komunitas sosial yang dengan sukareala membantu orang lain yang membutuhkan darah."
                                + " Komunitas Blood4Life ...");

        textUtdJakarta = view.findViewById(R.id.textUTDdesc);
        textUtdJakarta.setText("Unit Transfusi Darah merupakan salah satu unit kerja yang ada di PMI Provinsi DKI Jakarta." +
                                " Tugas dan fungsi utamanya ialah meningkatkan ...");

        cardBlood4Life = view.findViewById(R.id.card1);
        cardBlood4Life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), CommunityBlood4Life.class);
                view.getContext().startActivity(i);
            }
        });

        cardUTDJakarta = view.findViewById(R.id.card2);
        cardUTDJakarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(view.getContext(), CommunityUTDJakarta.class);
                view.getContext().startActivity(i);
            }
        });

        return view;
    }
}
