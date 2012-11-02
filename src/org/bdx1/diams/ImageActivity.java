package org.bdx1.diams;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.views.DiamsImageView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        DiamsImageView imgView = (DiamsImageView) findViewById(R.id.imageView);
        Examen ex = ((DiamsApplication) getApplication()).getCurrentExamen();
        imgView.setImage(ex.getSlice(0).getImage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image, menu);
        return true;
    }
}
