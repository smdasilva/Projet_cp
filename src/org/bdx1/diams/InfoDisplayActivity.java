package org.bdx1.diams;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.views.InfosView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class InfoDisplayActivity extends Activity {

    private Examen exam;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);
        DiamsApplication app = (DiamsApplication) getApplication();
        exam = app.getCurrentExamen();
        InfosView view = (InfosView) findViewById(R.id.infos);
        if (exam != null)
            view.setExamen(exam, exam.getSlice(app.getCurrentSliceIndex()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_info_display, menu);
        return true;
    }
}
