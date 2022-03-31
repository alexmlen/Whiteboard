import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class DShapeModel {
	protected Rectangle bounds;
	protected Color color;
	private ArrayList<Object> listeners;
	private int id;

	public DShapeModel() {
		bounds = new Rectangle(10, 10, 10, 10);
		this.color = Color.gray;
		listeners = new ArrayList<Object>();
		id = 0;
	}

	public DShapeModel(int x, int y, int width, int height, Color color, int id) {
		bounds = new Rectangle(x, y, width, height);
		this.color = color;
		listeners = new ArrayList<Object>();
		this.id = id;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getID() {
		return id;
	}

	public int getX() {
		return (int) bounds.getX();
	}

	public int getY() {
		return (int) bounds.getY();
	}

	public int getWidth() {
		return (int) bounds.getWidth();
	}

	public int getHeight() {
		return (int) bounds.getHeight();
	}

	public Color getColor() {
		return color;
	}

	public void setBounds(Rectangle r) {
		bounds = r;
		changed();
	}

	public void setID(int newID) {
		id = newID;
		changed();
	}

	public void setX(int x) {
		bounds.setLocation(x, getY());
		changed();
	}

	public void setY(int y) {
		bounds.setLocation(getX(), y);
		changed();
	}

	public void setWidth(int width) {
		bounds.setSize(width, getHeight());
		changed();
	}

	public void setHeight(int height) {
		bounds.setSize(getWidth(), height);
		changed();
	}

	public void setColor(Color color) {
		this.color = color;
		changed();
	}

	public void addListener(Object s) {
		listeners.add(s);
	}

	public void removeListener(Object s) {
		listeners.remove(s);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void changed() {
		for (Object s : listeners) {
			if (s instanceof DShape) {
				((DShape) s).modelChanged(this);
			} else if (s instanceof Controls) {
				((Controls) s).modelChanged(this);
			}
		}
	}
}
