package com.github.marcelothebuilder.jbeatbox.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.marcelothebuilder.jbeatbox.BeatBoxNetworkMessage;
import com.github.marcelothebuilder.jbeatbox.Messages;
import com.github.marcelothebuilder.jbeatbox.client.BeatBoxTrackBeat;
import com.github.marcelothebuilder.jbeatbox.client.network.NetworkClient;
import com.github.marcelothebuilder.jbeatbox.client.network.NetworkMessageListener;

public class BeatBoxGui extends JFrame implements NetworkMessageListener {
	private static final long serialVersionUID = 1L;
	private JPanel bgPanel, checkBoxPanel, namesPanel;
	private Box buttonBox;
	private JButton startBtn, stopBtn, tempoUpBtn, tempoDownBtn;
	private ArrayList<JCheckBox> checkBoxList;
	private Sequencer midiSequencer;
	private Sequence midiSequence;
	private Track midiTrack;

	// serialize
	private JButton saveButton, restoreButton;

	// networking
	private Box networkPanel;
	private JButton sendButton;
	private JScrollPane scrollNetworkBeats;
	private JTextField textNetworkMessage;
	private JList listNetworkBeats;

	private String[] instrumentNames = { Messages.getString("BeatBoxGui.0"), Messages.getString("BeatBoxGui.1"), Messages.getString("BeatBoxGui.2"), Messages.getString("BeatBoxGui.3"), Messages.getString("BeatBoxGui.4"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			Messages.getString("BeatBoxGui.5"), Messages.getString("BeatBoxGui.6"), Messages.getString("BeatBoxGui.7"), Messages.getString("BeatBoxGui.8"), Messages.getString("BeatBoxGui.9"), Messages.getString("BeatBoxGui.10"), Messages.getString("BeatBoxGui.11"), Messages.getString("BeatBoxGui.12"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			Messages.getString("BeatBoxGui.13"), Messages.getString("BeatBoxGui.14"), Messages.getString("BeatBoxGui.15") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	int[] instruments = { 35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63 };

	public BeatBoxGui() {
		super(Messages.getString("BeatBoxGui.16")); //$NON-NLS-1$
		setBounds(1920 / 2 - 300 / 2, 1080 / 2 - 300 / 2, 300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUpMidi();
		buildElements();
		buildTrackAndStart();
	}

	public void buildElements() {
		// JFrame thisFrame = this;
		BorderLayout layout = new BorderLayout();

		bgPanel = new JPanel(layout);
		bgPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // aesthetic

		GridLayout checkBoxLayout = new GridLayout(16, 16);

		checkBoxPanel = new JPanel(checkBoxLayout);
		checkBoxList = new ArrayList<JCheckBox>();
		for (int i = 0; i < 256; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBoxList.add(checkBox);
			checkBoxPanel.add(checkBox);
		}

		GridLayout namesLayout = new GridLayout(16, 1);

		namesPanel = new JPanel(namesLayout);
		for (String name : instrumentNames)
			namesPanel.add(new JLabel(name));

		buttonBox = new Box(BoxLayout.Y_AXIS);

		startBtn = new JButton(Messages.getString("BeatBoxGui.17")); //$NON-NLS-1$
		startBtn.addActionListener(new StartButtonListener());

		stopBtn = new JButton(Messages.getString("BeatBoxGui.18")); //$NON-NLS-1$
		stopBtn.addActionListener(new StopButtonListener());

		tempoUpBtn = new JButton(Messages.getString("BeatBoxGui.19")); //$NON-NLS-1$
		tempoUpBtn.addActionListener(new TempoUpButtonListener());

		tempoDownBtn = new JButton(Messages.getString("BeatBoxGui.20")); //$NON-NLS-1$
		tempoDownBtn.addActionListener(new TempoDownButtonListener());

		saveButton = new JButton(Messages.getString("BeatBoxGui.21")); //$NON-NLS-1$
		saveButton.addActionListener(new SaveButtonListener(this));

		restoreButton = new JButton(Messages.getString("BeatBoxGui.22")); //$NON-NLS-1$
		restoreButton.addActionListener(new RestoreButtonListener(this));

		buttonBox.add(startBtn);
		buttonBox.add(stopBtn);
		buttonBox.add(tempoUpBtn);
		buttonBox.add(tempoDownBtn);
		buttonBox.add(saveButton);
		buttonBox.add(restoreButton);

		bgPanel.add(namesPanel, BorderLayout.WEST);
		bgPanel.add(checkBoxPanel, BorderLayout.CENTER);
		bgPanel.add(buttonBox, BorderLayout.EAST);

		getContentPane().add(bgPanel);
	}

	public void addNetClient(final NetworkClient net) {
		//this.net = net;
		networkPanel = new Box(BoxLayout.Y_AXIS);
		networkPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		sendButton = new JButton(Messages.getString("BeatBoxGui.23")); //$NON-NLS-1$
		new JTextArea(20, 20);
		textNetworkMessage = new JTextField(20);

		BeatBoxMessageListModel<BeatBoxMessageListItem> listModel = new BeatBoxMessageListModel<>();
		
		listNetworkBeats = new JList<BeatBoxMessageListModel<BeatBoxMessageListItem>>(listModel);

		listNetworkBeats.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					JList source = (JList) e.getSource();
					BeatBoxMessageListItem li = (BeatBoxMessageListItem) source.getSelectedValue();
					parsePatternBoolArray(li.getBeat().getPattern());

				}
			}
		});
		listNetworkBeats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollNetworkBeats = new JScrollPane(listNetworkBeats);
		
		String[] names = {"Carl", "Jackson", "Milla", "Kika", "Jady", "Alice", "Turtle", "Monky"};
		Arrays.sort(names);
		
		final String name = names[ new Random().nextInt(names.length) ];
		
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BeatBoxNetworkMessage msg = new BeatBoxNetworkMessage(name, textNetworkMessage.getText(), //$NON-NLS-1$
						buildPatternBoolArray());
				net.sendNetworkMessage(msg);
			}
		});

		networkPanel.add(scrollNetworkBeats);
		networkPanel.add(textNetworkMessage);
		// networkPanel.add( listNetworkBeats );
		networkPanel.add(sendButton);

		buttonBox.add(networkPanel);

		showApp();
	}

	public void messageReceived(BeatBoxNetworkMessage beat) {
		assert(beat != null);
		BeatBoxMessageListModel<BeatBoxMessageListItem> model = (BeatBoxMessageListModel<BeatBoxMessageListItem>) listNetworkBeats
				.getModel();
		model.add(new BeatBoxMessageListItem(beat));
		// textNetworkBeats.append(beat.getSender());
	}

	public void setUpMidi() {
		try {
			midiSequencer = MidiSystem.getSequencer();
			midiSequencer.open();
			midiSequencer.setTempoInBPM(120);

			midiSequence = new Sequence(Sequence.PPQ, 4);
			midiTrack = midiSequence.createTrack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void buildTrackAndStart() {
		midiSequence.deleteTrack(midiTrack);
		midiTrack = midiSequence.createTrack();

		for (int i = 0; i < 16; i++) {
			int key = instruments[i];
			ArrayList<BeatBoxTrackBeat> list = new ArrayList<BeatBoxTrackBeat>();

			for (int j = 0; j < 16; j++) {
				// System.out.println("Selecting checkbox at "+j+" and "+i);
				JCheckBox jc = checkBoxList.get(j + (16 * i));
				if (jc.isSelected()) {
					// System.out.println("message "+key+" and "+i+" and "+j);
					list.add(new BeatBoxTrackBeat(key, j));
				}
			}

			makeTrack(list);

			try {
				makeMidiEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 0, 16);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// after every track
		try {
			makeMidiEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, 15);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			midiSequencer.setSequence(midiSequence);
			midiSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			midiSequencer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void makeTrack(ArrayList<BeatBoxTrackBeat> list) {
		for (BeatBoxTrackBeat beat : list) {
			makeNoteEvent(beat);
		}
	}

	public void makeNoteEvent(BeatBoxTrackBeat beat) {
		try {
			// System.out.println(beat);
			midiTrack.add(makeMidiEvent(ShortMessage.NOTE_ON, 9, beat.getKey(), 100, beat.getBeat()));
			midiTrack.add(makeMidiEvent(ShortMessage.NOTE_OFF, 9, beat.getKey(), 100, beat.getBeat() + 1));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected MidiEvent makeMidiEvent(int command, int channel, int one, int two, int tick)
			throws InvalidMidiDataException {

		ShortMessage sm = new ShortMessage();
		sm.setMessage(command, channel, one, two);
		MidiEvent event = new MidiEvent(sm, tick);

		return event;
	}

	private void parsePatternBoolArray(Boolean[] boolArray) {
		if (confirmPatternReplacement()) {
			for (int i = 0; i < 256; i++) {
				JCheckBox check = checkBoxList.get(i);
				check.setSelected(boolArray[i]);
			}
		}
	}

	private Boolean confirmPatternReplacement() {
		int userOption = JOptionPane.showConfirmDialog(this, Messages.getString("BeatBoxGui.25")); //$NON-NLS-1$

		if (userOption == JOptionPane.YES_OPTION)
			return true;
		else
			return false;
	}

	private Boolean[] buildPatternBoolArray() {
		Boolean[] boolArray = new Boolean[256];
		for (int i = 0; i < 256; i++) {
			JCheckBox cb = checkBoxList.get(i);
			boolArray[i] = cb.isSelected();
		}
		return boolArray;
	}

	public void showApp() {
		pack();
		setVisible(true);
	}

	public class StopButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			midiSequencer.stop();
		}
	}

	public class StartButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			buildTrackAndStart();
		}
	} // close inner class

	public class TempoUpButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = midiSequencer.getTempoFactor();
			midiSequencer.setTempoFactor((float) (tempoFactor * 1.03));
		}
	}

	public class TempoDownButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = midiSequencer.getTempoFactor();
			midiSequencer.setTempoFactor((float) (tempoFactor * 0.97));
		}
	}

	public class SaveButtonListener implements ActionListener {
		private JFrame dialogParent;

		SaveButtonListener(JFrame dialogParent) {
			this.dialogParent = dialogParent;
		}

		public void actionPerformed(ActionEvent e) {
			Boolean[] boolArray = buildPatternBoolArray();

			JFileChooser chooser = new JFileChooser();
			chooser.showSaveDialog(dialogParent);

			File selectedFile = chooser.getSelectedFile();

			if (selectedFile != null) {
				try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
					stream.writeObject(boolArray);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(-1);
				}

			}
		}
	}

	public class RestoreButtonListener implements ActionListener {
		private JFrame dialogParent;

		RestoreButtonListener(JFrame dialogParent) {
			this.dialogParent = dialogParent;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.showOpenDialog(dialogParent);
			File selectedFile = chooser.getSelectedFile();

			if (selectedFile != null) {
				try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(selectedFile))) {
					Boolean[] boolArray = (Boolean[]) stream.readObject();
					parsePatternBoolArray(boolArray);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}

}