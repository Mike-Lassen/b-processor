package com.bprocessor.ui.tools;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.bprocessor.Geometry;
import com.bprocessor.Path;
import com.bprocessor.Surface;
import com.bprocessor.ui.SketchView;
import com.bprocessor.ui.StandardTool;
import com.bprocessor.ui.StatusBar;
import com.bprocessor.ui.commands.EraseSurface;
import com.bprocessor.util.CommandManager;
import com.bprocessor.util.Filter;

public class EraserTool extends StandardTool {
	public EraserTool(SketchView view, StatusBar statusbar) {
		super(view, statusbar);
	}

	public void prepare() {
		view.setSelected(null);
	}
	public void finish() {
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent event) {
		Path<? extends Geometry> path = view.pickObject(event.getX(), event.getY(), new Filter<Geometry>() {
			@Override
			public boolean evaluate(Geometry object) {
				return object.getOwner() != null && object.getOwner().isSelectable();
			}
		});
		if (path != null) {
			if (path.target() instanceof Surface) {
				Surface surface = (Surface) path.target();
				if (surface.getExterior() != null) {
					CommandManager.instance().apply(new EraseSurface(surface));
					view.checkpoint();
					view.repaint();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseDragged(MouseEvent e) { }

	@Override
	public void mouseMoved(MouseEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) { }

}
