package com.example.phobigone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

public class CuriousFactsActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final static Map<Integer, Integer> FACTS_BY_BUTTON_ID= new HashMap<Integer, Integer>() {{
        put(R.id.fact1, R.string.fact1);
        put(R.id.fact2, R.string.fact2);
        put(R.id.fact3, R.string.fact3);
        put(R.id.fact4, R.string.fact4);}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curious_facts);

        //Set the onClick listeners to all the layout buttons
        for (Integer id : FACTS_BY_BUTTON_ID.keySet()){

            AppCompatButton button = findViewById(id);
            //Create a dialog window with the corresponding fact when a button is clicked
            button.setOnClickListener(vw -> {
                createNewDialogWindow(FACTS_BY_BUTTON_ID.get(vw.getId()));
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return(super.onOptionsItemSelected(item));
    }

    //Create a dialog window with the text with id textId
    private void createNewDialogWindow(Integer textId){
        dialogBuilder = new AlertDialog.Builder(this);
        final View factPopUpView = getLayoutInflater().inflate(R.layout.fact_pop_up, null);

        //Set the dialog window display and text and show it
        dialogBuilder.setView(factPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();
        TextView factView = dialog.findViewById(R.id.factTextView);
        factView.setText(getResources().getString(textId));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
}