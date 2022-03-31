import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Controls extends JPanel implements ModelListener {
	private Whiteboard board;
	private JTextField textField;
	private JComboBox<String> textBox;
	private HashMap<DShapeModel, Object[]> data;
	private Object[][] dataValues;
	private WhiteboardTable dataTable;
	private JTable jtable;
	private JFileChooser fc;
	private int idTotal;
	private Boolean isClient;
	private Boolean isConnectedAsServer;
	private ServerAccepter serverAccepter;
	private java.util.List<ObjectOutputStream> outputs = new ArrayList<ObjectOutputStream>();
	private ClientHandler clientHandler;
	private Controls theControls;

	public void isText(Boolean b) {
		if (!isClient) {
			textField.setEditable(b);
			textBox.setEnabled(b);
		}
	}

	public Controls(Whiteboard board) {
		theControls = this;
		theControls.idTotal = 0;
		isClient = false;
		isConnectedAsServer = false;
		theControls.board = board;
		theControls.setPreferredSize(new Dimension(400, 400));
		theControls.setBackground(new Color(240, 240, 240));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Box dataBox = Box.createHorizontalBox();
		Object rowData[][] = {};
		dataValues = rowData;
		Object columnNames[] = { "X", "Y", "Width", "Height" };
		WhiteboardTable dtm = new WhiteboardTable(rowData, columnNames);
		dataTable = dtm;
		JTable table = new JTable(dtm);
		jtable = table;
		JScrollPane pane = new JScrollPane(table);
		pane.getViewport().setBackground(Color.WHITE);
		data = new HashMap<DShapeModel, Object[]>();
		dataBox.add(pane);
		for (Component c : dataBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		Box shapeBox = Box.createHorizontalBox();
		JLabel infoShape = new JLabel("Add Shapes: ");
		shapeBox.add(infoShape, BorderLayout.WEST);
		JButton rect = new JButton("Rectangle");
		rect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DRectModel model = new DRectModel(10, 10, 20, 20, Color.GRAY, idTotal);
				idTotal++;
				board.addShape(model);
				Object[] vals = { 10, 10, 20, 20, model };
				data.put(model, vals);
				model.addListener(theControls);
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("add", model);
				}
			}
		});
		shapeBox.add(rect);
		JButton oval = new JButton("Oval");
		oval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DOvalModel model = new DOvalModel(10, 10, 20, 20, Color.GRAY, idTotal);
				idTotal++;
				board.addShape(model);
				Object[] vals = { 10, 10, 20, 20, model };
				data.put(model, vals);
				model.addListener(theControls);
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("add", model);
				}
			}
		});
		shapeBox.add(oval);

		JButton line = new JButton("Line");
		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DLineModel model = new DLineModel(10, 10, 20, 20, Color.GRAY, idTotal);
				idTotal++;
				board.addShape(model);
				Object[] vals = { 10, 10, 20, 20, model };
				data.put(model, vals);
				model.addListener(theControls);
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("add", model);
				}
			}
		});
		shapeBox.add(line);

		JButton text = new JButton("Text");
		text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DTextModel model = new DTextModel(10, 10, 50, 20, Color.GRAY, idTotal);
				idTotal++;
				board.addShape(model);
				Object[] vals = { 10, 10, 50, 20, model };
				data.put(model, vals);
				model.addListener(theControls);
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("add", model);
				}
			}
		});
		shapeBox.add(text);

		for (Component c : shapeBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		Box textBox = Box.createHorizontalBox();
		JLabel infoText = new JLabel("Edit Text: ");
		textBox.add(infoText);
		JTextField textField = new JTextField(15);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText();
				if (text != null && textField.isEditable()) {
					board.setText(text);
				}
			}
		});
		textField.setEditable(false);
		this.textField = textField;
		textBox.add(textField);
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		JComboBox<String> fontList = new JComboBox<>(fonts);
		fontList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String font = (String) fontList.getSelectedItem();
				board.setFont(font);
			}
		});
		fontList.setEnabled(false);
		this.textBox = fontList;
		textBox.add(fontList);

		for (Component c : textBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		textBox.setMaximumSize(new Dimension(400, 50));
		Box colorBox = Box.createHorizontalBox();
		JLabel infoContent = new JLabel("Set Shape Color: ");
		colorBox.add(infoContent);
		JButton color = new JButton("Set Color");
		color.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShape shape = board.getSelected();
				if (shape != null) {
					DShapeModel model = shape.getModel();
					Color initialColor = model.getColor();
					Color newColor = JColorChooser.showDialog(null, "JColorChooser Sample!", initialColor);
					if (newColor != null) {
						model.setColor(newColor);
						board.repaint();
					}
				}
			}
		});
		colorBox.add(color);

		Box editBox = Box.createHorizontalBox();
		JLabel infoEdit = new JLabel("Edit Shapes: ");
		editBox.add(infoEdit);
		JButton front = new JButton("Send to Front");
		front.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel model = board.sendFront();
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("front", model);
				}
			}
		});
		editBox.add(front);
		JButton back = new JButton("Send to Back");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel model = board.sendBack();
				updateTable();
				if (isConnectedAsServer) {
					sendRemote("back", model);
				}
			}
		});
		editBox.add(back);

		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DShapeModel b = board.removeShape();
				if (b != null) {
					data.remove(b);
					b.removeListener(this);
					updateTable();
					if (isConnectedAsServer) {
						sendRemote("remove", b);
					}
				}
			}
		});
		editBox.add(remove);
		for (Component c : editBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		Box fileBox = Box.createHorizontalBox();
		fc = new JFileChooser();

		JLabel infoFile = new JLabel("Save/Load Content: ");
		fileBox.add(infoFile);
		JButton save = new JButton("Save");

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileOutputStream fos;
				try {
					int returnVal = fc.showSaveDialog(board);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						if (file == null) {
							file = new File("Whiteboard.xml");
						}
						String name = file.getName();
						if (file.getName().length() < 5) {
							file = new File(file.getPath() + ".xml");
						} else if (!name.substring(name.length() - 4, name.length()).equals(".xml")) {
							file = new File(file.getPath() + ".xml");
						}
						fos = new FileOutputStream(file);
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						XMLEncoder xmlEncoder = new XMLEncoder(bos);
						xmlEncoder.writeObject(board.getModelArray());
						xmlEncoder.close();
						bos.close();
						fos.close();
					} else {
						System.out.println("Save file cancelled.");
					}
				} catch (FileNotFoundException error) {
					error.printStackTrace();
				} catch (Error error) {
					error.printStackTrace();
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		});
		fileBox.add(save);
		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileInputStream fos;
				try {
					int returnVal = fc.showOpenDialog(board);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						if (file == null) {
							return;
						}
						String name = file.getName();
						if (file.getName().length() < 5
								|| !name.substring(name.length() - 4, name.length()).equals(".xml")) {
							System.out.println("Not a valid file type");
							return;
						}
						fos = new FileInputStream(file);
						BufferedInputStream bos = new BufferedInputStream(fos);
						XMLDecoder xmlDecoder = new XMLDecoder(bos);
						DShapeModel[] models = (DShapeModel[]) xmlDecoder.readObject();
						board.clearCanvas();
						data.clear();
						for (DShapeModel m : models) {
							board.addShape(m);
							Object[] vals = { m.getX(), m.getY(), m.getWidth(), m.getHeight(), m };
							data.put(m, vals);
							m.addListener(theControls);
						}
						xmlDecoder.close();
						bos.close();
						fos.close();
					} else {
						System.out.println("Open file cancelled.");
					}
				} catch (FileNotFoundException error) {
					error.printStackTrace();
				} catch (Error error) {
					error.printStackTrace();
				} catch (IOException error) {
					error.printStackTrace();
				}
				updateTable();
			}
		});
		fileBox.add(load);
		JButton png = new JButton("Save as PNG");
		png.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int returnVal = fc.showSaveDialog(board);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						if (file == null) {
							file = new File("Whiteboard.png");
						}
						String name = file.getName();
						if (file.getName().length() < 5) {
							file = new File(file.getPath() + ".png");
						} else if (!name.substring(name.length() - 4, name.length()).equals(".png")) {
							file = new File(file.getPath() + ".png");
						}
						board.deselect();
						Dimension canvas = board.getCanvasSize();
						BufferedImage bi = new BufferedImage((int) canvas.getWidth(), (int) canvas.getHeight(),
								BufferedImage.TYPE_INT_ARGB);
						Graphics g = bi.createGraphics();
						board.canvasPaint(g);
						g.dispose();
						ImageIO.write(bi, "png", file);
					} else {
						System.out.println("Save file cancelled.");
					}
				} catch (FileNotFoundException error) {
					error.printStackTrace();
				} catch (Error error) {
					error.printStackTrace();
				} catch (IOException error) {
					error.printStackTrace();
				}
			}
		});
		fileBox.add(png);
		for (Component c : fileBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		Box networkBox = Box.createHorizontalBox();
		JLabel infoPort = new JLabel("");
		JLabel infoNetwork = new JLabel("Network: ");
		networkBox.add(infoNetwork);
		JButton server = new JButton("Start Server");
		JButton client = new JButton("Start Client");
		server.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(board,
						"Enter port number you want to use (default is 39587)");
				if (input != null) {
					if (input.equals("")) {
						input = "39587";
					}
					try {
						int port = Integer.parseInt(input);
						infoPort.setText("Server mode, port " + port);
						server.setEnabled(false);
						client.setEnabled(false);
						startServer(port);
					} catch (NumberFormatException error) {
						return;
					}
				}
			}
		});
		networkBox.add(server);
		client.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(board,
						"Enter port number you want to use (default is 39587)");
				if (input != null) {
					if (input.equals("")) {
						input = "39587";
					}
					try {
						int port = Integer.parseInt(input);
						infoPort.setText("Client mode, port " + port);
						infoShape.setEnabled(false);
						rect.setEnabled(false);
						oval.setEnabled(false);
						line.setEnabled(false);
						text.setEnabled(false);
						infoText.setEnabled(false);
						textField.setEditable(false);
						fontList.setEnabled(false);
						infoContent.setEnabled(false);
						color.setEnabled(false);
						remove.setEnabled(false);
						infoEdit.setEnabled(false);
						front.setEnabled(false);
						back.setEnabled(false);
						infoFile.setEnabled(false);
						save.setEnabled(false);
						load.setEnabled(false);
						png.setEnabled(false);
						infoNetwork.setEnabled(false);
						server.setEnabled(false);
						client.setEnabled(false);
						isClient = true;
						startClient(port);
					} catch (NumberFormatException error) {
						return;
					}
				}
			}
		});
		networkBox.add(client);
		networkBox.add(infoPort);
		for (Component c : networkBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		Box bigBox = Box.createVerticalBox();
		bigBox.add(shapeBox);
		bigBox.add(textBox);
		bigBox.add(colorBox);
		bigBox.add(editBox);
		bigBox.add(fileBox);
		bigBox.add(networkBox);
		bigBox.add(dataBox);
		add(bigBox);

		for (Component c : bigBox.getComponents()) {
			((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
	}

	public synchronized void addOutput(ObjectOutputStream out) {
		outputs.add(out);
	}

	class ServerAccepter extends Thread {
		private int port;

		ServerAccepter(int port) {
			this.port = port;
		}

		public void run() {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
				while (true) {
					Socket toClient = null;
					toClient = serverSocket.accept();
					isConnectedAsServer = true;
					addOutput(new ObjectOutputStream(toClient.getOutputStream()));
					for (int i = 0; i < idTotal; i++) {
						DShape shape = board.getShapeByID(i);
						sendRemote("add", shape.getModel());
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void startServer(int port) {
		serverAccepter = new ServerAccepter(port);
		serverAccepter.start();

	}

	public void sendLocal(String message, DShapeModel model) {
		if (isConnectedAsServer) {
			return;
		}
		if (message.equals("add")) {
			board.addShape(model);
			Object[] info = { model.getX(), model.getY(), model.getWidth(), model.getHeight(), model };
			data.put(model, info);
			model.addListener(theControls);
		} else if (message.equals("remove")) {
			DShape shape = board.getShapeByID(model.getID());
			DShapeModel actualModel = shape.getModel();
			actualModel.removeListener(this);
			data.remove(actualModel);
			board.removeServer(model);
		} else if (message.equals("front")) {
			board.frontServer(model);
		} else if (message.equals("back")) {
			board.backServer(model);
		} else if (message.equals("changed")) {
			board.mimic(model);
		}
		updateTable();
	}

	public void invokeToGUI(String message, DShapeModel model) {
		final String temp = message;
		final DShapeModel mod = model;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sendLocal(temp, mod);
			}
		});
	}

	public synchronized void sendRemote(String message, DShapeModel model) {
		OutputStream memStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(memStream);
		encoder.writeObject(model);
		encoder.close();
		String xmlString = memStream.toString();
		Iterator<ObjectOutputStream> it = outputs.iterator();
		while (it.hasNext()) {
			ObjectOutputStream out = it.next();
			try {
				out.writeObject(message);
				out.writeObject(xmlString);
				out.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
				it.remove();
			}
		}
	}

	private class ClientHandler extends Thread {
		private String name;
		private int port;

		ClientHandler(String name, int port) {
			this.name = name;
			this.port = port;
		}

		public void run() {
			Socket toServer = null;
			XMLDecoder decoder = null;
			try {
				toServer = new Socket(name, port);
				board.clearCanvas();
				data.clear();
				updateTable();
				board.repaint();
				ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());
				board.setConnected();

				while (true) {
					String message = (String) in.readObject();
					String xmlString = (String) in.readObject();
					decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
					DShapeModel model = (DShapeModel) decoder.readObject();
					invokeToGUI(message, model);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (decoder != null) {
				decoder.close();
			}
			if (toServer != null) {
				try {
					toServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void startClient(int port) {
		String host = "localhost";
		clientHandler = new ClientHandler(host, port);
		clientHandler.start();
	}

	@Override
	public void modelChanged(DShapeModel model) {
		Object[] info = { model.getX(), model.getY(), model.getWidth(), model.getHeight(), model };
		data.put(model, info);
		updateTable();
		if (isConnectedAsServer) {
			sendRemote("changed", model);
		}
	}

	public void updateTable() {
		dataValues = new Object[data.size()][];
		Object[] newValues = data.values().toArray();
		if (newValues.length != 0) {
			for (int i = 0; i < data.size(); i++) {
				Object[] v = (Object[]) newValues[i];
				int position = board.getPositionFromModel(v[4]);
				dataValues[position] = new Object[4];
				dataValues[position][0] = v[0];
				dataValues[position][1] = v[1];
				dataValues[position][2] = v[2];
				dataValues[position][3] = v[3];
			}
		} else {
			dataValues = new Object[0][];
		}
		Object columnNames[] = { "X", "Y", "Width", "Height" };
		dataTable = new WhiteboardTable(dataValues, columnNames);
		jtable.setModel(dataTable);
	}

	public void inspectText(String text, String font) {
		textField.setText(text);
		textBox.setSelectedItem(font);
	}
}
