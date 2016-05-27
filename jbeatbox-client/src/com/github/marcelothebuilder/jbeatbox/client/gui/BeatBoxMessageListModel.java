package com.github.marcelothebuilder.jbeatbox.client.gui;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

public class BeatBoxMessageListModel<T extends BeatBoxMessageListItem> extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private ArrayList<T> list = new ArrayList<T>();

	public T getElementAt(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	public void add(T beat) {
		list.add(beat);
		fireIntervalAdded(this, getSize(), getSize());
	}

}