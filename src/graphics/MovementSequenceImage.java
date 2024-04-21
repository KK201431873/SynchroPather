package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ll_constants.DriveConstants;
import ll_movement.MovementSequence;
import ll_movement.movements.CRSpline;
import ll_movement.movements.StraightLine;
import ll_movement.util.Movement;
import ll_movement.util.Pose;

@SuppressWarnings("serial")
public class MovementSequenceImage extends JComponent {
	
	private MovementSequence sequence;
	private double elapsedTime;
	private static double[] WORLD_ORIGIN = CanvasConstants.WORLD_ORIGIN;
	private static double PIXEL_PER_INCH = CanvasConstants.PIXEL_PER_INCH;
	
	public MovementSequenceImage(MovementSequence sequence) {
		this.sequence = sequence;
	}
	
	public void setMovementSequence(MovementSequence sequence) {
		this.sequence = sequence;
	}
	
	public void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
		  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
		
        for (int m_index = 0; m_index < sequence.getLength(); m_index++) {
        	Movement movement = sequence.getMovement(m_index);
        	double localElapsedTime = sequence.getLocalElapsedTime(elapsedTime);
        	boolean isRunning = sequence.getLocalMovementIndex(elapsedTime) == m_index;
        	
        	if (movement instanceof CRSpline) {
        		CRSpline spline = (CRSpline) movement;
				for (int segment = 0; segment < spline.getLength()-1; segment++) {
					
					int polyLength = (int) Math.ceil(1/DriveConstants.delta_t);
					
					int[] xPoly = new int[polyLength*2];
					int[] yPoly = new int[polyLength*2];
					
			        Pose pose = spline.getPose(segment, 0);
			        int i = 0;
			        for (double t = 0; t <= 1; t += DriveConstants.delta_t) {
			
			            double[] coordinatePose = {pose.getX() * PIXEL_PER_INCH, -pose.getY() * PIXEL_PER_INCH};
			
			            xPoly[i] = (int) (coordinatePose[0]+WORLD_ORIGIN[0]);
			            yPoly[i] = (int) (coordinatePose[1]+WORLD_ORIGIN[1]);
			            i++;
			            
			            pose = spline.getPose(segment, t);
			        }
			        
			        for (int j = 0; j < polyLength; j++) {
			        	xPoly[2*polyLength-j-1] = xPoly[j];
			        	yPoly[2*polyLength-j-1] = yPoly[j];
			        }
			
					Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
		
			        if (spline.getLocalSegment(localElapsedTime) == segment && isRunning) {
			            g2.setStroke(new BasicStroke(12));
			        	g2.setColor(new Color(34, 187, 14, 100));
						g2.drawPolygon(poly);
			            g2.setStroke(new BasicStroke(5));
			        	g2.setColor(new Color(34, 187, 14, 200));
						g2.drawPolygon(poly);
			        } else {
			            g2.setStroke(new BasicStroke(12));
			        	g2.setColor(new Color(34, 187, 14, 8));
						g2.drawPolygon(poly);
			            g2.setStroke(new BasicStroke(5));
			        	g2.setColor(new Color(34, 187, 14, 32));
						g2.drawPolygon(poly);
			        }
			        
				}
        	}
        	if (movement instanceof StraightLine) {
        		StraightLine straightLine = (StraightLine) movement;
        		Pose p0 = straightLine.getStartPose();
        		Pose p1 = straightLine.getEndPose();
        		
        		Line2D line = new Line2D.Double(
        				p0.getX()*PIXEL_PER_INCH + WORLD_ORIGIN[0], 
        				-p0.getY()*PIXEL_PER_INCH + WORLD_ORIGIN[1], 
        				p1.getX()*PIXEL_PER_INCH + WORLD_ORIGIN[0], 
        				-p1.getY()*PIXEL_PER_INCH + WORLD_ORIGIN[1]
        		);
		        if (isRunning) {
		            g2.setStroke(new BasicStroke(12));
		        	g2.setColor(new Color(34, 187, 14, 100));
					g2.draw(line);
		            g2.setStroke(new BasicStroke(5));
		        	g2.setColor(new Color(34, 187, 14, 200));
					g2.draw(line);
		        } else {
		            g2.setStroke(new BasicStroke(12));
		        	g2.setColor(new Color(34, 187, 14, 8));
					g2.draw(line);
		            g2.setStroke(new BasicStroke(5));
		        	g2.setColor(new Color(34, 187, 14, 32));
					g2.draw(line);
		        }
		        
        	}
			
        }
		
    }
    
}
