package com.bprocessor.ui;

import java.awt.FileDialog;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bprocessor.Sketch;
import com.bprocessor.SketchInfo;
import com.bprocessor.SketchInfoList;
import com.bprocessor.io.ModelClient;
import com.bprocessor.io.Persistence;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SketchController {
	private List<Sketch> sketches;
	private Sketch activeSketch;
	private SketchObserver observer;
	private String path;

	public SketchController(SketchObserver observer, String path) {
		this.observer = observer;
		sketches = new LinkedList<Sketch>();
		this.path = path;
		ObjectMapper mapper = new ObjectMapper();
		try {
			SketchInfoList sketchInfoList = mapper.readValue(new File(path), SketchInfoList.class);
			for (SketchInfo info : sketchInfoList.getInfos()) {
				Sketch sketch = Persistence.load(new File(info.getPath()));
				sketch.setPath(info.getPath());
				if (sketch != null) {
					sketches.add(sketch);
				}
			}
			if (sketches.size() > 0) {
				activeSketch = sketches.get(0);
			}
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
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
	public void setActiveSketch(Sketch sketch) {
		this.activeSketch = sketch;
	}

	public void changed(Object initiator) {
		if (observer != null) {
			observer.sketchChanged(initiator);
		}
		System.out.println("-- writing --");
		SketchInfoList infos = new SketchInfoList("workspace");
		for (Sketch current : sketches) {
			if (current.getPath() != null) {
				SketchInfo info = new SketchInfo(current.getId(), current.getName(), current.getPath());
				infos.getInfos().add(info);
				System.out.println("  " + info.getPath());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(path), infos);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
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

	public Sketch findById(int id) {
		for (Sketch current : sketches) {
			if (current.getUid() == id) {
				return current;
			}
		}
		return null;
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
		activeSketch = new Sketch("untitled");
		add(activeSketch);
	}
	public boolean isOpenEnabled() {
		return true;
	}
	public void userOpen(JFrame parent) {
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
			int index = sketches.indexOf(activeSketch);
			remove(activeSketch);
			if (index > (sketches.size() - 1)) {
				index = (sketches.size() - 1);
			}
			if (index >= 0 && index < sketches.size()) {
				activeSketch = sketches.get(index);
			} else {
				activeSketch = null;
			}
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

	public boolean isUploadEnabled() {
		return (activeSketch != null);
	}
	public void userUpload(JFrame parent) {
		ModelClient client = new ModelClient();
		try {
			if (activeSketch.getUid() == 0) {
				client.save(activeSketch);
				activeSketch.setModified(true);
			} else {
				client.update(activeSketch.getUid(), activeSketch);
			}
			changed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean isDeleteEnabled() {
		return (activeSketch != null && activeSketch.getUid() != 0);
	}
	public void userDelete(JFrame parent) {
		ModelClient client = new ModelClient();
		try {
			client.delete(activeSketch.getUid());
			activeSketch.setUid(0);
			activeSketch.setModified(true);
			changed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isDownloadEnabled() {
		return (activeSketch != null && activeSketch.getUid() != 0);
	}
	public void userDownload(JFrame parent) {
		ModelClient client = new ModelClient();
		try {
			Sketch sketch = client.get(activeSketch.getUid());
			if (sketch != null) {
				activeSketch.setGroup(sketch.getGroup());
				activeSketch.setModified(true);
				changed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isDownloadAllEnabled() {
		return true;
	}
	public void userDownloadAll(JFrame parent) {
		ModelClient client = new ModelClient();
		try {
			List<Sketch> lst = client.getAll();
			if (lst != null) {
				for (Sketch current : lst) {
					System.out.println("checking " + current.getUid());
					Sketch sketch = findById(current.getUid());
					if (sketch != null) {
						sketch.setGroup(current.getGroup());
						sketch.setUid(current.getUid());
					} else {
						current.setPath("sketches/" + current.getName() + "-" + current.getUid() + ".bps");
						Persistence.save(current, current.getPath());
						sketches.add(current);
					}
				}
				if (lst.size() > 0) {
					activeSketch = lst.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
