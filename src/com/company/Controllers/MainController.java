package com.company.Controllers;

import com.company.Perceptron;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.*;

public class MainController {

    @FXML
    private Canvas inputCanvas;
    @FXML
    private Label status;
    @FXML
    private TextField hiddenAmount;
    @FXML
    private TextField coef;
    @FXML
    private AreaChart<String, Double> graphic;

    private GraphicsContext graphicsContext;

    private boolean isGraphic = false;

    private Perceptron perceptron;

    @FXML
    private void initialize() {
        graphicsContext = inputCanvas.getGraphicsContext2D();
        clear();//Очистка экрана

        status.setText("Нейросеть не обучена");

//        perceptron = new Perceptron();
//
        graphic.setTitle("График ошибки");

        //При нажатии мыши рисуем квадрат в сетке 16x16 пикселей размером 10 пикселей
        inputCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                graphicsContext.fillRect(Math.floor(event.getX() / 10) * 10, Math.floor(event.getY() / 10) * 10, 10, 10));
        // При движении мыши рисуем квадрат в сетке 16x16 пикселей размером 10 пикселей
        inputCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                graphicsContext.fillRect(Math.floor(event.getX() / 10) * 10, Math.floor(event.getY() / 10) * 10, 10, 10));

    }
    //Обработка нажатия кнопки сохранения шаблона буквы Д
    @FXML
    private void saveD(){
        saveData("d.txt");
        clear();
    }

    //Обработка нажатия кнопки сохранения шаблона буквы Д
    @FXML
    private void saveA(){
        saveData("a.txt");
        clear();
    }

    @FXML
    private void saveB(){
        saveData("b.txt");
        clear();
    }
    @FXML
    private void saveV(){
        saveData("v.txt");
        clear();
    }
    @FXML
    private void saveG(){
        saveData("g.txt");
        clear();
    }
    @FXML
    private void saveE(){
        saveData("e.txt");
        clear();
    }

    @FXML
    private void saveJ(){

    }

    @FXML
    private void calculate(){
        int [] data = getDataFromCanvas();
        int result = perceptron.StartCalculations(data);
        String[] results = {"Буква А", "Буква Б", "Буква В", "Буква Г", "Буква Д", "Буква Е"};
        status.setText(results[result]);
    }

    //Метод для загрузки данных
    @FXML
    private void loadData() {

        perceptron = new Perceptron();

        perceptron.Study();

        status.setText("Нейросеть обучена");

        if(isGraphic)
            return;
        isGraphic = true;
        XYChart.Series<String, Double> errorValues = perceptron.getErrorValues();
        errorValues.setName("Ошибка");
        graphic.getData().addAll(errorValues);
    }
    //Метод сохранения данных в файл
    private void saveData(String file){
        //Получаем биты изображения
        int[] bits;
        bits=getDataFromCanvas();
        //Преобразуем в текст и сохраняем в файл
        try {
            PrintWriter writer = new PrintWriter((new FileWriter(file, true)));
            for(int i = 0;i<256;i++){
                writer.print(bits[i] +" ");
            }
            writer.println("");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Метод получения данных с канвас и преобразования их в одномерный массив
    private int[] getDataFromCanvas(){
        WritableImage wim = new WritableImage(160,160);
        SnapshotParameters parameters = new SnapshotParameters();
        WritableImage snapshot = inputCanvas.snapshot(parameters,wim);//Делаем снэпшот канваса
        PixelReader px = snapshot.getPixelReader();//Создаем объект px для чтения данных снэпшота
        int[] imageBits = new int[256];
        int tmp = 0;
        for(int i=0;i<16;i++){//Попиксельно анализируем изображение и заносим данные в выходной массив
            for(int j=0; j<16; j++){
                Color newColor = px.getColor(j*10+5,i*10+5);//Изображение 160x160, но для нейронки пойдут только 16х16, так что берем данные из середины нарисованных квадратов 10x10
                if(newColor.equals(Color.WHITE))
                    imageBits[tmp]=0;
                if(newColor.equals(Color.BLACK))
                    imageBits[tmp]=1;
                tmp++;
            }
        }
        return imageBits;
    }

    //Метод для очистки канваса
    @FXML
    private void clear() {
        graphicsContext.setFill(Color.WHITE);//Задать цвет кисти белый
        graphicsContext.fillRect(0,0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());//заливка канваса белым цветом
        graphicsContext.setFill(Color.BLACK);//Возвращение цвета кисти на черный
    }
}

