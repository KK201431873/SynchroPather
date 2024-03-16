import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
class RobotImage extends JComponent {
	
	public double x, y, heading;
	private static double[] WORLD_ORIGIN = new double[] {384,384};
	private static double PIXEL_PER_INCH = 5.33333;
	private final static double WIDTH = 17 * PIXEL_PER_INCH, HEIGHT = 14.5 * PIXEL_PER_INCH;
	
	public RobotImage() {
		x = 0;
		y = 0;
		heading = 0;
	}
	
	public void setPose(Pose pose) {
		this.x = pose.getX();
		this.y = pose.getY();
		this.heading = pose.getHeading();
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}
		  
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        double renderX = x * PIXEL_PER_INCH;
        double renderY = -y * PIXEL_PER_INCH;
        
        double[][] d = new double[][] {
        	{WIDTH/2*Math.cos(-heading) - HEIGHT/2*Math.sin(-heading),
        	WIDTH/2*Math.sin(-heading) + HEIGHT/2*Math.cos(-heading)},
        	{-WIDTH/2*Math.cos(-heading) - HEIGHT/2*Math.sin(-heading),
        	-WIDTH/2*Math.sin(-heading) + HEIGHT/2*Math.cos(-heading)},
        	{-WIDTH/2*Math.cos(-heading) - -HEIGHT/2*Math.sin(-heading),
        	-WIDTH/2*Math.sin(-heading) + -HEIGHT/2*Math.cos(-heading)},
        	{WIDTH/2*Math.cos(-heading) - -HEIGHT/2*Math.sin(-heading),
        	WIDTH/2*Math.sin(-heading) + -HEIGHT/2*Math.cos(-heading)},
        };
//		System.out.println(heading);
        double[][] corners = new double[][] {
        	{renderX+d[0][0], renderY+d[0][1]},
        	{renderX+d[1][0], renderY+d[1][1]},
        	{renderX+d[2][0], renderY+d[2][1]},
        	{renderX+d[3][0], renderY+d[3][1]},
        	{renderX+WIDTH/2*Math.cos(-heading), renderY+WIDTH/2*Math.sin(-heading)}
		};
		
		// re-center each point
		for (int i = 0; i < corners.length; i++) {
			double rx = corners[i][0];
			double ry = corners[i][1];
			corners[i][0] = rx+WORLD_ORIGIN[0];
			corners[i][1] = ry+WORLD_ORIGIN[1];
		}
		

        Line2D side0 = new Line2D.Double(corners[0][0], corners[0][1], corners[1][0], corners[1][1]);
        Line2D side1 = new Line2D.Double(corners[1][0], corners[1][1], corners[2][0], corners[2][1]);
        Line2D side2 = new Line2D.Double(corners[2][0], corners[2][1], corners[3][0], corners[3][1]);
        Line2D side3 = new Line2D.Double(corners[3][0], corners[3][1], corners[0][0], corners[0][1]);
        Line2D headingLine = new Line2D.Double(renderX+WORLD_ORIGIN[0], renderY+WORLD_ORIGIN[1], corners[4][0], corners[4][1]);

        BufferedImage bg = null;
		try {
			bg = ImageIO.read(new File("src/centerstage_field.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        g2.drawImage(bg, 0, 0, 768, 768, null);

		Path2D.Double path = new Path2D.Double();
		path.moveTo(corners[0][0], corners[0][1]);
		path.lineTo(corners[1][0], corners[1][1]);
		path.lineTo(corners[2][0], corners[2][1]);
		path.lineTo(corners[3][0], corners[3][1]);
		path.lineTo(corners[0][0], corners[0][1]);
		path.closePath();
		g2.setColor(new Color(68, 142, 228, 144));
		g2.fill(path);

		g2.setColor(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(5));
		g2.setColor(new Color(68, 142, 228));
        g2.draw(side0);
        g2.draw(side1);
        g2.draw(side2);
        g2.draw(side3);
		g2.setColor(new Color(8, 82, 168));
        g2.draw(headingLine);
    }

	private double[] rotateBy(double[] xy, double theta) {
    	double x = xy[0], y = xy[1];
    	return new double[] {
            	x*Math.cos(theta) - y*Math.sin(theta), 
            	x*Math.sin(theta) + y*Math.cos(theta)
    	};
    }
    
}
