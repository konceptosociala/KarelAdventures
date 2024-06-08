package org.konceptosociala.kareladventures.state;

import java.util.random.RandomGenerator;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.InvalidCellIdException;
import org.konceptosociala.kareladventures.ui.Logo;
import org.konceptosociala.kareladventures.ui.Margin;
import org.konceptosociala.kareladventures.ui.MsgBox;
import org.konceptosociala.kareladventures.ui.labyrinth.KarelDirection;
import org.konceptosociala.kareladventures.ui.labyrinth.Labyrinth;
import org.konceptosociala.kareladventures.ui.labyrinth.LabyrinthCell;
import org.konceptosociala.kareladventures.ui.labyrinth.LabyrinthCellId;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.Size;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import javafx.util.Pair;

public class KarelFarmState extends BaseAppState implements ScreenController {
    private final RandomGenerator rgen = RandomGenerator.getDefault();

    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;

    private LabyrinthCell[][] cells;
    private LabyrinthCell[][] initialCells;
    private boolean addedKarel;
    private boolean gameOver;
    private boolean won;
    private boolean msgBoxVisible;
    private KarelDirection direction;
    private int karelX;
    private int karelY;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
        rebuildCells();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        addedKarel = false;
        gameOver = false;
        won = false;
        msgBoxVisible = false;
        direction = KarelDirection.Right;

        nifty.addScreen("karel_farm_screen", new ScreenBuilder("Karel farm screen") {{
            controller(KarelFarmState.this);

            layer(new LayerBuilder("karel_farm_layer") {{
                childLayoutCenter();

                panel(new PanelBuilder("karel_farm_panel") {{
                    childLayoutVertical();

                    panel(Margin.vertical("10%"));
                    panel(new Logo("karel_farm_logo", "Interface/karel_farm_logo.png"));

                    panel(new PanelBuilder("karel_farm_controls_main_panel") {{
                        childLayoutCenter();     
                        align(Align.Center);
                        
                        image(new ImageBuilder("karel_farm_controls_bg") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-020.png");
                            imageMode("resize:22,4,22,22,22,4,22,4,22,4,22,22");
                            width("900px");
                            height("75%");
                            align(Align.Center);
                        }});

                        panel(new PanelBuilder("karel_farm_controls") {{
                            childLayoutHorizontal();
                            align(Align.Center);
                            width("900px");
                            height("75%");  

                            panel(new PanelBuilder("code_editor") {{
                                childLayoutCenter();
                                width("50%");
                                height("75%");  

                                panel(new PanelBuilder("code_editor_inner") {{
                                    childLayoutVertical();
                                    width("90%");
                                    height("90%");

                                    text(new TextBuilder("code_field_label") {{
                                        height("50px");
                                        width("100%");
                                        text("Code editor");
                                        color(Color.BLACK);
                                        font("Interface/Fonts/Ubuntu-C.ttf");
                                    }});
    
                                    text(new TextBuilder("code_field_history") {{
                                        height("100%");
                                        width("100%");
                                        color(Color.BLACK);
                                        backgroundColor(Color.WHITE);
                                        font("Interface/Fonts/FiraCode-Bold.ttf");
                                        textHAlign(Align.Left);
                                        textVAlign(VAlign.Top);
                                        align(Align.Left);
                                        valign(VAlign.Top);
                                    }});
    
                                    panel(Margin.vertical("10px"));
    
                                    control(new TextFieldBuilder("code_field") {{
                                        width("100%");
                                    }});
                                }});
                            }});

                            panel(new PanelBuilder("labyrinth") {{
                                childLayoutCenter();
                                width("50%");
                                height("75%");

                                panel(new PanelBuilder("labyrinth_inner") {{
                                    childLayoutVertical();
                                    width("90%");
                                    height("90%");  

                                    text(new TextBuilder("labyrinth_label") {{
                                        height("50px");
                                        width("100%");
                                        text("Karel Labyrinth");
                                        color(Color.BLACK);
                                        font("Interface/Fonts/Ubuntu-C.ttf");
                                    }});

                                    panel(new PanelBuilder("labyrinth_panel") {{
                                        childLayoutVertical();
                                        width("100%");
                                        height("100%");

                                        panel(new PanelBuilder("labyrinth_game") {{
                                            childLayoutCenter();
                                            width("100%h");
                                            height("85%h");
                                            align(Align.Center);

                                            image(new ImageBuilder("labyrinth_game_bg") {{
                                                filename("Interface/UI/Panel/panel-027.png");
                                                imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                                                width("100%");
                                                height("100%");
                                            }});

                                            panel(new Labyrinth("labyrinth_game_instance", cells));
                                        }});

                                        panel(new PanelBuilder("labyrinth_controls") {{
                                            childLayoutHorizontal();

                                            panel(new ImageButton("labyrinth_controls_reset", "Reset", new Size(150, 50), "resetLabyrinth()"));
                                            panel(new ImageButton("labyrinth_controls_rebuild", "Rebuild", new Size(150, 50), "rebuildLabyrinth()"));
                                        }});
                                    }});
                                }});
                            }});
                        }}); 
                    }});
                }});
            }});

            layer(new MsgBox("msgbox", "hideMsgBox()"));
        }}.build(nifty));

        nifty.gotoScreen("karel_farm_screen");
    }

    // UI callbacks

    @SuppressWarnings("null")
    public void resetLabyrinth() {
        if (msgBoxVisible || won) return;

        nifty
            .getScreen("karel_farm_screen")
            .findElementById("code_field_history")
            .getRenderer(TextRenderer.class)
            .setText("");

        copyCells(initialCells, cells);
 
        gameOver = false;
        direction = KarelDirection.Right;

        var pos = findInitialKarel();
        karelX = pos.getKey();
        karelY = pos.getValue();

        redrawLabyrinth();
    }

    
    @SuppressWarnings("null")
    public void rebuildLabyrinth() {
        if (msgBoxVisible) return;

        nifty
            .getScreen("karel_farm_screen")
            .findElementById("code_field_history")
            .getRenderer(TextRenderer.class)
            .setText("");

        rebuildCells();
        redrawLabyrinth();
    }

    @SuppressWarnings("null")
    public void enterCommand() {
        if (msgBoxVisible || gameOver || won) return;

        var textField = nifty
            .getScreen("karel_farm_screen")
            .findNiftyControl("code_field", TextField.class);

        var text = textField.getRealText().trim();
        
        if (!text.equals("")) {
            executeCommand(text);
        }

        textField.setText("");
    }

    @SuppressWarnings("null")
    public void showMsgBox(String title, String message) {
        nifty
            .getCurrentScreen()
            .findElementById("msgbox")
            .setVisible(true);

        msgBoxVisible = true;

        MsgBox.setData(nifty, "msgbox", title, message);
    }

    @SuppressWarnings("null")
    public void hideMsgBox() {
        nifty
            .getCurrentScreen()
            .findElementById("msgbox")
            .setVisible(false);

        msgBoxVisible = false;
    }

    // Commands

    @SuppressWarnings("null")
    private void executeCommand(String command) {
        switch (command) {
            case "move()" -> move();
            case "turnLeft()" -> turnLeft();
            case "turnRight()" -> turnRight();
            case "turnAround()" -> turnAround();
            case "pickBeeper()" -> pickBeeper();

            default -> {
                showMsgBox(
                    "Wrong command", 
                    "Command `"+command+"` is wrong. List of available commands: \n"
                    + "move() - move 1 step forward\n" 
                    + "turnLeft() - rotate Karel counter clockwise\n" 
                    + "turnRight() - rotate Karel clockwise\n" 
                    + "turnAround() - turn Karel 180 degrees\n" 
                    + "pickBeeper() - pick beeper to bag" 
                );

                return;
            }
        };

        var history = nifty
            .getScreen("karel_farm_screen")
            .findElementById("code_field_history")
            .getRenderer(TextRenderer.class);

        var historyText = history.getOriginalText();
        var newLineCount = historyText
            .split("\n")
            .length - 1;

        if (newLineCount >= 12) {
            historyText = historyText.replaceFirst("^.*\\n", "");
        }

        history.setText(historyText+"\n"+command);
    }

    private void move() {
        cells[karelX-1][karelY-1].setKarel(false);

        switch (direction) {
            case Up -> karelY--;
            case Down -> karelY++;
            case Left -> karelX--;
            case Right -> karelX++;
        }

        if (karelX <= 0 || karelY <= 0 || karelX > 4 || karelY > 4) {
            gameOver = true;
            showMsgBox("Game Over", "Karel has fallen beyond the map");
            return;
        }

        var newCell = cells[karelX-1][karelY-1];
        newCell.setKarel(true);
        newCell.setKarelDirection(direction);

        redrawLabyrinth();

        if (newCell.isBlocked()) {
            gameOver = true;
            showMsgBox("Game Over", "Karel has stacked in a wall");
            return;
        }
    }

    private void turnLeft() {
        direction = switch (direction) {
            case Up -> KarelDirection.Left;
            case Down -> KarelDirection.Right;
            case Left -> KarelDirection.Down;
            case Right -> KarelDirection.Up;
        };
        cells[karelX-1][karelY-1].setKarelDirection(direction);

        redrawLabyrinth();
    }

    private void turnRight() {
        direction = switch (direction) {
            case Up -> KarelDirection.Right;
            case Down -> KarelDirection.Left;
            case Left -> KarelDirection.Up;
            case Right -> KarelDirection.Down;
        };
        cells[karelX-1][karelY-1].setKarelDirection(direction);

        redrawLabyrinth();
    }

    private void turnAround() {
        direction = switch (direction) {
            case Up -> KarelDirection.Down;
            case Down -> KarelDirection.Up;
            case Left -> KarelDirection.Right;
            case Right -> KarelDirection.Left;
        };
        cells[karelX-1][karelY-1].setKarelDirection(direction);

        redrawLabyrinth();
    }
    
    private void pickBeeper() {
        var cell = cells[karelX-1][karelY-1];
        if (cell.isBeeper()) {
            cell.setBeeper(false);
            if (checkBeepers()) {
                victory();
                won = true;
                showMsgBox("Victory!", "Karel has collected all the beepers!");
                return;
            }
        } else {
            gameOver = true;
            showMsgBox("Game Over", "No beeper to pick up");
            return;
        }
    }

    // Other methods

    private boolean checkBeepers() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].isBeeper()) {
                    return false;
                }
            }
        }

        return true;
    }

    private Pair<Integer, Integer> findInitialKarel() {
        for (int i = 0; i < initialCells.length; i++) {
            for (int j = 0; j < initialCells[i].length; j++) {
                if (initialCells[i][j].isKarel()) {
                    return new Pair<>(i+1, j+1);
                }
            }
        }

        return null;
    }

    private void victory() {
        // TODO: make victory
    }

    @SuppressWarnings("null")
    private void redrawLabyrinth() {
        var labyrinth = nifty
            .getScreen("karel_farm_screen")
            .findElementById("labyrinth_game");

        var children = labyrinth.getChildren();
        children.get(children.size() - 1).markForRemoval();

        new Labyrinth("labyrinth_game_instance", cells).build(labyrinth);
    }

    private void rebuildCells() {
        addedKarel = false;
        gameOver = false;
        won = false;
        msgBoxVisible = false;
        direction = KarelDirection.Right;
        cells = new LabyrinthCell[4][4];
        initialCells = new LabyrinthCell[4][4];

        try {
            for (int i = 1; i <= 4; i++) {
                for (int j = 1; j <= 4; j++) {
                    boolean hasKarel = false;
                    boolean hasBeeper = false;
                    boolean blocked = false;

                    if (!addedKarel) {
                        hasKarel = randomBoolean();
                        if (hasKarel) {
                            addedKarel = true;
                            karelX = i;
                            karelY = j;
                        }
                    }

                    if (!hasKarel)
                        hasBeeper = randomBoolean();

                    if (!hasBeeper && !hasKarel)
                        blocked = rgen.nextBoolean();

                    cells[i-1][j-1] = new LabyrinthCell(
                        new LabyrinthCellId("lab_cell_"+i+"_"+j),
                        blocked,
                        hasKarel,
                        hasBeeper,
                        KarelDirection.Right
                    );
                }
            }
        } catch (InvalidCellIdException e) {
            e.printStackTrace();
        }

        copyCells(cells, initialCells);
    }

    private void copyCells(LabyrinthCell[][] from, LabyrinthCell[][] to) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                to[i][j] = from[i][j].clone();
            }
        }
    }

    private boolean randomBoolean() {
        boolean chance = true;
        int coef = rgen.nextInt(1, 4);
        for (int i = 0; i < coef; i++) {
            chance = chance && rgen.nextBoolean();
        }
        return chance;
    }  

    @Override
    protected void onDisable() {
        inputManager.setCursorVisible(false);
        nifty.gotoScreen("hud_screen");
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    @Override
    protected void cleanup(Application app) {
    }
}
