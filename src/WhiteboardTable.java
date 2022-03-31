import javax.swing.table.DefaultTableModel;

public class WhiteboardTable extends DefaultTableModel {

	public WhiteboardTable(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

}