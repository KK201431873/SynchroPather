package synchropather.toolkit.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

import javax.swing.JComponent;

import synchropather.DriveConstants;
import synchropather.systems.MovementType;
import synchropather.systems.__util__.Synchronizer;
import synchropather.systems.translation.TranslationState;

@SuppressWarnings("serial")
public class MovementSequenceImage extends JComponent {
	
	private Synchronizer synchronizer;
	private double elapsedTime;
	private static double[] WORLD_ORIGIN = CanvasConstants.WORLD_ORIGIN;
	private static double PIXEL_PER_INCH = CanvasConstants.PIXEL_PER_INCH;
	
	public MovementSequenceImage(Synchronizer synchronizer) {
		this.synchronizer = synchronizer;
	}
	
	public void setMovementSequence(Synchronizer synchronizer) {
		this.synchronizer = synchronizer;
	}
	
	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
		  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));

		TranslationState prev = (TranslationState) synchronizer.getState(MovementType.TRANSLATION, 0);
		ArrayList<Integer> xPoly = new ArrayList<>(), yPoly = new ArrayList<>();
		xPoly.add((int)(prev.getX() * PIXEL_PER_INCH + WORLD_ORIGIN[0]));
		yPoly.add((int)(-prev.getY() * PIXEL_PER_INCH + WORLD_ORIGIN[1]));

		double duration = synchronizer.getDuration();
		double dt = DriveConstants.delta_t;
		double prevTheta = 0;
		for (double elapsedTime = dt; elapsedTime < duration; elapsedTime += dt) {
			TranslationState current = (TranslationState) synchronizer.getState(MovementType.TRANSLATION, elapsedTime);
			double currentTheta = 0;
			if (elapsedTime!=dt) {
				currentTheta = current.minus(prev).theta();
			}
			if (!(current.equals(prev) || elapsedTime==dt || Math.abs(currentTheta-prevTheta) < 1e-2)) {
				xPoly.add((int)(current.getX() * PIXEL_PER_INCH + WORLD_ORIGIN[0]));
				yPoly.add((int)(-current.getY() * PIXEL_PER_INCH + WORLD_ORIGIN[1]));
			}
			prev = current;
			prevTheta = currentTheta;
		}

		System.out.println(xPoly.size());

		// draw polygon
		Polygon poly = new Polygon(xPoly.stream().mapToInt(i -> i).toArray(), yPoly.stream().mapToInt(i -> i).toArray(), xPoly.size());
		g2.setStroke(new BasicStroke(12));
		g2.setColor(new Color(34, 187, 14, 8));
		g2.drawPolygon(poly);
		g2.setStroke(new BasicStroke(5));
		g2.setColor(new Color(34, 187, 14, 32));
		g2.drawPolygon(poly);

		/*

		g2.setStroke(new BasicStroke(12));
		g2.setColor(new Color(34, 187, 14, 100));
		g2.drawPolygon(poly);
		g2.setStroke(new BasicStroke(5));
		g2.setColor(new Color(34, 187, 14, 200));
		g2.drawPolygon(poly);
		 */

		
    }
    
}
