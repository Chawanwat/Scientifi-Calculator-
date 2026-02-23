import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {

    // ✅ ต้องอยู่ระดับ class (ห้ามอยู่ใน start)
    private Button createBtn(String text) {
        Button b = new Button(text);
        b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        b.setStyle("-fx-background-color: #F8C8C4; -fx-text-fill: black;");
        String baseStyle = b.getStyle();

        b.setOnMouseEntered(e ->
                b.setStyle(baseStyle + "-fx-opacity: 0.85;")
        );

        b.setOnMouseExited(e ->
                b.setStyle(baseStyle)
        );
        return b;
    }

    @Override
    public void start(Stage stage) {

        TextField display = new TextField();
        display.setPromptText("0");
        display.setPrefHeight(65);
        display.setEditable(false);
        display.setBackground(new Background(
                new BackgroundFill(Color.MISTYROSE, new CornerRadii(8), Insets.EMPTY)
        ));
        display.setMaxWidth(Double.MAX_VALUE);

        GridPane keypad = new GridPane();
        keypad.setHgap(8);
        keypad.setVgap(8);
        keypad.setAlignment(Pos.CENTER);

        VBox root = new VBox(15);
        root.setPadding(new Insets(16));
        root.getChildren().addAll(display, keypad);

        // ✅ ให้ keypad ขยายตาม VBox (ถึงแม้คุณ setResizable(false) ก็ยังจัดสัดส่วนดี)
        VBox.setVgrow(keypad, Priority.ALWAYS);

        // =========================
        // ✅ กำหนด "6 คอลัมน์" ให้เท่ากัน
        // =========================
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 6);
            col.setHgrow(Priority.ALWAYS);
            keypad.getColumnConstraints().add(col);
        }


        for (int i = 0; i < 7; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 7);
            row.setVgrow(Priority.ALWAYS);
            keypad.getRowConstraints().add(row);
        }

        // =========================
        // ✅ แถวบน: ปุ่ม scientific เท่ากัน + DEL ใหญ่ (กิน 2 ช่อง)
        // layout:  sin cos tan ( ) DEL(2ช่อง)
        // col:     0   1   2  3 4 5- (del span 2 ไม่ได้ใน 6 col)
        //
        // 👉 วิธีแก้ที่ถูก: ให้ DEL อยู่ col=4 span=2 และย้าย ) ไป col=3
        // จะได้ใช้ col 0..5 พอดี
        // =========================
        keypad.add(createBtn("sin"), 0, 0);
        keypad.add(createBtn("cos"), 1, 0);
        keypad.add(createBtn("tan"), 2, 0);
        keypad.add(createBtn("("),   3, 0);
        keypad.add(createBtn(")"), 4, 0);

        // DEL กิน 2 ช่อง: col=4 และ col=5
        keypad.add(createBtn("DEL"), 5, 0, 2, 1);

        // ใส่ ) ไปแถวถัดไปเพื่อไม่ให้แน่นเกิน (หรือสลับตำแหน่งได้)
        keypad.add(createBtn("π"),0,1);
        keypad.add(createBtn("e"),1,1);
        keypad.add(createBtn("1/x"),2,1);
        keypad.add(createBtn("x²"), 3, 1);
        keypad.add(createBtn("xʸ"), 4, 1);
        keypad.add(createBtn("√"),  5, 1);
        keypad.add(createBtn("n!"),  6, 1);

        keypad.add(createBtn("ln"), 0, 2);
        keypad.add(createBtn("log"),1, 2);
        keypad.add(createBtn("%"),2, 2);
        keypad.add(createBtn("+/-"),3, 2);

        keypad.add(createBtn("Clear"), 4, 2, 3, 1);

        // =========================
        // ✅ ตัวเลข 7-9 / 4-6 / 1-3 (ให้กินแค่ 4 คอลัมน์ซ้าย)
        // ที่เหลือ 2 คอลัมน์ขวา เอาไว้ใส่ operator ให้สูงเท่ากัน
        // =========================
        String[][] nums = {
                {"7","8","9"},
                {"4","5","6"},
                {"1","2","3"}
        };

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                keypad.add(createBtn(nums[r][c]), c, r + 4); // col 0..2, row 3..5
            }
        }

        // operator ขวา (2 คอลัมน์) ให้ดูสมดุล
        keypad.add(createBtn("+"), 4, 6 );
        keypad.add(createBtn("-"), 4, 5);
        keypad.add(createBtn("÷"), 4, 4);
        keypad.add(createBtn("abs"), 6, 4);
        keypad.add(createBtn("×"), 5, 4);
        keypad.add(createBtn("0"), 3, 4, 1, 2);
        keypad.add(createBtn("."), 3, 6);
        keypad.add(createBtn("Enter"), 5, 5, 2, 2);
        RowConstraints row3 = new RowConstraints();
        row3.setPrefHeight(5);   // ความสูงที่ต้องการ
        row3.setMinHeight(5);
        row3.setMaxHeight(5);

        keypad.getRowConstraints().set(3, row3);

        Scene scene = new Scene(root, 380, 520);
        stage.setTitle("Scientific Calculator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        ScientificCalculator calc = new ScientificCalculator();

        Background backg = new Background(
                new BackgroundFill(
                        Color.web("#FFF7F6"),
                        CornerRadii.EMPTY,
                        Insets.EMPTY));

        root.setBackground(backg);

        for (var node : keypad.getChildren()) {
            if (node instanceof Button b) {
                b.setOnAction(e -> {
                    String t = b.getText();
                    try {
                        if (t.equals("Enter")) {
                            display.setText(calc.evaluate());
                        } else {
                            calc.input(t);
                            display.setText(calc.getExpression().isEmpty() ? "0" : calc.getExpression());
                        }
                    } catch (Exception ex) {
                        display.setText("Error");
                        calc.clear();
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
