package org.bdx1.diams;
import java.lang.reflect.Array;
import java.util.Vector;

public class Bezier {
	
	/* Attibuts de la classe */
	/*************************/
	private int taille;
	int [][] triangle;
	
	/* Constructeur n°1 */
	/********************/
	public void TrianglePascal(int nvTaille){
		if (nvTaille > 0){
			triangle = new int [nvTaille][];
			taille = nvTaille;
		}
		else{
			System.out.println("Parametre incorrect (defaut:5)");
			triangle = new int [5][];
			taille = 5;
		}
	
		for (int i = 0 ; i < taille ; i++){
			triangle[i] = new int[i+1];
		}
		for (int i = 0 ; i < taille ; i++){
		    for (int j = 0 ; j < (i+1) ; j++){
				if ((j == 0)||(i == j))
					triangle[i][j] = 1;
				else
					triangle[i][j] =triangle[i-1][j-1] + triangle[i-1][j];
			}
		}		
	}
	
	public String toString(){
		String affichage = "";
		
		for(int i=0 ; i<triangle.length ; i++){
			for(int j = 0 ; j<triangle[i].length ; j++){
				affichage += triangle[i][j];
			}
			affichage += "\n";
		}
		return affichage;
	}

	public float[][] computeBezierPoints(float [][] controlPoints, int nbSegments) {
	    
		TrianglePascal(controlPoints.length); // Construction du triangle
		float h = (float) (1.0 / nbSegments);
		float [][] curvePoints = new float [nbSegments+1][];
	    for (int i = 0; i < nbSegments+1; ++i) { 
	        float curStep = i * h;
	        curvePoints[i] = computeBezierPoint(controlPoints, curStep);
	    }
	    return curvePoints;
	}


	public float[] computeBezierPoint(float [][] controlPoints, float t) {
	    int nbControlPoints = controlPoints.length;
	    float s = 1 - t;
	    float [] bezierPoint = new float [2];
	    bezierPoint[0] = 0;
	    bezierPoint[1] = 0;

	    for (int i = 0; i < nbControlPoints ; ++i) {
	    	float [] controlPoint = new float [2];
	        controlPoint[0] = controlPoints[i][0];
	        controlPoint[1] = controlPoints[i][1];
	        float coeff = (float) (triangle[nbControlPoints - 1][i] * Math.pow(t, i) * Math.pow(s, nbControlPoints - 1 - i));
	        bezierPoint[0] += controlPoint[0] * coeff;
	        bezierPoint[1] += controlPoint[1] * coeff;
	    }

	    return bezierPoint;
	}
	

}
