import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;

public class Canvas extends JPanel {
	private ArrayList<DShape> shapes;
	private Whiteboard board;
	private DShape selectedShape;
	private int differenceX;
	private int differenceY;
	private int differenceXLine;
	private int differenceYLine;
	private Point anchorKnob;
	private Boolean lineKnob;
	private Boolean connected;

	public Canvas(Whiteboard board) {
		super();
		connected = false;
		super.paintComponents(this.getGraphics());
		this.board = board;
		this.setPreferredSize(new Dimension(400, 400));
		shapes = new ArrayList<DShape>();
		selectedShape = null;
		this.setBackground(Color.WHITE);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				findKnob(e);
				if (anchorKnob == null) {
					detectShape(e);
				} else {
					if (selectedShape instanceof DLine) {
						double x = anchorKnob.getX();
						double y = anchorKnob.getY();
						DShapeModel model = selectedShape.getModel();
						if (x == model.getX() && y == model.getY()) {
							lineKnob = false;
						} else {
							lineKnob = true;
						}
						selectedShape.drawKnobs();
						board.repaint();
					}
				}
				board.repaint();
				if (selectedShape == null) {
					board.isText(false);
					return;
				}
				if (selectedShape instanceof DText) {
					DTextModel model = (DTextModel) selectedShape.getModel();
					board.inspectText(model.getText(), model.getFont());
					board.isText(true);
				} else {
					board.isText(false);
				}
				if (anchorKnob == null) {
					DShapeModel model = selectedShape.getModel();
					differenceX = e.getX() - model.getX();
					differenceY = e.getY() - model.getY();
					differenceXLine = e.getX() - model.getWidth();
					differenceYLine = e.getY() - model.getHeight();
				}
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (selectedShape == null || connected) {
					return;
				}
				if (anchorKnob == null) {
					int x = e.getX() - differenceX;
					int y = e.getY() - differenceY;
					DShapeModel model = selectedShape.getModel();
					model.setX(x);
					model.setY(y);
					if (selectedShape instanceof DLine) {
						int xLine = e.getX() - differenceXLine;
						int yLine = e.getY() - differenceYLine;
						model.setWidth(xLine);
						model.setHeight(yLine);
					}
					selectedShape.drawKnobs();
					board.repaint();
				} else if (selectedShape instanceof DLine) {
					DShapeModel model = selectedShape.getModel();
					if (lineKnob) {
						model.setX(e.getX());
						model.setY(e.getY());
					} else {
						model.setWidth(e.getX());
						model.setHeight(e.getY());
					}
					selectedShape.drawKnobs();
					board.repaint();
				} else {
					int x = (int) (e.getX() - anchorKnob.getX());
					int y = (int) (e.getY() - anchorKnob.getY());
					DShapeModel model = selectedShape.getModel();
					if (x >= 0 && y >= 0) {
						model.setX((int) anchorKnob.getX());
						model.setY((int) anchorKnob.getY());
						model.setWidth((int) (e.getX() - anchorKnob.getX()));
						model.setHeight((int) (e.getY() - anchorKnob.getY()));
					} else if (x <= 0 && y >= 0) {
						model.setX(e.getX());
						model.setY((int) anchorKnob.getY());
						model.setWidth((int) (anchorKnob.getX() - e.getX()));
						model.setHeight((int) (e.getY() - anchorKnob.getY()));
					} else if (x >= 0 && y <= 0) {
						model.setY(e.getY());
						model.setX((int) anchorKnob.getX());
						model.setWidth((int) (e.getX() - anchorKnob.getX()));
						model.setHeight((int) (anchorKnob.getY() - e.getY()));
					} else if (x <= 0 && y <= 0) {
						model.setX(e.getX());
						model.setY(e.getY());
						model.setWidth((int) (anchorKnob.getX() - e.getX()));
						model.setHeight((int) (anchorKnob.getY() - e.getY()));
					}
					// case 4 top left
					selectedShape.drawKnobs();
					board.repaint();
				}
			}
		});
	}

	public void deselect() {
		selectedShape = null;
		board.repaint();
	}

	public void setText(String s) {
		((DTextModel) selectedShape.getModel()).setText(s);
		board.repaint();
	}

	public void setFont(String s) {
		((DTextModel) selectedShape.getModel()).setFont(s);
		board.repaint();
	}

	public void findKnob(MouseEvent e) {
		if (selectedShape == null) {
			return;
		}
		Point[] knobs = selectedShape.getKnobs();
		anchorKnob = null;
		for (int i = 0; i < knobs.length; i++) {
			int x = e.getX();
			int y = e.getY();
			if (knobs[i].getX() - 5 <= x && x <= knobs[i].getX() + 5 && knobs[i].getY() - 5 <= y
					&& y <= knobs[i].getY() + 5) {
				anchorKnob = new Point();
				if (selectedShape instanceof DLine) {
					if (i == 0) {
						anchorKnob = new Point((int) knobs[1].getX(), (int) knobs[1].getY());
					} else if (i == 1) {
						anchorKnob = new Point((int) knobs[0].getX(), (int) knobs[0].getY());
					}
				} else {
					if (i == 0) {
						anchorKnob = new Point((int) knobs[3].getX(), (int) knobs[3].getY());
					} else if (i == 1) {
						anchorKnob = new Point((int) knobs[2].getX(), (int) knobs[2].getY());
					} else if (i == 2) {
						anchorKnob = new Point((int) knobs[1].getX(), (int) knobs[1].getY());
					} else if (i == 3) {
						anchorKnob = new Point((int) knobs[0].getX(), (int) knobs[0].getY());
					}
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < shapes.size(); i++) {
			shapes.get(i).draw(g);
			if (shapes.get(i) == selectedShape) {
				shapes.get(i).drawKnobs();
			}
		}
	}

	public void clearCanvas() {
		if (shapes.size() != 0) {
			selectedShape = null;
			anchorKnob = null;
			lineKnob = null;
			for (DShape s : shapes) {
				DShapeModel model = s.getModel();
				model.clearListeners();
			}
			shapes.clear();
		}
	}

	public DShapeModel removeShape() {
		if (shapes.size() == 0 || selectedShape == null) {
			return null;
		}
		DShapeModel model = selectedShape.getModel();
		selectedShape.getModel().clearListeners();
		shapes.remove(selectedShape);
		selectedShape = null;
		return model;
	}

	public DShape getSelected() {
		return selectedShape;
	}

	private void detectShape(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (shapes.isEmpty()) {
			return;
		}
		DShape selection = null;
		for (DShape s : shapes) {
			Rectangle bounds = s.getBounds();
			double left = bounds.getX();
			double top = bounds.getY();
			double right;
			double bottom;
			if (s instanceof DLine) {
				double x2 = bounds.getWidth();
				double y2 = bounds.getHeight();
				if (left > x2) {
					right = left;
					left = x2;
				} else {
					right = x2;
				}
				if (top > y2) {
					bottom = top;
					top = y2;
				} else {
					bottom = y2;
				}
			} else {
				right = left + bounds.getWidth();
				bottom = top + bounds.getHeight();
			}
			if (left <= x && x <= right && top <= y && y <= bottom) {
				selection = s;
			}
		}
		selectedShape = selection;
	}

	public DShapeModel sendFront() {
		if (selectedShape == null) {
			return null;
		}
		shapes.remove(selectedShape);
		shapes.add(selectedShape);
		board.repaint();
		return selectedShape.getModel();
	}

	public DShapeModel sendBack() {
		if (selectedShape == null) {
			return null;
		}
		shapes.remove(selectedShape);
		shapes.add(0, selectedShape);
		board.repaint();
		return selectedShape.getModel();
	}

	public int getPositionFromModel(Object v) {
		DShapeModel model = (DShapeModel) v;
		int pos = 0;
		for (int i = 0; i < shapes.size(); i++) {
			DShape s = shapes.get(i);
			if (s.getModel() == model) {
				return i;
			}
		}
		return pos;
	}

	public DShapeModel[] getModelArray() {
		DShapeModel[] models = new DShapeModel[shapes.size()];
		for (int i = 0; i < shapes.size(); i++) {
			models[i] = shapes.get(i).getModel();
		}
		return models;
	}

	public void addShape(DShapeModel model) {
		if (model instanceof DRectModel) {
			DRect shape = new DRect();
			shape.setModel(model);
			shapes.add(shape);
			model.addListener(shape);
		} else if (model instanceof DOvalModel) {
			DOval shape = new DOval();
			shape.setModel(model);
			shapes.add(shape);
			model.addListener(shape);
		} else if (model instanceof DLineModel) {
			DLine shape = new DLine();
			shape.setModel(model);
			shapes.add(shape);
			model.addListener(shape);
		} else if (model instanceof DTextModel) {
			DText shape = new DText();
			shape.setModel(model);
			shapes.add(shape);
			model.addListener(shape);
		}
	}

	public DShape getShapeByID(int id) {
		for (DShape s : shapes) {
			DShapeModel model = s.getModel();
			if (model.getID() == id) {
				return s;
			}
		}
		return null;
	}

	public void removeServer(DShapeModel model) {
		DShape shape = getShapeByID(model.getID());
		if (shape == selectedShape) {
			removeShape();
		} else {
			shape.getModel().removeListener(shape);
			shapes.remove(shape);
		}
		board.repaint();
	}

	public void frontServer(DShapeModel model) {
		DShape shape = getShapeByID(model.getID());
		if (shape == selectedShape) {
			sendFront();
		} else {
			shapes.remove(shape);
			shapes.add(shape);
		}
		board.repaint();
	}

	public void backServer(DShapeModel model) {
		DShape shape = getShapeByID(model.getID());
		if (shape == selectedShape) {
			sendFront();
		} else {
			shapes.remove(shape);
			shapes.add(0, shape);
		}
		board.repaint();
	}

	public void mimic(DShapeModel model) {
		DShape shape = getShapeByID(model.getID());
		DShapeModel data = shape.getModel();
		data.setBounds(model.getBounds());
		data.setColor(model.getColor());
		data.setID(model.getID());
		if (model instanceof DTextModel) {
			DTextModel text = (DTextModel) data;
			DTextModel sentText = (DTextModel) model;
			text.setFont(sentText.getFont());
			text.setText(sentText.getText());
		}
		board.repaint();
	}

	public void setConnected() {
		connected = true;
	}
}
