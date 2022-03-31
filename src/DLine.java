import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class DLine extends DShape {
	public DLine() {
		super();
	}

	@Override
	public void draw(Graphics g) {
		this.g = g;
		Graphics2D g2d = (Graphics2D) g;
		Line2D.Double line2d = new Line2D.Double(getX(), getY(), getWidth(), getHeight());
		g2d.setColor(getColor());
		g2d.draw(line2d);
	}

	@Override
	public void drawKnobs() {
		if (knobs == null) {
			knobs = new Point[2];
		}
		if (getX() < getWidth() || getY() < getWidth()) {
			knobs[0] = new Point(getX(), getY());
			knobs[1] = new Point(getWidth(), getHeight());
		} else if (getX() > getWidth() || getY() > getWidth()) {
			knobs[0] = new Point(getX(), getY());
			knobs[1] = new Point(getWidth(), getHeight());
		} else {
			knobs[1] = new Point(getX(), getY());
			knobs[2] = new Point(getWidth(), getHeight());
		}
		Graphics2D tl = (Graphics2D) g;
		Rectangle2D.Double rectTL = new Rectangle2D.Double(knobs[0].getX() - 4, knobs[0].getY() - 4, KNOB_SIZE,
				KNOB_SIZE);
		tl.setColor(Color.black);
		tl.fill(rectTL);
		tl.draw(rectTL);
		Graphics2D br = (Graphics2D) g;
		Rectangle2D.Double rectBR = new Rectangle2D.Double(knobs[1].getX() - 4, knobs[1].getY() - 4, KNOB_SIZE,
				KNOB_SIZE);
		br.setColor(Color.black);
		br.fill(rectBR);
		br.draw(rectBR);
	}
}
