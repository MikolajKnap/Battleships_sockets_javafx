package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ClientPlaceshipsController {

    @FXML
    private Canvas gameCanvas;
    public Label label_shipSize;

    private final int gridSize = 10;
    private double cellSize;

    private int currentShipSize;
    private int remainingShipsOfSize4;
    private int remainingShipsOfSize3;
    private int remainingShipsOfSize2;
    private int remainingShipsOfSize1;

    private ArrayList<ArrayList<String>> arrayOfPositions;
    private ArrayList<String> takenPositions;

    @FXML
    public void initialize() {
        // Inicjalizacja rozmiaru komórki po uzyskaniu szerokości canvas
        cellSize = gameCanvas.getWidth() / gridSize;

        // Rysowanie planszy po uzyskaniu szerokości canvas
        drawGrid();

        // Ustawienie obsługi zdarzeń kliknięcia myszą po uzyskaniu canvas
        setMouseClickEvent();

        // Inicjalizacja zmiennych do śledzenia statków
        currentShipSize = 4;
        remainingShipsOfSize4 = 1;
        remainingShipsOfSize3 = 2;
        remainingShipsOfSize2 = 3;
        remainingShipsOfSize1 = 4;

        label_shipSize.setText(String.format("%d",currentShipSize));
        arrayOfPositions = new ArrayList<>();
        takenPositions = new ArrayList<>();
    }

    private void drawGrid() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        for (int i = 0; i <= gridSize; i++) {
            double x = i * cellSize;
            double y = i * cellSize;

            // Rysowanie linii pionowych
            gc.strokeLine(x, 0, x, gameCanvas.getHeight());

            // Rysowanie linii poziomych
            gc.strokeLine(0, y, gameCanvas.getWidth(), y);
        }
    }

    private void setMouseClickEvent() {
        gameCanvas.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY(), event.getButton()));
    }

    private void handleMouseClick(double x, double y, MouseButton mouseButton) {
        if(currentShipSize > 0){
            // Obliczenie indeksu komórki planszy na podstawie współrzędnych kliknięcia
            int columnIndex = (int) (x / cellSize);
            int rowIndex = (int) (y / cellSize);

            // Rysowanie statku
            if(drawShip(columnIndex, rowIndex, currentShipSize, mouseButton)){
                // Zmniejszenie liczby pozostałych statków danego rozmiaru
                updateRemainingShips();

                // Jeżeli wszystkie statki danego rozmiaru zostały umieszczone, przejdź do następnego rozmiaru
                if (remainingShipsOfSize4 == 0 || remainingShipsOfSize3 == 0 ||
                        remainingShipsOfSize2 == 0) {
                    switchToNextShipSize();
                    label_shipSize.setText(String.format("%d",currentShipSize));
                }
                if(remainingShipsOfSize1 == 0){
                    switchToNextShipSize();
                    label_shipSize.setText("Waiting for second player");
                    gameCanvas.setVisible(false);
                    ClientSocketConnection.sendArrayListArrString(arrayOfPositions);
                    ClientSocketConnection.setOwnBoard(arrayOfPositions);

                    CompletableFuture<String> future = CompletableFuture.supplyAsync(ClientSocketConnection::readMessage);
                    future.thenAccept(result -> Platform.runLater(() -> {
                        if(result.equals("GAME_PHASE")){
                            ClientSocketConnection.sendMessage("ACK");
                            ClientGUISettings.initializeNewWindow("client-playgame.fxml","GAME", label_shipSize);                }
                    }));
                }
            }
            else{
                ClientGUISettings.showAlert("Wrong place!");
            }
        }

    }


    private boolean drawShip(int columnIndex, int rowIndex, int shipSize, MouseButton mouseButton) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double x = columnIndex * cellSize;
        double y = rowIndex * cellSize;

        // Ustawienie koloru na przykład czerwony
        gc.setFill(Color.rgb(100,200,200));

        // Rysowanie statku na planszy
        if(mouseButton.equals(MouseButton.PRIMARY)){
            if(columnIndex <= 10 - shipSize){

                for(int i = 0; i < shipSize; i++){
                    int finalI = i;
                    if(
                            takenPositions.
                                    stream().
                                    anyMatch(posI -> posI.equals(String.format("%d%d",rowIndex,columnIndex + finalI)))
                    ){
                        return false;
                    }
                }

                ArrayList<String> tempArr = new ArrayList<>();
                for(int i = 0; i < shipSize; i++){
                    String pos = String.format("%d%d",rowIndex,columnIndex + i);
                    gc.fillRect(x, y, cellSize * shipSize, cellSize);

                    tempArr.add(pos);

                    String takenSpotDown = String.format("%d%d",rowIndex+1,columnIndex + i);
                    String takenSpotUp = String.format("%d%d",rowIndex-1,columnIndex + i);
                    takenPositions.add(takenSpotDown);
                    takenPositions.add(takenSpotUp);

                    takenPositions.add(pos);

                }
                arrayOfPositions.add(tempArr);

                String takenSpotLeft = String.format("%d%d",rowIndex,columnIndex-1);
                String takenSpotRight = String.format("%d%d",rowIndex,columnIndex+shipSize);
                takenPositions.add(takenSpotLeft);
                takenPositions.add(takenSpotRight);

                System.out.println(takenPositions.toString());

                return true;
            }
        }
        else{
            if(rowIndex <= 10 - shipSize){

                for(int i = 0; i < shipSize; i++){
                    int finalI = i;
                    if(
                            takenPositions.
                                    stream().
                                    anyMatch(posI -> posI.equals(String.format("%d%d",rowIndex + finalI,columnIndex)))
                    ){
                        return false;
                    }
                }

                ArrayList<String> tempArr = new ArrayList<>();
                for(int i = 0; i < shipSize; i++){
                    String pos = String.format("%d%d",rowIndex + i,columnIndex);
                    gc.fillRect(x, y, cellSize, cellSize * shipSize);

                    tempArr.add(pos);

                    String takenSpotLeft = String.format("%d%d",rowIndex + i,columnIndex - 1);
                    String takenSpotRight = String.format("%d%d",rowIndex + i,columnIndex + 1);
                    takenPositions.add(takenSpotLeft);
                    takenPositions.add(takenSpotRight);

                    takenPositions.add(pos);
                }
                arrayOfPositions.add(tempArr);

                String takenSpotDown = String.format("%d%d",rowIndex+shipSize,columnIndex);
                String takenSpotUp = String.format("%d%d",rowIndex-1,columnIndex);
                takenPositions.add(takenSpotDown);
                takenPositions.add(takenSpotUp);

                System.out.println(takenPositions.toString());

                return true;
            }
        }
        return false;
    }

    private void updateRemainingShips() {
        switch (currentShipSize) {
            case 4:
                remainingShipsOfSize4 -= 1;
                break;
            case 3:
                remainingShipsOfSize3 -= 1;
                break;
            case 2:
                remainingShipsOfSize2 -= 1;
                break;
            case 1:
                remainingShipsOfSize1 -= 1;
                break;
        }
    }

    private void switchToNextShipSize() {
        // Przełączenie na kolejny rozmiar statku
        currentShipSize -= 1;

        // Resetowanie liczby pozostałych statków danego rozmiaru
        switch (currentShipSize+1) {
            case 4:
                remainingShipsOfSize4 = 1;
                break;
            case 3:
                remainingShipsOfSize3 = 2;
                break;
            case 2:
                remainingShipsOfSize2 = 3;
                break;
            case 1:
                remainingShipsOfSize1 = 4;
                break;
        }
    }

}
