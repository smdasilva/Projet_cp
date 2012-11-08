package org.bdx1.diams;

import org.bdx1.diams.model.Examen;

import android.app.Application;

public class DiamsApplication extends Application {

    private Examen currentExamen;
    
    public Examen getCurrentExamen() {
        return this.currentExamen;
    }
    
    public void setCurrentExamen(Examen newExamen) {
        this.currentExamen = newExamen;
    }
    
}
