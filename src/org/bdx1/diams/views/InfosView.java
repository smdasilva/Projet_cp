package org.bdx1.diams.views;

import java.util.Map;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.Slice;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class InfosView extends TextView {

    private Examen examen;

    public InfosView(Context context) {
        super(context);
    }

    public InfosView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void setExamen(Examen ex, Slice current) {
        this.examen = ex;

        StringBuilder builder = new StringBuilder();
        builder.append("Patient infos :\n");
        Map<String, String> patientInfos = this.examen.getPatientInfos();
        for (String key : patientInfos.keySet()) {
            builder.append('\t');
            builder.append(key);
            builder.append(" : ");
            builder.append(patientInfos.get(key));
            builder.append("\n");
        }
        builder.append("\nStudy infos :\n");
        Map<String, String> studyInfos = this.examen.getStudyInfos();
        for (String key : studyInfos.keySet()) {
            builder.append('\t');
            builder.append(key);
            builder.append(" : ");
            builder.append(studyInfos.get(key));
            builder.append("\n");
        }
        builder.append("\nSlice Infos :\n");
        Map<String, String> sliceInfos = current.getInfos();
        for (String key : sliceInfos.keySet()) {
            builder.append('\t');
            builder.append(key);
            builder.append(" : ");
            builder.append(sliceInfos.get(key));
            builder.append("\n");
        }

        this.setText(builder.toString());
    }

}
