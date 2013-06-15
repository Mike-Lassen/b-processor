package com.bprocessor.ui;

import java.awt.FileDialog;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bprocessor.Sketch;
import com.bprocessor.io.Persistence;

public class SketchController {
	private List<Sketch> sketches;
	private Sketch activeSketch;
	private SketchObserver observer;

	public SketchController(SketchObserver observer) {
		this.observer = observer;
		sketches = new LinkedList<Sketch>();
	}

	public void add(Sketch sketch) {
		sketches.add(sketch);
	}
	public void remove(Sketch sketch) {
		sketches.remove(sketch);
	}
	public List<Sketch> getSketches() {
		return sketches;
	}
	public Sketch getActiveSketch() {
		return activeSketch;
	}
	public void setActiveSketchSketch(Sketch sketch) {
		this.activeSketch = sketch;
	}
	
	public void changed(Object initiator) {
		if (observer != null) {
			observer.sketchChanged(initiator);
		}
	}
	public void changed() {
		changed(this);
	}
	
	public String title() {
		if (activeSketch != null) {
			if (activeSketch.getPath() != null) {
				return "B-Processor Ð " + activeSketch.getPath();
			} else {
				return "B-Processor Ð Untitled";
			}
		} else {
			return "B-Processor";
		}
	}

	private int changesShouldBeSaved(JFrame parent) {
		final Object[] options = { "Save", "Discard", "Cancel" };

		return JOptionPane.showOptionDialog(parent,
				"Save changes before closing?",
				"Close",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
	}


	public boolean isNewEnabled() {
		return true;
	}
	public void userNew(JFrame parent) {
		if (userClose(parent)) {
			activeSketch = new Sketch("untitled");
			add(activeSketch);
		}
	}

	public boolean isOpenEnabled() {
		return true;
	}

	public void userOpen(JFrame parent) {
		if (userClose(parent)) {
			FileDialog dialog = new FileDialog(parent);
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
					activeSketch = theSketch;
					add(activeSketch);
				}
			}
		}
	}

	public boolean isCloseEnabled() {
		return activeSketch != null;
	}
	public boolean userClose(JFrame parent) {
		if (activeSketch != null) {
			if (activeSketch.isModified()) {
				int result = changesShouldBeSaved(parent);
				if (result == JOptionPane.YES_OPTION) {
					if (!userSave(parent)) {
						return false;
					}
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return false;
				}
			}
			remove(activeSketch);
			activeSketch = null;
		}
		return true;
	}

	public boolean isSaveEnabled() {
		if (activeSketch != null) {
			return activeSketch.isModified();
		} else {
			return false;
		}
	}
	public boolean userSave(JFrame parent) {
		if (activeSketch.isModified()) {
			if (activeSketch.getPath() != null) {
				Persistence.save(activeSketch, activeSketch.getPath());
				activeSketch.setModified(false);
				return true;
			} else {
				return userSaveAs(parent);
			}
		} else {
			return true;
		}
	}

	public boolean isSaveAsEnabled() {
		return activeSketch != null;
	}
	public boolean userSaveAs(JFrame parent) {
		FileDialog dialog = new FileDialog(parent, "Save", FileDialog.SAVE);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
		if (dialog.getFile() != null) {
			File file = new File(dialog.getDirectory(), dialog.getFile());
			activeSketch.setPath(file.getAbsolutePath() + ".bps");
			activeSketch.setName(file.getName());
			Persistence.save(activeSketch, activeSketch.getPath());
			activeSketch.setModified(false);
			return true;
		} else {
			return false;
		}
	}

}
