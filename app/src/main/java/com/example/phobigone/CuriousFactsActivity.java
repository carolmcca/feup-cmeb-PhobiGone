package com.example.phobigone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CuriousFactsActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final static Map<Integer, Integer> FACTS_BY_BUTTON_ID= new HashMap<Integer, Integer>() {{
        put(R.id.fact1, R.string.fact1);
        put(R.id.fact2, R.string.fact2);
        put(R.id.fact3, R.string.fact3);
        put(R.id.fact4, R.string.fact1);}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curious_facts);

        for (Integer id : FACTS_BY_BUTTON_ID.keySet()){

            AppCompatButton button = findViewById(id);

            button.setOnClickListener(vw -> {
                createNewDialogWindow(FACTS_BY_BUTTON_ID.get(vw.getId()));
            });
        }
    }

    private void createNewDialogWindow(Integer textId){
        dialogBuilder = new AlertDialog.Builder(this);
        final View factPopUpView = getLayoutInflater().inflate(R.layout.fact_pop_up, null);

        dialogBuilder.setView(factPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();
        TextView factView = dialog.findViewById(R.id.factTextView);
        factView.setText(getResources().getString(textId));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
}