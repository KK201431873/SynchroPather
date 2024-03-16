import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.JComponent;

@SuppressWarnings("serial")
class SplineImage extends JComponent {
	
	public CRSpline spline;
	private static double[] WORLD_ORIGIN = CanvasConstants.WORLD_ORIGIN;
	private static double PIXEL_PER_INCH = CanvasConstants.PIXEL_PER_INCH;
	
	public SplineImage(CRSpline spline) {
		this.spline = spline;
	}
	
	public void setSpline(CRSpline spline) {
		this.spline = spline;
	}
		  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(5));
		
		for (int segment = 0; segment < spline.getLength()-1; segment++) {
			
			int polyLength = (int) Math.ceil(1/DriveConstants.delta_t);
			
			int[] xPoly = new int[polyLength * 2];
			int[] yPoly = new int[polyLength * 2];
			
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
	        	xPoly[2*polyLength-1-j] = xPoly[j];
	        	yPoly[2*polyLength-1-j] = yPoly[j];
	        }
	
			Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);

	        if (Main.spline.getSegment(Main.elapsedTime) == segment) {
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
    
}
