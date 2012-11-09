package org.bdx1.diams;

import org.bdx1.diams.model.Examen;

import android.app.Application;

public class DiamsApplication extends Application {

    private Examen currentExamen;
    private int currentSliceIndex = 0;
    
    public Examen getCurrentExamen() {
        return this.currentExamen;
    }
    
    public void setCurrentExamen(Examen newExamen) {
        this.currentExamen = newExamen;
    }
    
    public int getCurrentSliceIndex() {
        return currentSliceIndex;
    }
    
    public void setCurrentSliceIndex(int newSliceIndex) {
        if (newSliceIndex >= 0 && newSliceIndex < currentExamen.getNumberOfSlices())
            currentSliceIndex = newSliceIndex;
    }
}
