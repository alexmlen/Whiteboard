import java.awt.*;
import javax.swing.*;

public class Whiteboard extends JFrame {
	Canvas canvas;
	Controls controls;

	public Whiteboard() {
		super("Whiteboard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		canvas = new Canvas(this);
		this.add(canvas, BorderLayout.CENTER);
		controls = new Controls(this);
		this.add(controls, BorderLayout.WEST);
		this.pack();
		setVisible(true);
	}

	public void addShape(DShapeModel model) {
		canvas.addShape(model);
		repaint();
	}

	public DShapeModel[] getModelArray() {
		return canvas.getModelArray();
	}

	public void clearCanvas() {
		canvas.clearCanvas();
	}

	public DShapeModel removeShape() {
		DShapeModel m = canvas.removeShape();
		repaint();
		return m;
	}

	public DShapeModel sendFront() {
		return canvas.sendFront();
	}

	public DShapeModel sendBack() {
		return canvas.sendBack();
	}

	public void isText(Boolean b) {
		controls.isText(b);
	}

	public void setText(String s) {
		canvas.setText(s);
	}

	public void setFont(String s) {
		canvas.setFont(s);
	}

	public int getPositionFromModel(Object v) {
		return canvas.getPositionFromModel(v);
	}

	public void inspectText(String text, String font) {
		controls.inspectText(text, font);
	}

	public DShape getSelected() {
		return canvas.getSelected();
	}

	public void canvasPaint(Graphics g) {
		canvas.paint(g);
	}

	public void deselect() {
		canvas.deselect();
	}

	public Dimension getCanvasSize() {
		return canvas.getSize();
	}

	public static void main(String[] args) {
		Whiteboard screen = new Whiteboard();
		screen.setVisible(true);
		Whiteboard screen2 = new Whiteboard();
		screen2.setVisible(true);
	}

	public DShape getShapeByID(int id) {
		return canvas.getShapeByID(id);
	}

	public void removeServer(DShapeModel model) {
		canvas.removeServer(model);
	}

	public void frontServer(DShapeModel model) {
		canvas.frontServer(model);
	}

	public void backServer(DShapeModel model) {
		canvas.backServer(model);
	}

	public void mimic(DShapeModel model) {
		canvas.mimic(model);
	}

	public void setConnected() {
		canvas.setConnected();
	}

}