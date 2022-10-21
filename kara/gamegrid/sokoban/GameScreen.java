package kara.gamegrid.sokoban;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JOptionPane;

import kara.gamegrid.Kara;
import kara.gamegrid.KaraWorld;
import kara.gamegrid.Leaf;
import kara.gamegrid.Mushroom;
import kara.gamegrid.Tree;

/**
 * This is the world for the Kara Sokoban game:
 * <p>
 * This class manages the following:
 * <ul>
 * <li>all information about the world and pixel sizes
 * <li>the Fonts
 * <li>all the screen states
 * <li>all the levels
 * <li>the current level and number of moves
 * <li>the highscore
 * <ul>
 * 
 * @author Marco Jakob (http://edu.makery.ch)
 */
@SuppressWarnings("serial")
public class GameScreen extends KaraWorld {
	// the screen settings
	public static final int WIDTH_IN_CELLS = 21;
	public static final int HEIGHT_IN_CELLS = 18;

	// fonts and colors
	public static final String FONT_NAME = "Tahoma";
	public static final Font FONT_S = new Font(FONT_NAME, Font.PLAIN, 12);
	public static final Font FONT_S_BOLD = new Font(FONT_NAME, Font.BOLD, 12);
	public static final Font FONT_M = new Font(FONT_NAME, Font.PLAIN, 17);
	public static final Font FONT_L = new Font(FONT_NAME, Font.PLAIN, 20);
	public static final Font FONT_XL = new Font(FONT_NAME, Font.PLAIN, 30);
	public static final Font FONT_XL_BOLD = new Font(FONT_NAME, Font.BOLD, 30);
	public static final Font FONT_XXL = new Font(FONT_NAME, Font.PLAIN, 50);

	// the screen states
	private ScreenState startState;
	private ScreenState enterNameState;
	private ScreenState levelSplashState;
	private ScreenState gameState;
	private ScreenState levelCompleteState;;
	private ScreenState gameCompleteState;
	private ScreenState highscoreState;

	private ScreenState state;

	private Level[] allLevels;
	private HighscoreManager highscoreManager;
	private int currentLevelNumber;
	private int numberOfMoves;
	private boolean levelComplete;
	
	private String levelFileName;
	private boolean developerMode = true;
	private boolean highscoreEnabled = false;

	/**
	 * Constructor for a game screen for the Sokoban game
	 * <i>Konstruktor fuer einen GameScreen fuer das Sokoban Spiel</i>
	 * 
	 * @param levelFileName the file containing the levels
	 * @param karaClass The class where Kara is programmed in.
	 */
	public GameScreen(String levelFileName, Class<? extends Kara> karaClass) {
		// Create a new world with the specified cells
		super(WIDTH_IN_CELLS, HEIGHT_IN_CELLS, karaClass);
		this.levelFileName = levelFileName;
		
		setTitle("Kara Sokoban");
		
		setPaintOrder(Label.class, KaraSokoban.class, Tree.class,
				Mushroom.class, Leaf.class);
	}
	
	@Override
	public void show() {
		// call prepare() before showing...
		prepare();
		
		super.show();
	}
	
	/**
	 * Initializes the GameScreen.
	 */
	protected void prepare() {
		if (developerMode) {
			setMouseDragAndDrop(MouseSettings.DISABLED_WHEN_RUNNING);
			setMouseContextMenu(MouseSettings.DISABLED_WHEN_RUNNING);
		} else {
			setMouseDragAndDrop(MouseSettings.DISABLED);
			setMouseContextMenu(MouseSettings.DISABLED);
		}
		
		// maximum speed for fast reaction
		setSimulationPeriod(0);

		// Read all the levels from the level file
		try {
			this.allLevels = Level.parseFromFile(levelFileName, karaClass);
			
			if (allLevels == null || allLevels.length == 0) {
				String message = "<html>" + "Could not load Levels from file: <p><i>" 
						+ "Konnte Levels nicht laden von der Datei: "
						+ "</i><p><p>" + levelFileName
						+ "<p><p>(A Level-file must contain at least one String \"Level:\")</html>";
				
				JOptionPane.showMessageDialog(null, message, "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			String message = "<html>" + "Could not find level file: <p><i>" 
					+ "Konnte die Level Datei nicht finden: "
					+ "</i><p><p>" + levelFileName + "</html>";
			
			JOptionPane.showMessageDialog(null, message, "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

		// init the screen states
		startState = new StartState(this);
		enterNameState = new EnterNameState(this);
		levelSplashState = new LevelSplashState(this);
		gameState = new GameState(this);
		levelCompleteState = new LevelCompleteState(this);
		gameCompleteState = new GameCompleteState(this);
		highscoreState = new HighscoreState(this);

		// init the level number and number of moves
		currentLevelNumber = 1;
		numberOfMoves = 0;

		// init the highscore manager
		if (highscoreEnabled) {
			// Tries to use the FileHighscore
			if (FileHighscore.isAvailable()) {
				highscoreManager = new FileHighscore();
				highscoreManager.initHighscores();
			}
		}

		// skip the start menu if in developer mode
		if (developerMode) {
			setState(gameState);
		} else {
			setState(startState);
		}
	}
	
    /**
	 * Sets and initializes the specified screen state. Before the new screen is
	 * initialized, all objects in the world are removed.
	 * 
	 * @param state
	 *            the new state of the screen
	 */
	protected void setState(ScreenState state) {
		setState(state, true);
	}

	/**
	 * Sets and initializes the specified screen state.
	 * 
	 * @param state
	 *            the new state of the screen
	 * @param clearWorld
	 *            if true, all objects in the world are removed before the new
	 *            state is initialized.
	 */
	protected void setState(ScreenState state, boolean clearWorld) {
		if (clearWorld) {
			// Remove all objects in the world
			removeAllActors();
			setBgImagePath(null);
		}
		this.state = state;
		state.initScreen();
	}

	/**
	 * Returns the start screen state.
	 */
	protected ScreenState getStartState() {
		return startState;
	}

	/**
	 * Returns the enter name screen state.
	 */
	protected ScreenState getEnterNameState() {
		return enterNameState;
	}

	/**
	 * Returns the level splash screen state.
	 */
	protected ScreenState getLevelSplashState() {
		return levelSplashState;
	}

	/**
	 * Returns the game screen state.
	 */
	protected ScreenState getGameState() {
		return gameState;
	}

	/**
	 * Returns the level complete screen state.
	 */
	protected ScreenState getLevelCompleteState() {
		return levelCompleteState;
	}

	/**
	 * Returns the game complete screen state.
	 */
	protected ScreenState getGameCompleteState() {
		return gameCompleteState;
	}

	/**
	 * Returns the highscore screen state.
	 */
	protected ScreenState getHighscoreState() {
		return highscoreState;
	}

	/**
	 * Removes the tiled background images and sets the bg color to black with
	 * no grid.
	 */
	protected void createBlackBackground() {
		clearFieldBackground();
		setBgColor(Color.BLACK);
	}

	/**
	 * Returns all the levels.
	 */
	protected Level[] getAllLevels() {
		return allLevels;
	}

	/**
	 * Returns the total number of levels.
	 */
	protected int getNumberOfLevels() {
		return allLevels.length;
	}

	/**
	 * Sets the level number for the current level.
	 */
	protected void setCurrentLevelNumber(int currentLevelNumber) {
		this.currentLevelNumber = currentLevelNumber;
	}

	/**
	 * Returns the level number for the current level.
	 */
	protected int getCurrentLevelNumber() {
		return currentLevelNumber;
	}

	/**
	 * Returns the current level or null, if levels could not be loaded.
	 */
	protected Level getCurrentLevel() {
		if (currentLevelNumber > 0 && currentLevelNumber - 1 < allLevels.length) {
			return allLevels[currentLevelNumber - 1];
		}
		return null;
	}

	/**
	 * Returns the level with the specified number.
	 */
	protected Level getLevel(int levelNumber) {
		if (levelNumber > 0 && levelNumber - 1 < allLevels.length) {
			return allLevels[levelNumber - 1];
		}
		return null;
	}

	/**
	 * Returns the number of moves that were made.
	 */
	protected int getNumberOfMoves() {
		return numberOfMoves;
	}

	/**
	 * Sets the number of moves.
	 */
	protected void setNumberOfMoves(int moves) {
		numberOfMoves = moves;
	}

	/**
	 * Returns true if the current level is complete.
	 */
	protected boolean isLevelComplete() {
		return levelComplete;
	}

	/**
	 * Sets whether the current level is complete.
	 */
	protected void setLevelComplete(boolean levelComplete) {
		this.levelComplete = levelComplete;
	}

	/**
	 * Returns whether the game is complete, i.e. the last level is completed.
	 * 
	 * @return true if the game is complete
	 */
	protected boolean isGameComplete() {
		return isLevelComplete()
				&& getCurrentLevelNumber() >= getNumberOfLevels();
	}

	/**
	 * Returns if the highscore is enabled and available.
	 */
	protected boolean isHighscoreAvailable() {
		return highscoreManager != null;
	}

	/**
	 * Returns if the highscore is enabled and available.
	 */
	protected boolean isHighscoreReadOnly() {
		if (highscoreManager == null) {
			return true;
		}

		return highscoreManager.isReadOnly();
	}

	/**
	 * Returns the name of the current player or empty String if none has been
	 * set.
	 */
	protected String getPlayerName() {
		if (highscoreManager == null) {
			return "";
		}
		return highscoreManager.getCurrentPlayerName();
	}

	/**
	 * Sets the name of the current player. (Will be ignored if ServerHighscore
	 * is used since the username of UserInfo is used).
	 */
	protected void setPlayerName(String playerName) {
		if (highscoreManager == null) {
			return;
		}
		highscoreManager.setCurrentPlayerName(playerName);
	}

	/**
	 * Returns true if the name of the current player can be set.
	 */
	protected boolean canSetPlayerName() {
		// Name of player can only be set if using the FileHighscore.
		return highscoreManager instanceof FileHighscore;
	}

	/**
	 * Returns the Highscore for the current level. May be null.
	 */
	protected Highscore getHighscoreForCurrentLevel() {
		return getHighscoreForLevel(currentLevelNumber);
	}

	/**
	 * Returns the Highscore for the specified level.
	 */
	protected Highscore getHighscoreForLevel(int levelNumber) {
		if (highscoreManager == null) {
			return null;
		}
		return highscoreManager.getHighscoreForLevel(levelNumber);
	}

	/**
	 * Sets the specified Highscore.
	 */
	protected void setHighscore(Highscore highscore) {
		highscoreManager.setHighscore(highscore);
	}

	/**
	 * The act method is called by the framework at each action step.
	 * The world's act method is called before the act method of any objects in
	 * the world.
	 * <p>
	 * Delegates act to the current state.
	 */
	public void act() {
		super.act();
		// Delegate to the current state
		state.act();
	}
	
	/**
	 * Creates an ASCII-representation of all the actors in the world.
	 * 
	 * @return the world as ASCII text
	 */
	protected String toASCIIText() {
		return Level.createFromActors(getActors(), 0, "XXXX").toASCIIText(false);
	}

	/** 
	 * Set to true to directly show the game (for testing and level design). Set to false for normal mode. <br>
	 * <i>Bei true wird direkt das Spielfeld angezeigt (zum Testen und Level Erstellen). Fuer Normaler Modus auf false setzen.</i>)
	 */
	public void setDeveloperMode(boolean developerMode) {
		this.developerMode = developerMode;
	}

	/**
	 * Set to true to enable the highscore. <br>
	 * <i>Wenn auf true gesetzt, dann wir die Highscore aktiviert.</i>
	 */
	public void setHighscoreEnabled(boolean highscoreEnabled) {
		this.highscoreEnabled = highscoreEnabled;
	}

	/**
	 * Returns true, if the highscore is enabled.
	 * @return
	 */
	public boolean isHighscoreEnabled() {
		return highscoreEnabled;
	}

	/**
	 * Helper method to add a label to the world.
	 * 
	 * @param label
	 *            the label that should be added to the world.
	 * @param x
	 *            the x position of the actor in the grid.
	 * @param y
	 *            the y position of the actor in the grid.
	 */
	public void addObject(Label label, int x, int y) {
		label.addToWorld(x, y);
	}
	
	/**
	 * Gets the most recently pressed key <br>
	 * <i>Ermittelt die zuletzt gedrueckte Taste</i>.
	 * <p>
	 * 
	 * <ul>
	 * <li>"a", "b", .., "z" (alphabetical keys), "0".."9" (digits), most punctuation marks. 
	 * 		Also returns uppercase characters when appropriate.</li>
	 * <li>"up", "down", "left", "right" (the cursor keys)</li>
	 * <li>"enter", "space", "tab", "escape", "backspace", "shift", "control"</li>
	 * <li>"F1", "F2", .., "F12" (the function keys)</li>
	 * </ul>
	 * 
	 * @return the most recently pressed key as String or an empty String if no key was pressed.
	 */
	public String getKey() {
		// Rises visibility of getKey() from protected to public
		return super.getKey();
	}
}
