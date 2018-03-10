package me.andimeo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import me.andimeo.FilterCondition.LineType;
import me.andimeo.FilterCondition.PositionType;

public class GUI {

	private JFrame frame;
	private DataParser parser;

	// stocks components
	JList<Stock> batchesList;
	JList<Stock> stocksList;

	// time unit inputs
	JRadioButton dayRadioButton;
	JRadioButton weekRadioButton;
	JRadioButton monthRadioButton;

	// data source inputs
	private JComboBox<String> dataSourceComboBox;

	// date inputs
	private JComboBox<String> yearComboBox;
	private JComboBox<String> monthComboBox;
	private JComboBox<String> dayComboBox;

	// long or short position inputs
	private ButtonGroup longOrShortButtonGroup;
	private JRadioButton longPositionRadioButton;
	private JRadioButton shortPositionRadioButton;
	private final int POSITION_NUM = 4;
	private JTextField longPositionTextFields[];
	private JTextField shortPositionTextFields[];

	// turnover inputs
	private JTextField lowerBoundTextField;
	private JTextField upperBoundTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		parser = new DataParser();

		dataSourceComboBox = new JComboBox<>();

		yearComboBox = new JComboBox<>();
		monthComboBox = new JComboBox<>();
		dayComboBox = new JComboBox<>();

		longPositionTextFields = new JTextField[POSITION_NUM];
		shortPositionTextFields = new JTextField[POSITION_NUM];
		for (int i = 0; i < POSITION_NUM; i++) {
			longPositionTextFields[i] = new JTextField();
			shortPositionTextFields[i] = new JTextField();
		}
		longOrShortButtonGroup = new ButtonGroup();

		lowerBoundTextField = new JTextField();
		upperBoundTextField = new JTextField();

		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(topPanel(), BorderLayout.NORTH);
		frame.getContentPane().add(centerPanel(), BorderLayout.CENTER);

	}

	private JPanel topPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		JButton chooseButton = new JButton("选择");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setDialogTitle("选择数据目录");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		JTextField dirTextField = new JTextField(40);
		dirTextField.setEditable(false);
		chooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						dirTextField.setText(file.getCanonicalPath());
						System.out.println(parser.parse(file));
						JOptionPane.showMessageDialog(frame, "数据加载完毕");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		topPanel.add(dirTextField);
		topPanel.add(chooseButton);
		return topPanel;
	}

	private JPanel centerPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2));
		centerPanel.add(leftPanel());
		centerPanel.add(rightPanel());
		return centerPanel;
	}

	private JPanel leftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftPanel.setLayout(new BorderLayout(10, 0));

		batchesList = new JList<>();
		batchesList.setPreferredSize(new Dimension(50, -1));
		JScrollPane batchesScrollPane = new JScrollPane(batchesList);
		leftPanel.add(batchesScrollPane, BorderLayout.WEST);
		batchesScrollPane.setPreferredSize(new Dimension(50, -1));

		stocksList = new JList<>();
		stocksList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (renderer instanceof JLabel && value instanceof Stock) {
					// Here value will be of the Type 'CD'
					((JLabel) renderer).setText(((Stock) value).getCode());
				}
				return renderer;
			}
		});
		JScrollPane stocksScrollPane = new JScrollPane(stocksList);
		leftPanel.add(stocksScrollPane, BorderLayout.CENTER);

		DefaultListModel<Stock> listModel = new DefaultListModel<>();
		stocksList.setModel(listModel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		dataSourceComboBox.addItem("载入所有数据");
		dataSourceComboBox.addItem("载入沪市数据");
		dataSourceComboBox.addItem("载入深市数据");
		dataSourceComboBox.addItem("载入自定义数据");

		JButton clearButton = new JButton("清空");
		JButton saveButton = new JButton("保存");
		JButton loadButton = new JButton("载入");

		bottomPanel.add(clearButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(dataSourceComboBox);
		bottomPanel.add(loadButton);

		// clear
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
				listModel.removeAllElements();
			}
		});
		// TODO: save

		// load
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
				List<Stock> stocks;
				if (dataSourceComboBox.getSelectedIndex() == 0) {
					stocks = parser.getAllStocks();
				} else if (dataSourceComboBox.getSelectedIndex() == 1) {
					stocks = parser.getStocksFromSpecificMarket("sh");
				} else if (dataSourceComboBox.getSelectedIndex() == 2) {
					stocks = parser.getStocksFromSpecificMarket("sz");
				} else {
					stocks = new ArrayList<>();
				}
				for (Stock stock : stocks) {
					listModel.addElement(stock);
				}
			}
		});

		bottomPanel.add(clearButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(dataSourceComboBox);
		bottomPanel.add(loadButton);
		leftPanel.add(bottomPanel, BorderLayout.SOUTH);
		return leftPanel;
	}

	private JPanel rightPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(5, 0, 10, 10));

		rightPanel.add(timeUnitPanel());
		rightPanel.add(datePanel());
		rightPanel.add(positionPanel());
		rightPanel.add(turnoverPanel());
		rightPanel.add(filterPanel());
		return rightPanel;
	}

	private JPanel timeUnitPanel() {
		JPanel timeUnitPanel = new JPanel();
		timeUnitPanel.setLayout(new GridLayout(1, 3, 0, 0));
		TitledBorder border = new TitledBorder("日/周/月线");
		timeUnitPanel.setBorder(border);

		dayRadioButton = new JRadioButton("日线");
		weekRadioButton = new JRadioButton("周线");
		monthRadioButton = new JRadioButton("月线");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(dayRadioButton);
		buttonGroup.add(weekRadioButton);
		buttonGroup.add(monthRadioButton);
		timeUnitPanel.add(dayRadioButton);
		timeUnitPanel.add(weekRadioButton);
		timeUnitPanel.add(monthRadioButton);
		return timeUnitPanel;
	}

	private JPanel datePanel() {
		JPanel datePanel = new JPanel();
		datePanel.setLayout(new FlowLayout());
		TitledBorder border = new TitledBorder("日期");
		datePanel.setBorder(border);

		datePanel.add(yearComboBox);
		datePanel.add(new JLabel("年"));
		datePanel.add(monthComboBox);
		datePanel.add(new JLabel("月"));
		datePanel.add(dayComboBox);
		datePanel.add(new JLabel("日"));
		return datePanel;
	}

	private JPanel positionPanel() {
		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new GridLayout(2, 1));
		TitledBorder border = new TitledBorder("多/空头");
		positionPanel.setBorder(border);

		positionPanel.add(longPositionPanel());
		positionPanel.add(shortPositionPanel());
		return positionPanel;
	}

	private JPanel longPositionPanel() {
		JPanel longPositionPanel = new JPanel();
		longPositionPanel.setLayout(new FlowLayout());

		longPositionRadioButton = new JRadioButton("多头排列");
		longOrShortButtonGroup.add(longPositionRadioButton);
		longPositionPanel.add(longPositionRadioButton);

		longPositionPanel.add(longPositionTextFields[0]);
		longPositionTextFields[0].setColumns(2);

		JLabel label = new JLabel(">");
		longPositionPanel.add(label);

		longPositionPanel.add(longPositionTextFields[1]);
		longPositionTextFields[1].setColumns(2);

		JLabel label_1 = new JLabel(">");
		longPositionPanel.add(label_1);

		longPositionPanel.add(longPositionTextFields[2]);
		longPositionTextFields[2].setColumns(2);

		JLabel lblNewLabel = new JLabel(">");
		longPositionPanel.add(lblNewLabel);

		longPositionTextFields[3] = new JTextField();
		longPositionPanel.add(longPositionTextFields[3]);
		longPositionTextFields[3].setColumns(2);
		return longPositionPanel;
	}

	private JPanel shortPositionPanel() {
		JPanel shortPositionPanel = new JPanel();
		shortPositionPanel.setLayout(new FlowLayout());

		shortPositionRadioButton = new JRadioButton("空头排列");
		longOrShortButtonGroup.add(shortPositionRadioButton);
		shortPositionPanel.add(shortPositionRadioButton);

		shortPositionTextFields[0].setColumns(2);
		shortPositionPanel.add(shortPositionTextFields[0]);

		JLabel label_2 = new JLabel("<");
		shortPositionPanel.add(label_2);

		shortPositionTextFields[1].setColumns(2);
		shortPositionPanel.add(shortPositionTextFields[1]);

		JLabel label_3 = new JLabel("<");
		shortPositionPanel.add(label_3);

		shortPositionTextFields[2].setColumns(2);
		shortPositionPanel.add(shortPositionTextFields[2]);

		JLabel label_4 = new JLabel("<");
		shortPositionPanel.add(label_4);

		shortPositionTextFields[3] = new JTextField();
		shortPositionTextFields[3].setColumns(2);
		shortPositionPanel.add(shortPositionTextFields[3]);

		return shortPositionPanel;
	}

	private JPanel turnoverPanel() {
		JPanel turnoverPanel = new JPanel();
		turnoverPanel.setLayout(new FlowLayout());

		TitledBorder border = new TitledBorder("成交额");
		turnoverPanel.setBorder(border);

		JLabel lowerBoundLabel = new JLabel("下界");
		turnoverPanel.add(lowerBoundLabel);
		turnoverPanel.add(lowerBoundTextField);
		lowerBoundTextField.setColumns(5);

		JLabel upperBoundLabel = new JLabel("上界");
		turnoverPanel.add(upperBoundLabel);
		turnoverPanel.add(upperBoundTextField);
		upperBoundTextField.setColumns(5);
		return turnoverPanel;
	}

	private JPanel filterPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new FlowLayout());

		JButton saveButton = new JButton("保存");
		JButton loadButton = new JButton("载入");
		JButton filterButton = new JButton("筛选");

		// TODO: save

		// TODO: load

		// TODO: filter
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FilterCondition condition = new FilterCondition();
				if (dayRadioButton.isSelected()) {
					condition.setLineType(LineType.DAY);
				} else if (weekRadioButton.isSelected()) {
					condition.setLineType(LineType.WEEK);
				} else if (monthRadioButton.isSelected()) {
					condition.setLineType(LineType.MONTH);
				} else {
					JOptionPane.showMessageDialog(frame, "请选择 日/周/月线类型");
					return;
				}

				String yearStr = (String) yearComboBox.getSelectedItem();
				condition.setYear(Integer.parseInt(yearStr));
				String monthStr = (String) monthComboBox.getSelectedItem();
				condition.setMonth(Integer.parseInt(monthStr));
				String dayStr = (String) dayComboBox.getSelectedItem();
				condition.setDay(Integer.parseInt(dayStr));

				if (longPositionRadioButton.isSelected()) {
					condition.setPositionType(PositionType.LONG);
					List<Integer> positions = new ArrayList<Integer>();
					for (int i = 0; i < 4; i++) {
						String s = longPositionTextFields[i].getText();
						if (Utils.isInt(s)) {
							positions.add(Integer.parseInt(s));
						}
					}
					condition.setPositions(positions);
				} else if (shortPositionRadioButton.isSelected()) {
					condition.setPositionType(PositionType.SHORT);
					List<Integer> positions = new ArrayList<Integer>();
					for (int i = 0; i < 4; i++) {
						String s = shortPositionTextFields[i].getText();
						if (Utils.isInt(s)) {
							positions.add(Integer.parseInt(s));
						}
					}
					condition.setPositions(positions);
				} else {
					condition.setPositionType(PositionType.NONE);
					condition.setPositions(new ArrayList<Integer>());
				}

				String s = lowerBoundTextField.getText();
				if (Utils.isDouble(s)) {
					condition.setLowerLimit(Double.parseDouble(s));
				} else {
					condition.setLowerLimit(Double.NEGATIVE_INFINITY);
				}

				s = upperBoundTextField.getText();
				if (Utils.isDouble(s)) {
					condition.setUpperLimit(Double.parseDouble(s));
				} else {
					condition.setUpperLimit(Double.POSITIVE_INFINITY);
				}

				DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
				List<Stock> stocks = condition.filter(listModel.elements());
				listModel.removeAllElements();
				for (Stock stock : stocks) {
					listModel.addElement(stock);
				}
			}
		});

		filterPanel.add(saveButton);
		filterPanel.add(loadButton);
		filterPanel.add(filterButton);
		return filterPanel;
	}
}
