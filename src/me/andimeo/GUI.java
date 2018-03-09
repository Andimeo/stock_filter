package me.andimeo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class GUI {

	private JFrame frame;

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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		topPanel.add(dirTextField);
		topPanel.add(chooseButton);
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2));
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		leftPanel.setLayout(new BorderLayout(10, 0));
		centerPanel.add(leftPanel);

		JScrollPane batchesScrollPane = new JScrollPane();
		leftPanel.add(batchesScrollPane, BorderLayout.WEST);
		batchesScrollPane.setPreferredSize(new Dimension(20, 500));
		JList<String> batchesList = new JList<>();
		batchesList.setPreferredSize(new Dimension(20, 500));
		batchesScrollPane.add(batchesList);

		JScrollPane stocksScrollPane = new JScrollPane();
		leftPanel.add(stocksScrollPane, BorderLayout.CENTER);
		stocksScrollPane.setPreferredSize(new Dimension(200, 500));
		JList<String> stocksList = new JList<>();
		stocksList.setPreferredSize(new Dimension(200, 500));
		stocksScrollPane.setViewportView(stocksList);

		DefaultListModel<String> listModel = new DefaultListModel<>();
		listModel.addElement("Jane Doe");
		listModel.addElement("John Smith");
		listModel.addElement("Kathy Green");
		stocksList.setModel(listModel);

		JPanel rightPanel = new JPanel();
		centerPanel.add(rightPanel);
		rightPanel.setLayout(new GridLayout(5, 0, 10, 10));

		rightPanel.add(timeUnitPanel());
		rightPanel.add(datePanel());
		rightPanel.add(positionPanel());
		rightPanel.add(turnoverPanel());

	}

	private JPanel timeUnitPanel() {
		JPanel timeUnitPanel = new JPanel();
		timeUnitPanel.setLayout(new GridLayout(1, 3, 0, 0));
		TitledBorder border = new TitledBorder("日/周/月线");
		timeUnitPanel.setBorder(border);

		JRadioButton dayRadioButton = new JRadioButton("日线");
		JRadioButton weekRadioButton = new JRadioButton("周线");
		JRadioButton monthRadioButton = new JRadioButton("月线");
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
}
