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
import java.util.LinkedHashSet;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import me.andimeo.FilterCondition.LineType;
import me.andimeo.FilterCondition.PositionType;

public class GUI {

	private JFrame frame;
	private DataParser dataParser;
	private HistoryManager historyManager;
	private CodeStockMap codeStockMap;
	private Template template;
	private LineType lineType;
	private PositionType positionType;

	// stocks components
	private JComboBox<String> historyComboBox;
	private JList<Stock> stocksList;
	private Set<Stock> currentStocks;
	private JLabel totalLabel;

	// time unit inputs
	private JRadioButton dayRadioButton;
	private JRadioButton weekRadioButton;
	private JRadioButton monthRadioButton;

	// data source inputs
	private JComboBox<String> dataSourceComboBox;

	// date inputs
	private JComboBox<Integer> durationComboBox;
	private JLabel durationLabel;
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
		template = new Template();
		currentStocks = new LinkedHashSet<>();
		totalLabel = new JLabel();
		lineType = null;
		positionType = PositionType.NONE;

		dataSourceComboBox = new JComboBox<>();

		durationComboBox = new JComboBox<>();
		for (int i = 0; i < 20; i++) {
			durationComboBox.insertItemAt(i + 1, i);
		}
		durationComboBox.setSelectedIndex(0);
		durationLabel = new JLabel("筛选天数");
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
					updateStockJList();
				} else {
					int index = historyComboBox.getSelectedIndex();
					HistoryItem item = historyManager.getHistory(index);
					currentStocks.clear();
					currentStocks.addAll(item.getStocks());
					updateStockJList();
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
					String text = "<html><pre> " + stock.getCode() + "        "
							+ codeStockMap.getStockName(stock.getCode()) + "</pre></html>";
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
		dataSourceComboBox.addItem("载入沪市A股");
		dataSourceComboBox.addItem("载入深市A股");
		dataSourceComboBox.addItem("载入B股数据");
		dataSourceComboBox.addItem("载入中小创数据");
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

		// save
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setDialogTitle("选择目录");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Stock Filter", "sf");
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fileChooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.getAbsolutePath().endsWith("sf")) {
						file = new File(file + ".sf");
					}
					Set<String> codes = new LinkedHashSet<>();
					currentStocks.stream().forEach(stock -> {
						codes.add(stock.getCode());
					});
					try {
						StockSerializer.serialize(file, codes);
						JOptionPane.showMessageDialog(frame, "保存成功");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		});
		// load
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Stock> stocks;
				if (dataSourceComboBox.getSelectedIndex() == 0) {
					stocks = dataParser.getAllStocks();
				} else if (dataSourceComboBox.getSelectedIndex() == 1) {
					stocks = dataParser.getShangHaiA();
				} else if (dataSourceComboBox.getSelectedIndex() == 2) {
					stocks = dataParser.getShenZhenA();
				} else if (dataSourceComboBox.getSelectedIndex() == 3) {
					stocks = dataParser.getB();
				} else if (dataSourceComboBox.getSelectedIndex() == 4) {
					stocks = dataParser.getXiaopan();
				} else {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("."));
					fileChooser.setDialogTitle("选择目录");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("Stock Filter", "sf");
					fileChooser.setFileFilter(filter);
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setAcceptAllFileFilterUsed(false);
					stocks = new ArrayList<>();
					int returnVal = fileChooser.showOpenDialog(frame);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						try {
							Set<String> codes = new LinkedHashSet<>();
							StockSerializer.deserialize(file, codes);
							codes.stream().forEach(code -> {
								stocks.add(dataParser.getStockByCode(code));
							});
						} catch (IOException e1) {
							stocks.clear();
							e1.printStackTrace();
						}
					}
				}
				currentStocks.addAll(stocks);
				updateStockJList();
				JOptionPane.showMessageDialog(frame, "载入自定义数据成功");
			}
		});

		bottomPanel.add(clearButton);
		bottomPanel.add(dataSourceComboBox);
		bottomPanel.add(loadButton);
		bottomPanel.add(saveButton);
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
		dayRadioButton.setSelected(true);
		lineType = LineType.DAY;
		dayRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTemplate();
				updatePositionsByTemplate(LineType.DAY);
				lineType = LineType.DAY;
				durationComboBox.setVisible(true);
				durationLabel.setVisible(true);
			}
		});
		weekRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTemplate();
				updatePositionsByTemplate(LineType.WEEK);
				lineType = LineType.WEEK;
				durationComboBox.setVisible(false);
				durationLabel.setVisible(false);
			}
		});
		monthRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTemplate();
				updatePositionsByTemplate(LineType.MONTH);
				lineType = LineType.MONTH;
				durationComboBox.setVisible(false);
				durationLabel.setVisible(false);
			}
		});
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
		datePanel.add(durationComboBox);
		datePanel.add(durationLabel);
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
				updateTemplate();
				if (positionComboBox.getSelectedIndex() != 2) {
					String text = "<";
					positionType = PositionType.SHORT;
					if (positionComboBox.getSelectedIndex() == 0) {
						text = ">";
						positionType = PositionType.LONG;
					}
					for (int i = 0; i < POSITION_NUM; i++) {
						Integer value = template.get(lineType, positionType, i);
						positionTextFields[i].setEditable(true);
						positionLabels[i].setText(text);
						if (value == null) {
							positionTextFields[i].setText("");
						} else {
							positionTextFields[i].setText("" + value);
						}
					}
				} else {
					for (int i = 0; i < POSITION_NUM; i++) {
						positionLabels[i].setText("");
					}
					for (int i = 0; i < POSITION_NUM; i++) {
						positionTextFields[i].setText("");
						positionTextFields[i].setEditable(false);
					}
					positionType = PositionType.NONE;
				}
			}
		});

		positionComboBox.setSelectedIndex(2);
		return positionPanel;
	}

	private JPanel turnoverPanel() {
		JPanel turnoverPanel = new JPanel();
		turnoverPanel.setLayout(new FlowLayout());

		TitledBorder border = new TitledBorder("成交额（万元）");
		turnoverPanel.setBorder(border);

		JLabel lowerBoundLabel = new JLabel("下界");
		lowerBoundLabel.setToolTipText("为空时代表无最低金额约束");
		turnoverPanel.add(lowerBoundLabel);
		turnoverPanel.add(lowerBoundTextField);
		lowerBoundTextField.setColumns(5);

		JLabel upperBoundLabel = new JLabel("上界");
		upperBoundLabel.setToolTipText("为空时代表无最高金额约束");
		turnoverPanel.add(upperBoundLabel);
		turnoverPanel.add(upperBoundTextField);
		upperBoundTextField.setColumns(5);
		return turnoverPanel;
	}

	private JPanel filterPanel() {
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new FlowLayout());

		JButton saveButton = new JButton("保存模版");
		JButton loadButton = new JButton("载入模版");
		JButton filterButton = new JButton("筛选");

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setDialogTitle("选择目录");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Stock Filter Template", "sft");
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fileChooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (!file.getAbsolutePath().endsWith("sft")) {
						file = new File(file + ".sft");
					}
					try {
						template.serialize(file);
						JOptionPane.showMessageDialog(frame, "保存成功");
					} catch (IOException e3) {
						e3.printStackTrace();
						return;
					}
				}
			}

		});

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				fileChooser.setDialogTitle("选择目录");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Stock Filter Template", "sft");
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						template.deserialize(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

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
					currentStocks.clear();
					currentStocks.addAll(outputs);
					updateStockJList();

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
		condition.setDuration(1);
		if (dayRadioButton.isSelected()) {
			condition.setLineType(LineType.DAY);
			condition.setDuration((Integer) durationComboBox.getSelectedItem());
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
		historyComboBox.setSelectedIndex(-1);
		DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) historyComboBox.getModel();
		int size = comboBoxModel.getSize();
		for (int i = 0; i < size - 1; i++) {
			comboBoxModel.removeElementAt(0);
		}
		for (int i = 0; i < historyManager.size(); i++) {
			StringBuilder sb = new StringBuilder();
			sb.append("<html><pre>");
			sb.append(String.format("%d (%d)", i + 1, historyManager.getHistory(i).getStocks().size()));
			sb.append("</pre></html>");
			comboBoxModel.insertElementAt(sb.toString(), i);
		}
		comboBoxModel.setSelectedItem("当前");
	}

	private void updateStockJList() {
		DefaultListModel<Stock> listModel = (DefaultListModel<Stock>) stocksList.getModel();
		listModel.removeAllElements();
		for (Stock stock : currentStocks) {
			listModel.addElement(stock);
		}
		totalLabel.setText("股票池数量: " + currentStocks.size());
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
			for (int i = longPositions.size(); i < POSITION_NUM; i++) {
				positionTextFields[i].setText("");
			}
			break;
		case SHORT:
			positionComboBox.setSelectedIndex(1);
			List<Integer> shortPositions = condition.getPositions();
			for (int i = 0; i < shortPositions.size(); i++) {
				positionTextFields[i].setText(String.valueOf(shortPositions.get(i)));
			}
			for (int i = shortPositions.size(); i < POSITION_NUM; i++) {
				positionTextFields[i].setText("");
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

	private void updateTemplate() {
		if (lineType != null && positionType != PositionType.NONE) {
			for (int i = 0; i < POSITION_NUM; i++) {
				template.set(lineType, positionType, i, null);
			}
			for (int i = 0; i < POSITION_NUM; i++) {
				Integer value;
				String v = positionTextFields[i].getText();
				try {
					value = Integer.parseInt(v);
				} catch (NumberFormatException e1) {
					value = null;
				}
				if (value == null) {
					break;
				}
				template.set(lineType, positionType, i, value);
			}
		}
	}

	private void updatePositionsByTemplate(LineType lineType) {
		if (positionType == PositionType.NONE) {
			return;
		}

		for (int i = 0; i < POSITION_NUM; i++) {
			String text = "";
			Integer value = template.get(lineType, positionType, i);
			if (value != null) {
				text = "" + value;
			}
			positionTextFields[i].setText(text);
		}
	}
}
