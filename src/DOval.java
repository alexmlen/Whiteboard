import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DOval extends DShape {
	public DOval() {
		super();
	}

	@Override
	public void draw(Graphics g) {
		this.g = g;
		Graphics2D g2d = (Graphics2D) g;
		Ellipse2D.Double circle = new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight());
		g2d.setColor(getColor());
		g2d.fill(circle);
		g2d.draw(circle);
	}
}
