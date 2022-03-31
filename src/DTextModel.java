import java.awt.Color;

public class DTextModel extends DShapeModel {
	private String font;
	private String text;

	public DTextModel() {
		super();
		font = "Serif";
		text = "Text";
	}

	public DTextModel(int x, int y, int width, int height, Color color, int id) {
		super(x, y, width, height, color, id);
		font = "Serif";
		text = "Text";
	}

	public String getFont() {
		return font;
	}

	public String getText() {
		return text;
	}

	public void setFont(String s) {
		font = s;
		changed();
	}

	public void setText(String s) {
		text = s;
		changed();
	}
}
