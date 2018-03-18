package me.andimeo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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
	private DataParser dataParser;
	private HistoryManager historyManager;
	private CodeStockMap codeStockMap;

	// stocks components
	private JComboBox<String> historyComboBox;
	private JList<Stock> stocksList;
	private List<Stock> currentStocks;
	private JLabel totalLabel;

	// time unit inputs
	private JRadioButton dayRadioButton;
	private JRadioButton weekRadioButton;
	private JRadioButton monthRadioButton;

	// data source inputs
	private JComboBox<String> dataSourceComboBox;

	// date inputs
	private JComboBox<String> yearComboBox;
	private JComboBox<String> monthComboBox;
	private JComboBox<String> dayComboBox;

	// long or short position inputs
	private JComboBox<String> positionComboBox;
	private final int POSITION_NUM = 4;
	private JTextField positionTextFields[];
	private JLabel positionLabels[];

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
	 * 
	 * @throws IOException
	 */
	public GUI() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		dataParser = new DataParser();
		historyManager = new HistoryManager();
		codeStockMap = CodeStockMap.instance();
		currentStocks = new ArrayList<>();
		totalLabel = new JLabel();

		dataSourceComboBox = new JComboBox<>();

		yearComboBox = new JComboBox<>();
		monthComboBox = new JComboBox<>();
		dayComboBox = new JComboBox<>();

		positionComboBox = new JComboBox<>();
		positionTextFields = new JTextField[4];
		positionLabels = new JLabel[4];
		for (int i = 0; i < POSITION_NUM; i++) {
			positionTextFields[i] = new JTextField();
			positionTextFields[i].setColumns(2);
			positionLabels[i] = new JLabel();
		}

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
						dirTextField.setText("加载数据中，请稍等...");
						System.out.println(dataParser.parse(file));
						dirTextField.setText(file.getCanonicalPath());
						Set<Integer> yearSets = dataParser.yearSets();
						for (int year : yearSets) {
							yearComboBox.addItem(String.valueOf(year));
						}
						for (int i = 1; i < 13; i++) {
							monthComboBox.addItem(String.valueOf(i));
						}
						for (int i = 1; i < 32; i++) {
							dayComboBox.addItem(String.valueOf(i));
						}
						TradingDate lastDate = dataParser.getLastDate();
						yearComboBox.setSelectedItem(String.valueOf(lastDate.year));
						monthComboBox.setSelectedItem(String.valueOf(lastDate.month));
						dayComboBox.setSelectedItem(String.valueOf(lastDate.day));
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
		centerPanel.setLayout(new GridLayout(1, 3));
		centerPanel.add(leftPanel());
		centerPanel.add(rightPanel());
		return centerPanel;
	}

	private JPanel leftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftPanel.setLayout(new BorderLayout(10, 0));

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		historyComboBox = new JComboBox<>();
		DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
		comboBoxModel.addElement("当前");
		comboBoxModel.setSelectedItem("当前");
		historyComboBox.setModel(comboBoxModel);
		historyComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (historyComboBox.getSelectedIndex() == -1) {
					return;
				}
				if (historyComboBox.getSelectedItem().equals("当前")) {
					updateStockJList(currentStocks);
				} else {
					int index = historyComboBox.getSelectedIndex();
					HistoryItem item = historyManager.getHistory(index);
					updateStockJList(item.getStocks());
					updateFilterConditionPanel(item.getCondition());
				}
			}
		});
		top.add(historyComboBox);
		top.add(totalLabel);
		
		leftPanel.add(top, BorderLayout.NORTH);

		stocksList = new JList<>();
		stocksList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (renderer instanceof JLabel && value instanceof Stock) {
					Stock stock = (Stock) value;
					String text = stock.getCode() + "\t" + codeStockMap.getStockName(stock.getCode());
					((JLabel) renderer).setText(text);
				}
				return renderer;
			}
		});
		JScrollPane stocksScrollPane = new JScrollPane(stocksList);
		leftPanel.add(stocksScrollPane, BorderLayout.CENTER);

		DefaultListModel<Stock> stockListModel = new DefaultListModel<>();
		stocksList.setModel(stockListModel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		dataSourceComboBox.addItem("载入所有数据");
		dataSourceComboBox.addItem("载入沪市数据");
		dataSourceComboBox.addItem("载入深市数据");
		dataSourceComboBox.addItem("载入自定义数据");

		JButton clearButton = new JButton("清空");
		JButton saveButton = new JButton("保存");
		JButton loadButton = new JButton("添加");
		
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
				currentStocks.clear();
			}
		});
		// TODO: save

		// load
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			        List<Stock> stocks;
				if (dataSourceComboBox.getSelectedIndex() == 0) {
					stocks = dataParser.getAllStocks();
				} else if (dataSourceComboBox.getSelectedIndex() == 1) {
					stocks = dataParser.getStocksFromSpecificMarket("sh");
				} else if (dataSourceComboBox.getSelectedIndex() == 2) {
					stocks = dataParser.getStocksFromSpecificMarket("sz");
				} else {
					stocks = new ArrayList<>();
				}
				currentStocks.addAll(stocks);
				updateStockJList(currentStocks);
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
		positionPanel.setLayout(new FlowLayout());
		TitledBorder border = new TitledBorder("多/空头");
		positionPanel.setBorder(border);

		positionComboBox.addItem("多头排列");
		positionComboBox.addItem("空头排列");
		positionComboBox.addItem("无");

		positionPanel.add(positionComboBox);
		positionPanel.add(positionTextFields[0]);
		for (int i = 1; i < POSITION_NUM; i++) {
			positionPanel.add(positionLabels[i]);
			positionPanel.add(positionTextFields[i]);
		}

		positionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (positionComboBox.getSelectedIndex() != 2) {
					String text = "<";
					if (positionComboBox.getSelectedIndex() == 0) {
						text = ">";
					}
					for (int i = 0; i < POSITION_NUM; i++) {
						positionTextFields[i].setEditable(true);
						positionLabels[i].setText(text);
					}
				} else {
					for (int i = 0; i < POSITION_NUM; i++) {
						positionLabels[i].setText("");
					}
					for (int i = 0; i < POSITION_NUM; i++) {
						positionTextFields[i].setEditable(false);
					}
				}
			}
		});

		positionComboBox.setSelectedIndex(2);
		return positionPanel;
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
			public void actionPerformed(ActionEvent event) {
				FilterCondition condition = null;
				try {
					condition = generateFilterCondition();
				} catch (TimeUnitException e) {
					JOptionPane.showMessageDialog(frame, "请选择 日/周/月线类型");
					return;
				} catch (DateException e) {
					JOptionPane.showMessageDialog(frame, "日期不合法");
					return;
				}

				List<Stock> inputs = collectStocksList();
				List<Stock> outputs = condition.filter(inputs);

				if (!outputs.isEmpty()) {
					HistoryItem historyItem = generateHistory(condition, inputs);
					int index = historyComboBox.getSelectedIndex();
					historyManager.truncate(index);
					historyManager.addHistory(historyItem);
					updateHistoryJComboBox();
					updateStockJList(outputs);
					currentStocks.clear();
					currentStocks.addAll(outputs);
				} else {
					JOptionPane.showMessageDialog(frame, "没有符合条件的股票");
				}
			}
		});

		filterPanel.add(saveButton);
		filterPanel.add(loadButton);
		filterPanel.add(filterButton);
		return filterPanel;
	}

	private FilterCondition generateFilterCondition() throws TimeUnitException, DateException {
		FilterCondition condition = new FilterCondition();
		if (dayRadioButton.isSelected()) {
			condition.setLineType(LineType.DAY);
		} else if (weekRadioButton.isSelected()) {
			condition.setLineType(LineType.WEEK);
		} else if (monthRadioButton.isSelected()) {
			condition.setLineType(LineType.MONTH);
		} else {
			throw new TimeUnitException();
		}

		String yearStr = (String) yearComboBox.getSelectedItem();
		int year = Integer.parseInt(yearStr);
		String monthStr = (String) monthComboBox.getSelectedItem();
		int month = Integer.parseInt(monthStr);
		String dayStr = (String) dayComboBox.getSelectedItem();
		int day = Integer.parseInt(dayStr);
		if (!dataParser.isLegalTradingDate(year, month, day)) {
			throw new DateException();
		}
		TradingDate date = new TradingDate(year, month, day);
		condition.setDate(date);

		int index = positionComboBox.getSelectedIndex();
		if (index == 0) {
			condition.setPositionType(PositionType.LONG);
			List<Integer> positions = new ArrayList<Integer>();
			for (int i = 0; i < 4; i++) {
				String s = positionTextFields[i].getText();
				if (Utils.isInt(s)) {
					positions.add(Integer.parseInt(s));
				}
			}
			condition.setPositions(positions);
		} else if (index == 1) {
			condition.setPositionType(PositionType.SHORT);
			List<Integer> positions = new ArrayList<Integer>();
			for (int i = 0; i < 4; i++) {
				String s = positionTextFields[i].getText();
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
		return condition;
	}

	private List<Stock> collectStocksList() {
		List<Stock> stocks = new ArrayList<>();
		DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
		for (int i = 0; i < listModel.getSize(); i++) {
			stocks.add(listModel.get(i));
		}
		return stocks;
	}

	private HistoryItem generateHistory(FilterCondition condition, List<Stock> list) {
		HistoryItem item = new HistoryItem();
		item.setCondition(condition);
		item.setStocks(list);
		return item;
	}

	private void updateHistoryJComboBox() {
		DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) historyComboBox.getModel();
		int size = comboBoxModel.getSize();
		for (int i = 0; i < size - 1; i++) {
			comboBoxModel.removeElementAt(0);
		}
		for (int i = 0; i < historyManager.size(); i++) {
			comboBoxModel.insertElementAt(String.valueOf(i + 1), i);
		}
		comboBoxModel.setSelectedItem("当前");
	}

	private void updateStockJList(List<Stock> stocks) {
		DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
		listModel.removeAllElements();
		for (Stock stock : stocks) {
			listModel.addElement(stock);
		}
		totalLabel.setText("股票池数量: " + stocks.size());
	}

	private void updateFilterConditionPanel(FilterCondition condition) {
		switch (condition.getLineType()) {
		case DAY:
			dayRadioButton.setSelected(true);
			break;
		case WEEK:
			weekRadioButton.setSelected(true);
			break;
		case MONTH:
			monthRadioButton.setSelected(true);
			break;
		}

		TradingDate date = condition.getDate();
		yearComboBox.setSelectedItem(String.valueOf(date.year));
		monthComboBox.setSelectedItem(String.valueOf(date.month));
		dayComboBox.setSelectedItem(String.valueOf(date.day));

		switch (condition.positionType()) {
		case LONG:
			positionComboBox.setSelectedIndex(0);
			List<Integer> longPositions = condition.getPositions();
			for (int i = 0; i < longPositions.size(); i++) {
				positionTextFields[i].setText(String.valueOf(longPositions.get(i)));
			}
			break;
		case SHORT:
			positionComboBox.setSelectedIndex(1);
			List<Integer> shortPositions = condition.getPositions();
			for (int i = 0; i < shortPositions.size(); i++) {
				positionTextFields[i].setText(String.valueOf(shortPositions.get(i)));
			}
			break;
		case NONE:
			positionComboBox.setSelectedIndex(2);
			break;
		}

		double lowerLimit = condition.getLowerLimit();
		double upperLimit = condition.getUpperLimit();
		if (lowerLimit == Double.NEGATIVE_INFINITY) {
			lowerBoundTextField.setText("");
		} else {
			lowerBoundTextField.setText(String.valueOf(lowerLimit));
		}

		if (upperLimit == Double.POSITIVE_INFINITY) {
			upperBoundTextField.setText("");
		} else {
			upperBoundTextField.setText(String.valueOf(upperLimit));
		}
	}
}
