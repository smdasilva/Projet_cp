package org.bdx1.diams;

import java.io.File;

import org.bdx1.diams.model.Examen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File dicomFile = new File(
                Environment.getExternalStorageDirectory()+"/dicomTest1.dcm");
        Examen exam = Factory.MODEL_FACTORY.makeExamen(dicomFile);
        DiamsApplication app = (DiamsApplication) getApplication();
        app.setCurrentExamen(exam);
        Intent intent = new Intent(getApplicationContext(), InfoDisplayActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
