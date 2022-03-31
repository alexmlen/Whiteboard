import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class DText extends DShape {
	public DText() {
		super();
	}

	@Override
	public void draw(Graphics g) {
		this.g = g;
		Graphics2D g2d = (Graphics2D) g;
		Rectangle2D.Double rect2d = new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
		DTextModel textModel = (DTextModel) model;
		Font font = new Font(textModel.getFont(), Font.PLAIN, getHeight());
		g2d.setFont(font);
		g2d.setColor(getColor());
		String text = textModel.getText();

		Shape clip = g2d.getClip();
		g2d.setClip(clip.getBounds().createIntersection(getBounds()));
		int offset = (getHeight() / 4) * 3;
		g2d.drawString(text, getX(), getY() + offset);

		g.setClip(clip);
	}
}
