package org.bdx1.diams;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.views.DiamsImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ImageActivity extends Activity {

    private DiamsImageView imageView;
    private SeekBar centerSlider;
    private SeekBar widthSlider;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        
        imageView = (DiamsImageView) findViewById(R.id.imageView);
        
        centerSlider = (SeekBar) findViewById(R.id.centerSlider);
        widthSlider = (SeekBar) findViewById(R.id.widthSlider);
        
        centerSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            public void onStopTrackingTouch(SeekBar seekBar) {                
            }
            
            public void onStartTrackingTouch(SeekBar seekBar) {                
            }
            
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                changeWindowCenter(progress);
            }
        });
        
        widthSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            public void onStopTrackingTouch(SeekBar seekBar) {                
            }
            
            public void onStartTrackingTouch(SeekBar seekBar) {                
            }
            
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                changeWindowWidth(progress);
            }
        });
        
        Examen ex = ((DiamsApplication) getApplication()).getCurrentExamen();
        imageView.setSlice(ex.getSlice(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image, menu);
        return true;
    }
    
    public void showInfos(View v) {
        Intent intent = new Intent(getApplicationContext(), InfoDisplayActivity.class);
        startActivity(intent);
    }
    
    public void changeWindowCenter(int newCenter) {
        imageView.setWindowCenter(newCenter);
        imageView.invalidate();
    }
    
    public void changeWindowWidth(int newWidth) {
        imageView.setWindowWidth(newWidth);
        imageView.invalidate();
    }
}
