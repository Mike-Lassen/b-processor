package com.bprocessor.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {
    private JToolBar toolBar;
    protected BuildingEditor editor;

    Map<String, JToggleButton> buttons;
    Map<String, Tool> tools;

    ButtonGroup group;

    Image loadImage(String name) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(name);
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        return image;
    }


    public class ToolAction implements ActionListener {
        private String name;

        public ToolAction(String name) {
            this.name = name;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Tool tool = tools.get(name);
            editor.setActiveTool(tool);
        }
    }

    public void registerTool(String name, String iconName, Tool tool) {
        JToggleButton button = new JToggleButton(new ImageIcon(loadImage(iconName)));
        button.setFocusable(false);
        button.addActionListener(new  ToolAction(name));
        toolBar.add(button);
        buttons.put(name, button);
        tools.put(name,  tool);
        group.add(button);
    }

    public void selectTool(String name) {
        JToggleButton button = buttons.get(name);
        Tool tool = tools.get(name);
        button.setSelected(true);
        editor.setActiveTool(tool);
    }

    public ToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar);
        buttons = new HashMap<String, JToggleButton>();
        tools = new HashMap<String, Tool>();
        group = new ButtonGroup();

    }

    public void addSeperator(int size) {
        Dimension dim = new Dimension(size, 0);
        toolBar.addSeparator(dim);
    }
}
