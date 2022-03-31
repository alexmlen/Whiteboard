import java.awt.*;
import java.awt.geom.Rectangle2D;

public class DRect extends DShape {
	public DRect() {
		super();
	}

	@Override
	public void draw(Graphics g) {
		this.g = g;
		Graphics2D g2d = (Graphics2D) g;
		Rectangle2D.Double rect2d = new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
		g2d.setColor(getColor());
		g2d.fill(rect2d);
		g2d.draw(rect2d);
	}
}
