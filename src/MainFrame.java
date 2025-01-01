import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Highlighter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class MainFrame extends JFrame implements ActionListener {
	String defaultTitle = "File reader";

	Scanner scanner;

	JScrollPane scrollPane;
	JScrollBar verticalScrollBar;
	JScrollBar horizontalScrollBar;
	JTextArea textArea;

	JPanel buttonPanel;
	JButton openFile;
	JButton copyButton;
	JButton clearButton;
	JButton findButton;
	JButton changeThemeButton;

	Boolean darkMode = false;

	// Dark mode colours
	Color darkBackgroundColour = new Color(40, 40, 40);
	Color darkSecondaryBackgroundColour = new Color(30, 30, 30);

	Color whiteText = new Color(215, 215, 215);

	// Light mode colours
	Color lightBackgroundColour = this.getContentPane().getBackground();
	Color lightSecondaryBackgroundColour = new Color(243, 243, 243);

	Color blackText = new Color(51, 51, 51);

	public MainFrame() {
		// Textbox/textfield
		textArea = new JTextArea("Open a file to view its contents.");
		textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		textArea.setEditable(false);

		scrollPane = new JScrollPane(textArea);
		verticalScrollBar = scrollPane.getVerticalScrollBar();
		horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		scrollPane.setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
		this.add(scrollPane, BorderLayout.CENTER);

		// Buttons
		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		this.add(buttonPanel, BorderLayout.SOUTH);

		openFile = setUpButton("Open file");
		copyButton = setUpButton("Copy entire text");
		clearButton = setUpButton("Clear text");
		findButton = setUpButton("Find");
		changeThemeButton = setUpButton("Toggle dark mode");

		// Window setup
		setWindowTitle(defaultTitle);
		this.setSize(512, 400);
		this.setLocationRelativeTo(null);
		this.setMinimumSize(this.getSize());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openFile) {
			JFileChooser fileChooser = new JFileChooser();

			int fileChosen = fileChooser.showOpenDialog(this);

			if (fileChosen == JFileChooser.APPROVE_OPTION) {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				File file = new File(filePath);

				try {
					scanner = new Scanner(new FileInputStream(file));
				} catch (FileNotFoundException | SecurityException exception) {
					JOptionPane.showMessageDialog(this, "This file cannot be read or found....", "Unable to read!",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					if (scanner != null) {
						textArea.setText("");

						while (scanner.hasNextLine()) {
							textArea.append(String.format("%s\n", scanner.nextLine()));
						}

						textArea.setCaretPosition(0);
						this.setTitle(String.format("%s (%s)", defaultTitle, filePath));
						scanner.close();
						scanner = null;
					}
				}
			}
		} else if (e.getSource() == changeThemeButton) {
			darkMode = !darkMode;

			if (darkMode) {
				setTheme(darkBackgroundColour, darkSecondaryBackgroundColour, whiteText, "light");
			} else {
				setTheme(lightBackgroundColour, lightSecondaryBackgroundColour, blackText, "dark");
			}
		} else if (e.getSource() == copyButton) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(textArea.getText()), null);
			JOptionPane.showMessageDialog(this, "Text copied to clipboard!", "Text copied!",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == clearButton) {
			textArea.setText("");
			setWindowTitle(defaultTitle);
		} else if (e.getSource() == findButton) {
			String wordToFind = JOptionPane.showInputDialog("Find a word");
			String fileText = textArea.getText();
			Highlighter highlighter = textArea.getHighlighter();

			Pattern pattern = Pattern.compile(wordToFind);
			Matcher matcher = pattern.matcher(fileText);

			while (matcher.find()) {
				try {
					highlighter.addHighlight(matcher.start(), matcher.end(), new DefaultHighlightPainter(Color.RED));
				} catch (BadLocationException e1) {
					System.out.println("Bad location");
				}
			}
		}
	}

	// GUI setup

	private JButton setUpButton(String buttonText) {
		JButton button = new JButton(buttonText);
		button.setFocusable(false);
		button.addActionListener(this);
		buttonPanel.add(button);
		return button;
	}

	private void setTheme(Color backgroundColour, Color textBackgroundColour, Color textColour, String themeName) {
		changeThemeButton.setText(String.format("Toggle %s mode", themeName));
		this.getContentPane().setBackground(backgroundColour);
		// Changing the scrollbar colours
		verticalScrollBar.setBackground(backgroundColour);
		horizontalScrollBar.setBackground(backgroundColour);
		// Changing the textbox's colours
		textArea.setBackground(textBackgroundColour);
		textArea.setForeground(textColour);
		textArea.setCaretColor(textColour);
		// Changing the colours of the buttons
		for (Component component : buttonPanel.getComponents()) {
			component.setBackground(backgroundColour);
			component.setForeground(textColour);
		}
	}

	private void setWindowTitle(String text) {
		this.setTitle(text);
	}
}
