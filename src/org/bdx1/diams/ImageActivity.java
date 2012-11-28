package org.bdx1.diams;

import java.util.Map;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.Slice;
import org.bdx1.diams.views.DiamsImageView;
import org.bdx1.diams.views.DrawView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ImageActivity extends Activity {

    private DiamsImageView imageView;
    private DrawView drawView;
    private SeekBar centerSlider;
    private SeekBar widthSlider;
    private SeekBar zoomSlider;
    private TextView centerText;
    private TextView widthText;
    private DiamsApplication app;
    private StringBuilder builder = new StringBuilder();
    private Button sliceInc, sliceDec;
    private TextView sliceText;
	private ImageButton switchButton;
    private enum states {DRAG, DRAW}
    private states currentState = states.DRAG;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        
        app = (DiamsApplication) getApplication();
        Examen ex = app.getCurrentExamen();
        Slice currentSlice = ex.getSlice(app.getCurrentSliceIndex());
        Map<String, String> infos = currentSlice.getInfos();
        
        int windowCenter = Integer.parseInt(infos.get("Window center"));
        int windowWidth = Integer.parseInt(infos.get("Window width"));
        
        imageView = (DiamsImageView) findViewById(R.id.imageView);
        imageView.setSlice(currentSlice);
        
        drawView = (DrawView) findViewById(R.id.drawView);

        centerText = (TextView) findViewById(R.id.centerSliderText);
        widthText = (TextView) findViewById(R.id.widthSliderText);
        centerSlider = (SeekBar) findViewById(R.id.centerSlider);
        widthSlider = (SeekBar) findViewById(R.id.widthSlider);
        switchButton = (ImageButton) findViewById(R.id.modeButton);
        zoomSlider = (SeekBar) findViewById(R.id.zoomBar);
        
        
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
        
        switchButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				switchMode(v);
				changeButtonImage();
			}
		});
        
        zoomSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
            
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }
            
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                imageView.updateScale(progress/100f);
            }
        });
        
        zoomSlider.setProgress(100);
        centerSlider.setProgress(windowCenter);
        widthSlider.setProgress(windowWidth);
        
        sliceInc = (Button) findViewById(R.id.sliceInc);
        sliceDec = (Button) findViewById(R.id.sliceDec);
        sliceText = (TextView) findViewById(R.id.sliceText);
        sliceText.setText("Slice "+(app.getCurrentSliceIndex()+1)+"/"+app.getCurrentExamen().getNumberOfSlices());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.show_infos:
            showInfos();
            return true;
        default :
            return super.onOptionsItemSelected(item);    
        }
    }

    public void showInfos() {
        Intent intent = new Intent(getApplicationContext(), InfoDisplayActivity.class);
        startActivity(intent);
    }
    
    public void changeWindowCenter(int newCenter) {
        imageView.setWindowCenter(newCenter);
        builder.delete(0, builder.length());
        builder.append("Center : ");
        builder.append(newCenter);
        centerText.setText(builder, TextView.BufferType.EDITABLE);
        imageView.invalidate();
    }
    
    public void changeWindowWidth(int newWidth) {
        imageView.setWindowWidth(newWidth);
        builder.delete(0, builder.length());
        builder.append("Width : ");
        builder.append(newWidth);
        widthText.setText(builder, TextView.BufferType.EDITABLE);
        imageView.invalidate();
    }
    
    public void decrementSlice(View v) {
        app.setCurrentSliceIndex(app.getCurrentSliceIndex()-1);
        if (app.getCurrentSliceIndex()<=0) sliceDec.setEnabled(false);
        sliceInc.setEnabled(true);
        sliceChanged();
    }
    
    public void incrementSlice(View v) {
        app.setCurrentSliceIndex(app.getCurrentSliceIndex()+1);
        if (app.getCurrentSliceIndex()>=app.getCurrentExamen().getNumberOfSlices()-1)
            sliceInc.setEnabled(false);
        sliceDec.setEnabled(true);
        sliceChanged();
    }
    
   public void switchMode(View v) {
	   if (currentState == states.DRAG) {
		   currentState = states.DRAW;
	   } else {
		   currentState = states.DRAG;
	   }
   }
   
   private void changeButtonImage() {
	   if (currentState == states.DRAG){
		   switchButton.setImageResource(R.drawable.ic_menu_move);
		   drawView.setVisibility(View.GONE);
	   } else {
		   switchButton.setImageResource(R.drawable.ic_menu_draw);
		   drawView.setVisibility(View.VISIBLE);
	   }
   }
    
    public void sliceChanged() {
        builder.delete(0, builder.length());
        builder.append("Slice ");
        builder.append(app.getCurrentSliceIndex()+1);
        builder.append("/");
        builder.append(app.getCurrentExamen().getNumberOfSlices());
        sliceText.setText(builder);
        imageView.setSlice(app.getCurrentExamen().getSlice(app.getCurrentSliceIndex()));
    }
    
}
