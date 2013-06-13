package com.bprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;

import com.bprocessor.Sketch;
import com.bprocessor.io.Persistence;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    public static MainFrame instance;
    private GlobalMenuBar menubar;
    private ToolBar toolbar;
    private StatusBar statusbar;
    private BuildingEditor editor;

    private Sketch sketch = null;

    public void setup() {
        menubar.setup();
    }
    public void userNew() {
        if (userClose()) {
            sketch = new Sketch("sketch");
            editor.setSketch(sketch);
        }
    }
    private int changesShouldBeSaved()
    {
        final Object[] options = { "Yes", "No", "Cancel" };

        return JOptionPane.showOptionDialog(this,
                "Save changes before closing?",
                "Close",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
    }

    public void userOpen() {
        if (userClose()) {
            FileDialog dialog = new FileDialog(this);
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);
            dialog.setFilenameFilter(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String extension = "";
                    int i = name.lastIndexOf('.');
                    if (i > 0) {
                        extension = name.substring(i+1);
                        extension.equals("bps");
                    }
                    return true;
                }
            });
            dialog.setVisible(true);
            if (dialog.getFile() != null) {
                File file = new File(dialog.getDirectory(), dialog.getFile());
                Sketch theSketch = Persistence.load(file);
                if (theSketch != null) {
                    theSketch.setPath(file.getAbsolutePath());
                    sketch = theSketch;
                    editor.setSketch(sketch);
                }
            }
        }
    }
    public boolean userClose() {
        if (sketch != null) {
        	if (sketch.isModified()) {
        		int result = changesShouldBeSaved();
        		if (result == JOptionPane.YES_OPTION) {
        			if (!userSave()) {
        				return false;
        			}
        		} else if (result == JOptionPane.CANCEL_OPTION) {
        			return false;
        		}
        	}
        	editor.setSketch(null);
            sketch = null;
        }
        return true;
    }
    public boolean userSave() {
    	if (sketch.isModified()) {
    		if (sketch.getPath() != null) {
    			Persistence.save(sketch, sketch.getPath());
    			sketch.setModified(false);
    			return true;
    		} else {
    			return userSaveAs();
    		}
    	} else {
    		return true;
    	}
    }
    public boolean userSaveAs() {
        FileDialog dialog = new FileDialog(this, "Save", FileDialog.SAVE);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
        if (dialog.getFile() != null) {
            File file = new File(dialog.getDirectory(), dialog.getFile());
            sketch.setPath(file.getAbsolutePath() + ".bps");
            Persistence.save(sketch, sketch.getPath());
            sketch.setModified(false);
            return true;
        } else {
        	return false;
        }
    }

    public class GlobalMenuBar extends MenuBar {
        public FileMenu fileMenu;

        public void setup() {
            if (sketch != null) {
                if (sketch.getPath() == null) {
                    MainFrame.this.setTitle("B-Processor - Untitled");
                } else {
                    MainFrame.this.setTitle("B-Processor - " + sketch.getPath());
                }
                fileMenu.newItem.setEnabled(true);
                fileMenu.openItem.setEnabled(true);
                fileMenu.closeItem.setEnabled(true);
                fileMenu.saveItem.setEnabled(sketch.isModified());
                fileMenu.saveAsItem.setEnabled(true);
            } else {
                MainFrame.this.setTitle("B-Processor");
                fileMenu.newItem.setEnabled(true);
                fileMenu.openItem.setEnabled(true);
                fileMenu.closeItem.setEnabled(false);
                fileMenu.saveItem.setEnabled(false);
                fileMenu.saveAsItem.setEnabled(false);

            }
        }

        public class FileMenu extends Menu {
            public MenuItem newItem;
            public MenuItem openItem;
            public MenuItem closeItem;
            public MenuItem saveItem;
            public MenuItem saveAsItem;

            public FileMenu(String title) {
                super(title);
                { 
                    newItem = new MenuItem("New Sketch...");
                    newItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            userNew();
                            setup();
                        }
                    });
                    add(newItem);
                }
                { 
                    openItem = new MenuItem("Open Sketch...");
                    openItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            userOpen();
                            setup();
                        }
                    });
                    add(openItem);
                }
                addSeparator();
                { 
                    closeItem = new MenuItem("Close");
                    closeItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            userClose();
                            setup();
                        }
                    });
                    add(closeItem);
                }
                { 
                    saveItem = new MenuItem("Save");
                    saveItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            userSave();
                            setup();
                        }
                    });
                    add(saveItem);
                }
                { 
                    saveAsItem = new MenuItem("Save as...");
                    saveAsItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            userSaveAs();
                            setup();
                        }
                    });
                    add(saveAsItem);
                }
            }
        }
        public GlobalMenuBar() {
            fileMenu = new FileMenu("File");
            this.add(fileMenu);
            ;		}
    }

    public class TreeArea extends JPanel {
        public TreeArea() {
            this.addMouseListener(dragger);
            setLayout(new BorderLayout());
            JPanel top = new JPanel();
            top.setPreferredSize(new Dimension(320, 24));
            top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
            add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            Color ligth = new Color(0xF9, 0xF9, 0xF9);
            center.setBackground(ligth);
            JScrollPane scroll = new JScrollPane(center);
            scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
            add(scroll, BorderLayout.CENTER);
        }
    }
    public class EditorArea extends JPanel {
        public EditorArea() {
            this.addMouseListener(dragger);
            setLayout(new BorderLayout());
            JPanel top = new JPanel();
            top.setPreferredSize(new Dimension(320, 24));
            top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
            add(top, BorderLayout.NORTH);
            editor = new BuildingEditor();
            editor.addMouseListener(new Blocker());
            add(editor, BorderLayout.CENTER);
        }
    }
    public class AttributesArea extends JPanel {
        public AttributesArea() {
            this.addMouseListener(dragger);
            setLayout(new BorderLayout());
            JPanel top = new JPanel();
            top.setPreferredSize(new Dimension(320, 24));
            top.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.GRAY));
            add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
            Color ligth = new Color(0xF9, 0xF9, 0xF9);
            center.setBackground(ligth);
            add(center, BorderLayout.CENTER);
        }
    }

    public class Dragger extends MouseInputAdapter {
        public void mousePressed(MouseEvent e) {
        }
    }
    public class Blocker extends MouseInputAdapter {
    }

    Dragger dragger;

    public class MainArea extends JPanel {
        public MainArea() {
            this.addMouseListener(dragger);
            setLayout(new BorderLayout());
            JPanel left = new TreeArea();
            left.setPreferredSize(new Dimension(320, 672));
            left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
            add(left, BorderLayout.WEST);

            JPanel center = new EditorArea();
            center.setPreferredSize(new Dimension(640, 672));
            center.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
            add(center, BorderLayout.CENTER);

            JPanel right = new AttributesArea();
            right.setPreferredSize(new Dimension(320, 672));
            right.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY));
            add(right, BorderLayout.EAST);
        }
    }

    public MainFrame() {
        instance = this;
        this.setTitle("B-Processor");
        menubar = new GlobalMenuBar();
        this.setMenuBar(menubar);
        dragger = new Dragger();
        setLayout(new BorderLayout());

        toolbar = new ToolBar();
        toolbar.setPreferredSize(new Dimension(1280, 40));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        add(toolbar, BorderLayout.NORTH);

        JPanel center = new MainArea();
        center.setPreferredSize(new Dimension(1280, 672));
        center.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE));
        add(center, BorderLayout.CENTER);

        statusbar = new StatusBar();
        statusbar.setPreferredSize(new Dimension(1280, 40));

        statusbar.setBorder(new EtchedBorder() {
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(getShadowColor(c));
                g.drawLine(x, y, x + width, y);

                g.setColor(getHighlightColor(c));
                g.drawLine(x, y + 1, x + width, y + 1);
            }
        });
        add(statusbar, BorderLayout.SOUTH);

        toolbar.editor = editor;
        editor.toolbar = toolbar;
        editor.statusbar = statusbar;
        
        editor.setup();
        menubar.setup();
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
