package com.nsu.cse215l.redlolli.redlolli.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;

/**
 * Factory for building the game's static UI scenes (death, victory, item-found, level transition).
 * Provides shared button styling and resource loading utilities.
 */
public class SceneFactory {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 730;

    // ========================= TEXT SCENES =========================

    /** Creates the death screen scene. */
    public static Scene createDeathScene(String activeDeathMessage, int deathCount,
            Runnable onRestart, Runnable onMainMenu) {
        VBox layout = newBlackVBox(14);

        Text title = new Text("YOU DIED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 70));
        title.setFill(Color.DARKRED);
        layout.getChildren().add(title);

        String[] deathLines = {
                "pale luna smiles wide", "there is no escape",
                "pale luna smiles wide", "no more lollies to take",
                "pale luna smiles wide", "now you are dead"
        };
        for (String line : deathLines) {
            layout.getChildren().add(styledText(line, "Serif", 21, Color.LIGHTGRAY));
        }

        if (activeDeathMessage != null && !activeDeathMessage.isBlank()) {
            layout.getChildren().add(styledText(activeDeathMessage, "Serif", FontWeight.BOLD, 18, Color.rgb(190, 130, 130)));
        }
        if (deathCount >= 5) {
            layout.getChildren().add(styledText("You keep coming back. She likes that.", "Serif", FontWeight.BOLD, 18, Color.rgb(200, 70, 70)));
        }

        Button restartBtn = createStyledButton("RESTART FROM LEVEL 1");
        restartBtn.setOnAction(e -> onRestart.run());
        Button menuBtn = createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> onMainMenu.run());
        layout.getChildren().addAll(new Text(""), restartBtn, menuBtn);
        return new Scene(layout, WIDTH, HEIGHT);
    }

    /** Creates the victory screen scene. */
    public static Scene createVictoryScene(Runnable onMainMenu) {
        VBox layout = newBlackVBox(14);

        Text title = new Text("YOU ESCAPED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 60));
        title.setFill(Color.LIMEGREEN);
        layout.getChildren().add(title);

        String[] victoryLines = {
                "pale luna smiles wide,", "the ground is soft,",
                "pale luna smiles wide,", "there is a hole,",
                "pale luna smiles wide,", "tie her up with rope,",
                "congratulations! you have escaped from pale luna"
        };
        for (String line : victoryLines) {
            boolean isFinal = line.startsWith("congratulations");
            layout.getChildren().add(styledText(line, "Serif",
                    isFinal ? FontWeight.BOLD : FontWeight.NORMAL,
                    isFinal ? 24 : 20,
                    isFinal ? Color.GOLD : Color.LIGHTGRAY));
        }

        Button menuBtn = createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> onMainMenu.run());
        layout.getChildren().add(menuBtn);
        return new Scene(layout, WIDTH, HEIGHT);
    }

    /** Creates the item-found screen scene. */
    public static Scene createItemFoundScene(int level, String[] itemFoundMainText,
            String[] itemFoundButtonText, Runnable onContinue) {
        VBox layout = newBlackVBox(25);

        layout.getChildren().add(styledText("pale luna smiles wide...", "Serif", FontWeight.BOLD, 28, Color.rgb(120, 0, 0)));

        Text mainText = new Text(itemFoundMainText[level - 1]);
        mainText.setTextAlignment(TextAlignment.CENTER);
        mainText.setFont(Font.font("Serif", FontWeight.BOLD, 65));
        mainText.setFill(Color.DARKRED);

        String[] itemDescriptions = {
                "The earth was soft that night. Too soft. Like it was waiting for her.",
                "The blade bit into the ground. Each scoop made a sound like breathing.",
                "She did not struggle at the end. Her eyes were wide open. Smiling."
        };
        Text descText = new Text(itemDescriptions[level - 1]);
        descText.setWrappingWidth(700);
        descText.setTextAlignment(TextAlignment.CENTER);
        descText.setFont(Font.font("Serif", 19));
        descText.setFill(Color.rgb(170, 150, 145));

        Button continueBtn = new Button(itemFoundButtonText[level - 1]);
        continueBtn.setFont(Font.font("Serif", FontWeight.BOLD, 28));
        String normalStyle = "-fx-background-color: #1a0000; -fx-text-fill: #cc0000; -fx-border-color: #660000; -fx-border-width: 2px; -fx-padding: 12 50;";
        String hoverStyle = "-fx-background-color: #330000; -fx-text-fill: #ff3333; -fx-border-color: #990000; -fx-border-width: 2px; -fx-padding: 12 50;";
        continueBtn.setStyle(normalStyle);
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(hoverStyle));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(normalStyle));
        continueBtn.setOnAction(e -> onContinue.run());

        layout.getChildren().addAll(mainText, descText, new Text(""), continueBtn);
        return new Scene(layout, WIDTH, HEIGHT);
    }

    /** Creates the level transition newspaper screen. */
    public static Scene createLevelTransitionScene(int currentLevel, Runnable onContinue) {
        VBox layout = newBlackVBox(20);
        String[] headlines = {
                "CENTRAL PARK DIG SITE RETURNS TO NEWS",
                "BASEMENT SKETCHES FOUND IN COLD CASE",
                "ROPE RECOVERED. HUNTER UNSEEN."
        };
        layout.getChildren().add(styledText("NEWSPAPER CLIPPING", "Serif", FontWeight.BOLD, 36, Color.rgb(170, 170, 170)));
        layout.getChildren().add(styledText(headlines[Math.min(currentLevel - 1, headlines.length - 1)], "Serif", FontWeight.BOLD, 24, Color.rgb(170, 60, 60)));
        Button next = createStyledButton("CONTINUE");
        next.setOnAction(e -> onContinue.run());
        layout.getChildren().add(next);
        return new Scene(layout, WIDTH, HEIGHT);
    }

    // ========================= SHARED UTILITIES =========================

    /** Creates a standard styled game menu button. */
    public static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        String normalStyle = "-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-border-color: darkred; -fx-border-width: 1px; -fx-padding: 10 40;";
        String hoverStyle = "-fx-background-color: #330000; -fx-text-fill: white; -fx-border-color: red; -fx-border-width: 1px; -fx-padding: 10 40;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    /** Loads an image from the classpath resource path, returning null on failure. */
    public static Image tryLoadImage(String path) {
        try {
            URL url = SceneFactory.class.getResource(path);
            if (url != null) return new Image(url.toExternalForm());
        } catch (Exception ignored) {}
        return null;
    }

    // ========================= PRIVATE HELPERS =========================

    private static VBox newBlackVBox(int spacing) {
        VBox layout = new VBox(spacing);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");
        return layout;
    }

    private static Text styledText(String content, String fontFamily, int fontSize, Color color) {
        return styledText(content, fontFamily, FontWeight.NORMAL, fontSize, color);
    }

    private static Text styledText(String content, String fontFamily, FontWeight weight, int fontSize, Color color) {
        Text t = new Text(content);
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFont(Font.font(fontFamily, weight, fontSize));
        t.setFill(color);
        return t;
    }
}
