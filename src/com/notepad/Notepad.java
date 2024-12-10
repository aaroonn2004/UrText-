package com.notepad;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class Notepad {

	private static JFrame frame;
	private final int MAX_LENGTH = 500, MAX_HEIGHT = 450;
	private final Container cont;
	private JMenuBar jmbNotepad;
	private JMenu jmArchivo, jmAcerca;
	private JMenuItem jmiArchivoNuevo, jmiArchivoAbrir, jmiArchivoGuardar, jmiArchivoGuardarComo, jmiArchivoCerrar, jmiAcerca;
	private static JTextArea textArea;
	private JScrollPane scrollPane;
	private JPanel panelTexto;
	private JTextField txfInfo;
	private CaretListener listener;
	private static File file;
	private static JFileChooser selector;

	public Notepad() {
		frame = new JFrame();
		this.cont = frame.getContentPane();
		frame.setTitle(NotepadTextos.FRAME_TITLE);
		frame.setSize(MAX_LENGTH, MAX_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selector = new JFileChooser();
		anadirBarra();
		agregarElementosCont();
		frame.setVisible(true);
	}

	private void anadirBarra() {
		jmbNotepad = new JMenuBar();
		jmArchivo = new JMenu(NotepadTextos.MENU_ARCHIVO);
		jmAcerca = new JMenu(NotepadTextos.MENU_ACERCA);
		jmiArchivoNuevo = new JMenuItem(NotepadTextos.MENU_ITEM_NUEVO);
		jmiArchivoAbrir = new JMenuItem(NotepadTextos.MENU_ITEM_ABRIR);
		jmiArchivoGuardar = new JMenuItem(NotepadTextos.MENU_ITEM_GUARDAR);
		jmiArchivoGuardarComo = new JMenuItem(NotepadTextos.MENU_ITEM_GUARDAR_COMO);
		jmiArchivoCerrar = new JMenuItem(NotepadTextos.MENU_ITEM_CERRAR);
		jmiAcerca = new JMenuItem(NotepadTextos.ACERCA_ITEM_DE);

		ActionListen actionListen = new ActionListen();
		
		jmiArchivoNuevo.addActionListener(actionListen);
		jmiArchivoAbrir.addActionListener(actionListen);
		jmiArchivoGuardar.addActionListener(actionListen);
		jmiArchivoGuardarComo.addActionListener(actionListen);
		jmiArchivoCerrar.addActionListener(actionListen);

		jmArchivo.add(jmiArchivoNuevo);
		jmArchivo.add(jmiArchivoAbrir);
		jmArchivo.add(jmiArchivoGuardar);
		jmArchivo.add(jmiArchivoGuardarComo);
		jmArchivo.add(jmiArchivoCerrar);

		jmiAcerca.addActionListener(actionListen);
		
		jmAcerca.add(jmiAcerca);
		
		jmbNotepad.add(jmArchivo);
		jmbNotepad.add(jmAcerca);
		getFrame().setJMenuBar(jmbNotepad);
	}

	private void agregarElementosCont() {
		panelTexto = new JPanel();
		panelTexto.setLayout(new BorderLayout());
		txfInfo = new JTextField();
		txfInfo.setText(String.format(NotepadTextos.INFO_LINEA_COLUMNA, 0, 0));
		txfInfo.setEditable(false);
		setTextArea(new JTextArea());
		getTextArea().setFont(new Font("Arial", Font.PLAIN, 12));
		scrollPane = new JScrollPane(getTextArea());
		panelTexto.add(scrollPane, BorderLayout.CENTER);
		panelTexto.add(txfInfo, BorderLayout.SOUTH);
		cont.add(panelTexto);

		listener = new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				int posicionF = e.getDot();

				try {
					int linea = getTextArea().getLineOfOffset(posicionF);
					int columna = posicionF - getTextArea().getLineStartOffset(linea);

					String info = String.format(NotepadTextos.INFO_LINEA_COLUMNA, linea + 1, columna + 1);
					txfInfo.setText(info);
				} catch (BadLocationException e1) {
					System.err.println(NotepadTextos.ERROR_CARET);
				}
			}
		};

		getTextArea().addCaretListener(listener);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (Exception e) {
			System.err.println(NotepadTextos.ERROR_LAF + e);
		}
		new Notepad();
	}

	public static JFileChooser getSelector() {
		return selector;
	}

	public static File getFile() {
		return file;
	}

	public static void setFile(File file) {
		Notepad.file = file;
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		Notepad.textArea = textArea;
	}
}

class ActionListen implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		File file = Notepad.getFile();
		JFileChooser selector = Notepad.getSelector();
		FileNameExtensionFilter texto = new FileNameExtensionFilter(NotepadTextos.FILE_FILTER_DESC, "txt", "xml",
				"html");
		selector.setFileFilter(texto);
		int result;

		if (source instanceof JMenuItem) {
			JMenuItem menuItem = (JMenuItem) source;
			String com = menuItem.getText();

			switch (com) {
			case NotepadTextos.MENU_ITEM_ABRIR:
				result = selector.showOpenDialog(Notepad.getFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					file = selector.getSelectedFile();
					Notepad.getFrame().setTitle(NotepadTextos.FRAME_TITLE + " - " + file.getName());
					Notepad.setFile(file);
					Notepad.getTextArea().setText(leerArchivo());
				}
				break;

			case NotepadTextos.MENU_ITEM_NUEVO:
				Notepad.getTextArea().setText("");
				Notepad.setFile(null);
				Notepad.getFrame().setTitle(NotepadTextos.FRAME_TITLE);
				break;

			case NotepadTextos.MENU_ITEM_GUARDAR:
				if (Notepad.getFile()!=null) {					
					escribirArchivo();
				} else {
					guardar(selector);
				}
				break;

			case NotepadTextos.MENU_ITEM_GUARDAR_COMO:
				guardar(selector);
				break;
				
			case NotepadTextos.MENU_ITEM_CERRAR:
				System.exit(1);
				break;
				
			case NotepadTextos.ACERCA_ITEM_DE:
				JOptionPane.showMessageDialog(null, NotepadTextos.ACERCA_DE_MENSAJE, NotepadTextos.MENU_ACERCA, JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}

	private String leerArchivo() {
		String cadena = "";
		int i;
		try {
			FileReader fr = new FileReader(Notepad.getFile());

			while ((i = fr.read()) != -1) {
				cadena += (char) i;
			}
			fr.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, NotepadTextos.ERROR_FILE_READER, NotepadTextos.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, NotepadTextos.ERROR_FILE_IO, NotepadTextos.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
		}
		return cadena;
	}
	
	private void guardar(JFileChooser selector) {
		File file = Notepad.getFile();
		int result = selector.showSaveDialog(Notepad.getFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			file = selector.getSelectedFile();
			Notepad.setFile(file);
			escribirArchivo();
			Notepad.getFrame().setTitle(NotepadTextos.FRAME_TITLE + " - " + file.getName());
		}
	}

	private void escribirArchivo() {
		FileWriter ficEscribe;
		try {
			ficEscribe = new FileWriter(Notepad.getFile());
			
			String cadena = Notepad.getTextArea().getText();

			char[] cad = cadena.toCharArray();

			for (int i = 0; i < cad.length; i++) {
				ficEscribe.write(cad[i]);
			}

			ficEscribe.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, NotepadTextos.ERROR_FILE_READER, NotepadTextos.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, NotepadTextos.ERROR_FILE_IO, NotepadTextos.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
		}
	}
}