import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build
 * a GUI for the Big Two card game and handle all user actions.
 */
public class BigTwoGUI implements CardGameUI {
	private BigTwo game = null; // a BigTwo object
	private BigTwoClient client = null; // a BigTwoClient Object
	private ArrayList<CardGamePlayer> playerList; // the list of players
	private ArrayList<Hand> handsOnTable; // the list of hands played on the
	private int activePlayer = -1; // the index of the active player
	private boolean[] selected = new boolean[BigTwo.MAX_CARD_NUM]; // selected cards
	// gui elements
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem connectMenuItem;
	private JMenuItem quitMenuItem;
	private BigTwoPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JScrollPane msgAreaScrollPane;
	private JTextArea chatArea;
	private JScrollPane chatAreaScrollPane;
	private JTextField chatInput;

	/**
	 * a constructor for creating a BigTwoGUI. The parameter game is a reference to
	 * a Big Two card game associated with this GUI.
	 * 
	 * @param game
	 */
	public BigTwoGUI(BigTwo game) {
		this.game = game;
		playerList = game.getPlayerList();
		handsOnTable = game.getHandsOnTable();

		// frame
		frame = new JFrame("Ng Tsz Hin 3035855571's ASM5");
		frame.setSize(960, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		// menuBar
		menuBar = new JMenuBar();
		menu = new JMenu("Game");
		connectMenuItem = new JMenuItem("Connect");
		quitMenuItem = new JMenuItem("Quit");
		menu.add(connectMenuItem);
		menu.add(quitMenuItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);

		// bigTwoPanel (class BigTwoPanel is defined at the end of this file, where the
		// listener logic is located at too)
		bigTwoPanel = new BigTwoPanel();
		c.weightx = 1;
		c.weighty = 15;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 2;
		frame.add(bigTwoPanel, c);

		// msgArea
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		msgArea.setColumns(25);
		msgArea.setRows(1);
		msgAreaScrollPane = new JScrollPane(msgArea);
		// scroll to bottom on append
		((DefaultCaret) msgArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		frame.add(msgAreaScrollPane, c);

		// chatArea
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setColumns(25);
		chatArea.setRows(1);
		chatAreaScrollPane = new JScrollPane(chatArea);
		// scroll to bottom on append
		((DefaultCaret) chatArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 2;
		c.gridy = 1;
		frame.add(chatAreaScrollPane, c);

		// playButton
		playButton = new JButton("Play");
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 2;
		frame.add(playButton, c);

		// passButton
		passButton = new JButton("Pass");
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 2;
		frame.add(passButton, c);

		// chatInput
		chatInput = new JTextField("chat here (press Enter to send)");
		chatInput.setColumns(25);
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 2;
		c.gridy = 2;
		frame.add(chatInput, c);

		// add listener for resizing window
		frame.addComponentListener(new ComponentAdapter() {
			// fix Java bug: maximize window does not call componentResized
			@Override
			public void componentMoved(ComponentEvent e) {
				componentResized(e);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (frame.getWidth() > 1000) {
					msgArea.setColumns(25);
					chatArea.setColumns(25);
					chatInput.setColumns(25);
				} else if (frame.getWidth() <= 1000 && frame.getWidth() > 800) {
					msgArea.setColumns(20);
					chatArea.setColumns(20);
					chatInput.setColumns(20);
				} else if (frame.getWidth() <= 800 && frame.getWidth() > 600) {
					msgArea.setColumns(15);
					chatArea.setColumns(15);
					chatInput.setColumns(15);
				} else if (frame.getWidth() <= 600) {
					msgArea.setColumns(10);
					chatArea.setColumns(10);
					chatInput.setColumns(10);
				}
			}
		});

		// add listener for connect menu item
		connectMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.connect();
			}
		});

		// add listener for quit menu item
		quitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// add listener for clicking play
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] cardIdx;
				cardIdx = getSelected();
				// check if player selected any cards
				if (cardIdx != null) {
					resetSelected();
					client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
				}
			}
		});

		// add listener for clicking pass
		passButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetSelected();
				client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, null));
			}
		});

		// add listener for sending chat
		chatInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!chatInput.getText().trim().isEmpty()) {
						client.sendMessage(new CardGameMessage(CardGameMessage.MSG, client.getPlayerID(),
								chatInput.getText().trim()));
						chatInput.setText("");
					} else
						chatInput.setText("");
				}
			}
		});
		disable();
		frame.setVisible(true);
	}

	/**
	 * pass the client to the gui
	 * 
	 * @param client
	 */
	public void setClient(BigTwoClient client) {
		this.client = client;
	}

	/**
	 * a method for setting the index of the active player (i.e., the player having
	 * control of the GUI).
	 * 
	 * @param id of the active player
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
	}

	/**
	 * a method for repainting the GUI.
	 */
	public void repaint() {
		frame.repaint();
	}

	/**
	 * a method for printing the specified string to the message area of the GUI.
	 * 
	 * @param message to display
	 */
	public void printMsg(String msg) {
		msgArea.append(msg + "\n");
	}

	/**
	 * a method for clearing the message area of the GUI.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}

	/**
	 * a method for printing the specified string to the chat area of the GUI.
	 * 
	 * @param chat to display
	 */
	public void printChat(String msg) {
		chatArea.append(msg + "\n");
	}

	/**
	 * a method for resetting the GUI. It will (i) reset the list of selected cards;
	 * (ii) clear the message area; and (iii) enable user interactions.
	 */
	public void reset() {
		clearMsgArea();
		resetSelected();
		repaint();
	}

	/**
	 * a method for enabling user interactions with the GUI. It will (i) enable the
	 * “Play” button and “Pass” button (i.e., making them clickable); and (ii)
	 * enable the BigTwoPanel for selection of cards through mouse clicks.
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}

	/**
	 * a method for disabling user interactions with the GUI. You should (i) disable
	 * the “Play” button and “Pass” button (i.e., making them not clickable); and
	 * (ii) disable the BigTwoPanel for selection of cards through mouse clicks.
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}

	/**
	 * a method for prompting the active player to select cards and make his/her
	 * move. A message should be displayed in the message area showing it is the
	 * active player’s turn.
	 */
	public void promptActivePlayer() {
		printMsg(playerList.get(activePlayer).getName() + "'s turn: ");
		if (client.getPlayerID() == activePlayer)
			enable();
		else
			disable();
		repaint();
	}

	private int[] getSelected() {
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}

	private void resetSelected() {
		for (int j = 0; j < selected.length; j++) {
			selected[j] = false;
		}
	}

	private class BigTwoPanel extends JPanel {
		private int width;
		private int height;
		private int imageheight;
		private int[] playery = new int[4];
		private int tabley;
		private Image image;
		private int cardx;
		private int cardwidth;

		public BigTwoPanel() {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {

					int x = e.getX();
					int y = e.getY();
					updateValues();
					int i;
					// detect click on overlapping cards
					for (i = 0; i < playerList.get(client.getPlayerID()).getCardsInHand().size(); i++) {
						if (x >= cardx && x < cardx + cardwidth / 2
								&& y >= (selected[i] ? playery[client.getPlayerID()] + 7
										: playery[client.getPlayerID()] + 15)
								&& y < (selected[i] ? playery[client.getPlayerID()] + 7 + imageheight
										: playery[client.getPlayerID()] + 15 + imageheight)) {
							selected[i] = !selected[i];
						}
						cardx += cardwidth / 2;
					}
					// detect click on the remaining half of last card
					if (x >= cardx && x < cardx + cardwidth / 2
							&& y >= (selected[i - 1] ? playery[client.getPlayerID()] + 7
									: playery[client.getPlayerID()] + 15)
							&& y < (selected[i - 1] ? playery[client.getPlayerID()] + 7 + imageheight
									: playery[client.getPlayerID()] + 15 + imageheight)) {
						selected[i - 1] = !selected[i - 1];
					}

					repaint();
				}
			});
		}

		public void updateValues() {
			width = this.getWidth();
			height = this.getHeight();
			imageheight = (int) ((double) (height) / 5) - 20;
			for (int i = 0; i < BigTwo.MAX_PLAYER_NUM; i++)
				playery[i] = (int) ((double) (height) / 5 * i);
			tabley = (int) ((double) (height) / 5 * 4);
			cardx = 15 + imageheight;
			cardwidth = Math.min((int) (((double) imageheight) / 97 * 73),
					(int) (((double) (width - cardx)) / ((double) BigTwo.MAX_CARD_NUM / 2 + 0.5)));
		}

		public static Image composeCardImage(Card c, boolean faceup) {
			if (!faceup)
				return new ImageIcon("src/image/b.gif").getImage();
			char rankToChar[] = { 'a', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'j', 'q', 'k' };
			char suitToChar[] = { 'd', 'c', 'h', 's' };
			String s = "";
			s += rankToChar[c.getRank()];
			s += suitToChar[c.getSuit()];
			s += ".gif";
			return new ImageIcon("src/image/" + s).getImage();
		}

		@Override
		public void paintComponent(Graphics g) {
			updateValues();

			// set background
			g.setColor(new Color(0, 173, 0));
			g.fillRect(0, 0, width, height);

			// for each player
			for (int i = 0; i < BigTwo.MAX_PLAYER_NUM; i++) {
				// players who have joined
				if (playerList.get(i).getName() != null) {
					// draw name
					g.setColor(new Color(0, 0, 0));
					if (activePlayer == i)
						g.setFont(new Font("monospace", Font.BOLD, 12));
					else
						g.setFont(new Font("monospace", Font.PLAIN, 12));
					if (client.getPlayerID() == i)
						g.drawString(playerList.get(i).getName() + " (You)", 5, playery[i] + 10);
					else
						g.drawString(playerList.get(i).getName(), 5, playery[i] + 10);
					// draw icon
					image = new ImageIcon("src/image/player" + Integer.toString(i) + ".jpg").getImage();
					g.drawImage(image, 5, playery[i] + 15, imageheight, imageheight, this);
					// draw cards
					if (game.getNumOfPlayers() == BigTwo.MAX_PLAYER_NUM) {
						cardx = 15 + imageheight;
						for (int j = 0; j < playerList.get(i).getCardsInHand().size(); j++) {
							image = composeCardImage(playerList.get(i).getCardsInHand().getCard(j),
									client.getPlayerID() == i);
							g.drawImage(image, cardx,
									client.getPlayerID() == i && selected[j] ? playery[i] + 7 : playery[i] + 15,
									cardwidth, imageheight, this);
							cardx += cardwidth / 2;
						}
					}
				}
			}

			// table
			g.setFont(new Font("monospace", Font.PLAIN, 12));
			g.setColor(new Color(0, 0, 0));
			if (!handsOnTable.isEmpty()) {
				g.drawString("Table: played by " + handsOnTable.get(handsOnTable.size() - 1).getPlayer().getName(), 5,
						tabley + 10);
				cardx = 5;
				for (int j = 0; j < handsOnTable.get(handsOnTable.size() - 1).size(); j++) {
					image = composeCardImage(handsOnTable.get(handsOnTable.size() - 1).getCard(j), true);
					g.drawImage(image, cardx, tabley + 15, cardwidth, imageheight, this);
					cardx += cardwidth / 2;
				}
			} else
				g.drawString("Table: Empty", 5, tabley + 10);

		}
	}
}
