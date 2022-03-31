import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class DShape implements ModelListener {
	protected DShapeModel model;
	protected Graphics g;
	protected final int KNOB_SIZE = 9;
	protected Point[] knobs;

	public DShape() {

	}

	public void setModel(DShapeModel model) {
		this.model = model;
	}

	public DShapeModel getModel() {
		return model;
	}

	public void draw(Graphics g) {
	}

	public Rectangle getBounds() {
		return model.getBounds();
	}

	public int getX() {
		return model.getX();
	}

	public int getY() {
		return model.getY();
	}

	public int getWidth() {
		return model.getWidth();
	}

	public int getHeight() {
		return model.getHeight();
	}

	public Color getColor() {
		return model.getColor();
	}

	public void modelChanged(DShapeModel model) {
		this.draw(g);
	}

	public Point[] getKnobs() {
		return knobs;
	}

	public void drawKnobs() {
		if (knobs == null) {
			knobs = new Point[4];
		}
		knobs[0] = new Point(getX(), getY());
		knobs[1] = new Point(getX() + getWidth(), getY());
		knobs[2] = new Point(getX(), getY() + getHeight());
		knobs[3] = new Point(getX() + getWidth(), getY() + getHeight());
		Graphics2D tl = (Graphics2D) g;
		Rectangle2D.Double rectTL = new Rectangle2D.Double(knobs[0].getX() - 4, knobs[0].getY() - 4, KNOB_SIZE,
				KNOB_SIZE);
		tl.setColor(Color.black);
		tl.fill(rectTL);
		tl.draw(rectTL);
		Graphics2D tr = (Graphics2D) g;
		Rectangle2D.Double rectTR = new Rectangle2D.Double(knobs[1].getX() - 4, knobs[1].getY() - 5, KNOB_SIZE,
				KNOB_SIZE);
		tr.setColor(Color.black);
		tr.fill(rectTR);
		tr.draw(rectTR);
		Graphics2D bl = (Graphics2D) g;
		Rectangle2D.Double rectBL = new Rectangle2D.Double(knobs[2].getX() - 4, knobs[2].getY() - 4, KNOB_SIZE,
				KNOB_SIZE);
		bl.setColor(Color.black);
		bl.fill(rectBL);
		bl.draw(rectBL);
		Graphics2D br = (Graphics2D) g;
		Rectangle2D.Double rectBR = new Rectangle2D.Double(knobs[3].getX() - 4, knobs[3].getY() - 4, KNOB_SIZE,
				KNOB_SIZE);
		br.setColor(Color.black);
		br.fill(rectBR);
		br.draw(rectBR);
	}
}
