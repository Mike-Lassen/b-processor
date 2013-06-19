package com.bprocessor.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
    private Map<String, JToggleButton> buttons;
    private Map<String, Tool> tools;
    private Tool activeTool;    
    private ButtonGroup group;

    public ToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar);
        buttons = new HashMap<String, JToggleButton>();
        tools = new HashMap<String, Tool>();
        group = new ButtonGroup();

    }
    
    public InputListener getInputListener() {
    	return new ToolInputListener();
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
    	if(name != null) {
    		JToggleButton button = buttons.get(name);
    		Tool tool = tools.get(name);
    		button.setSelected(true);
    		changeTool(tool);
    	} else {
    		group.clearSelection();
    		changeTool(null);
    	}
    }
    
    public void changeTool(Tool tool) {
    	if (activeTool != null) {
    		activeTool.finish();
    	}
    	activeTool = tool;
    	if (activeTool != null) {
    		activeTool.prepare();
    	}
    }
    
    
    public void disableAll() {
    	for (JToggleButton button : buttons.values()) {
    		button.setEnabled(false);
    	}
    }
    public void enableAll() {
    	for (JToggleButton button : buttons.values()) {
    		button.setEnabled(true);
    	}
    }

    

    public void addSeperator(int size) {
        Dimension dim = new Dimension(size, 0);
        toolBar.addSeparator(dim);
    }
    
    
    private Image loadImage(String name) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(name);
        Image image = Toolkit.getDefaultToolkit().getImage(url);
        return image;
    }
    
    private class ToolAction implements ActionListener {
        private String name;

        public ToolAction(String name) {
            this.name = name;
        }
        @Override
        public void actionPerformed(ActionEvent event) {
            Tool tool = tools.get(name);
            changeTool(tool);
        }
    }
    
    public class ToolInputListener implements InputListener  {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (activeTool != null) {
                activeTool.mouseWheelMoved(e);
            }
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (activeTool != null) {
                activeTool.keyPressed(e);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (activeTool != null) {
                activeTool.keyReleased(e);
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            if (activeTool != null) {
                activeTool.keyTyped(e);
            }
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseDragged(e);
            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseMoved(e);
            }
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseClicked(e);
            }
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseEntered(e);
            }
        }
        @Override
        public void mouseExited(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseExited(e);
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mousePressed(e);
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (activeTool != null) {
                activeTool.mouseReleased(e);
            }
        }
    }
}
