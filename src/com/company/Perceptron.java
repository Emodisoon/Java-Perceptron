package com.company;

import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Perceptron {
    final int INPUTS_AMOUNT = 256;              //Кол-во входов
    final int OUTPUTS_AMOUNT = 6;               //Кол-во выходов
    int MIDDLE_LAYER_AMOUNT = 10;         //Количество элементов в среднем слое

    double learnSpeed = 0.08;             //Коэфициент скорости обучения

    int[] inputs;                               //Массив, содержащий значения входов
    double[] outputs, hiden;                    //Массивы выходов и среднего слоя
    double[][] wEH, wHO;                        //Массивы коээфициентов между 1-2 и 2-3 слоем

    double[] deltasHidden, deltasOutput;        //Дельты скрытого и выходного слоя
    double[] ddxHiden, ddxOutput;               //Производные активационной функции скрытого и выходного слоя

    double[] err;                               //Ошибка среднего слоя
    double gError;                              //Ошибка всей сети


    XYChart.Series<String, Double> errorValues; //Набор данных для построения графика


    public Perceptron(){
        //Инициализая массивов

        inputs = new int[INPUTS_AMOUNT];
        outputs = new double[OUTPUTS_AMOUNT];
        hiden = new double[MIDDLE_LAYER_AMOUNT];
        wEH = new double[INPUTS_AMOUNT][MIDDLE_LAYER_AMOUNT];
        wHO = new double[MIDDLE_LAYER_AMOUNT][OUTPUTS_AMOUNT];

        ddxHiden = new double[MIDDLE_LAYER_AMOUNT];
        ddxOutput = new double[OUTPUTS_AMOUNT];

        deltasOutput = new double[OUTPUTS_AMOUNT];
        deltasHidden = new double[MIDDLE_LAYER_AMOUNT];

        errorValues = new XYChart.Series<>();


        //Присвоение коэффициентам рандомного значения != 0
        for(int i=0;i<INPUTS_AMOUNT;i++)
            for(int j=0;j<MIDDLE_LAYER_AMOUNT;j++)
                wEH[i][j]=Math.random()*0.2+0.1;
        for(int i=0;i<MIDDLE_LAYER_AMOUNT;i++)
            for(int j=0;j<OUTPUTS_AMOUNT;j++)
                wHO[i][j]=Math.random()*0.2+0.1;
    }


    private void SubStudy(ArrayList<int[]> template, int[] answers) {//Метод обратного распространения ошибки и коррекции весовых коэффициентов
        for (int[] ints : template) {//Перебираем все шаблоны
            System.arraycopy(ints, 0, inputs, 0, INPUTS_AMOUNT);//Копируем массив из шаблона в массив входных нейронов
            Calculate();//Прямое распространение
            //Блок обратного распространения ошибки
            double[] lErr = new double[OUTPUTS_AMOUNT];
            for(int i=0;i<lErr.length;i++){
                lErr[i]=answers[i]-outputs[i];//Рассчет ошибки выходного слоя
                deltasOutput[i]=lErr[i]*ddxOutput[i];//Рассчет дельты выходного слоя
                gError += lErr[i]*lErr[i]/2;//Рассчет среднеквадратичного значения ошибки
            }

            for (int i = 0; i < MIDDLE_LAYER_AMOUNT; i++) { //Рассчет ошибки скрытого слоя путем умножения дельты выходного слоя на весовой коэффициент
                err[i] = 0;
                deltasHidden[i]=0;
                for(int j=0;j<OUTPUTS_AMOUNT;j++){
                    deltasHidden[i]+=deltasOutput[j]*wHO[i][j];//Дельта скрытого слоя равна дельте выходного слоя умноженного на весовой коэфициент
                    err[i]+=lErr[j]*wHO[i][j];
                }
                deltasHidden[i]*=ddxHiden[i];//Умножение дельты на производную активационной функции
            }
            //Конец блока обратного распространения ошибки
            //Блок коррекции весов
            for (int i = 0; i < INPUTS_AMOUNT; i++) {//Корркция весов между входным и скрытым слоем
                for (int j = 0; j < MIDDLE_LAYER_AMOUNT; j++) {
                    wEH[i][j] += learnSpeed * deltasHidden[j]*inputs[i];
                }
            }
            for (int i = 0; i < MIDDLE_LAYER_AMOUNT; i++) {//Корркция весов между скрытым и выходным слоем
                for(int j=0;j<OUTPUTS_AMOUNT;j++){
                    wHO[i][j] += learnSpeed * deltasOutput[j]*hiden[i];
                }
            }
        }
    }




    public void Study(){//Метод обучения нейросети методом обратного распространения ошибки
        err = new double[MIDDLE_LAYER_AMOUNT];//Создаем массив ошибок скрытого слоя
        int iteration=0;
        //Загружаем обучающие наборы данных из файлов
        ArrayList<int[]> templatesA = loadDataFromFile("a.txt");
        ArrayList<int[]> templatesB = loadDataFromFile("b.txt");
        ArrayList<int[]> templatesV = loadDataFromFile("v.txt");
        ArrayList<int[]> templatesG = loadDataFromFile("g.txt");
        ArrayList<int[]> templatesD = loadDataFromFile("d.txt");
        ArrayList<int[]> templatesE = loadDataFromFile("e.txt");
        //Создаем наборы ответов для обучающих данных
        int[]   answersA = {1,0,0,0,0,0},
                answersB = {0,1,0,0,0,0},
                answersV = {0,0,1,0,0,0},
                answersG = {0,0,0,1,0,0},
                answersD = {0,0,0,0,1,0},
                answersE = {0,0,0,0,0,1};
        do{
            gError=0;
            iteration++;
            //Поочереди передаем обучающие наборы данных в функцию обратного распространения ошибки и коррекции весовых коэффициентов
            SubStudy(templatesA, answersA);
            SubStudy(templatesB, answersB);
            SubStudy(templatesV, answersV);
            SubStudy(templatesG, answersG);
            SubStudy(templatesD, answersD);
            SubStudy(templatesE, answersE);

            errorValues.getData().add(new XYChart.Data<>(Integer.toString(iteration), gError));//Заполняем набор данных для построения графика

            System.out.println("Итерация: " + iteration);//Вывод информации о текущей операции и ошибке
            System.out.println("Ошибка: " + gError);
        }while(gError >=0.1);

    }


    public int StartCalculations(int [] data){//Публичный метод, для запуска начала прямого распространения
        System.arraycopy(data, 0, inputs,0,INPUTS_AMOUNT);//Копируем входные данные в входной слой
        Calculate();//Производим расчет выходного слоя
        softmax();//Вычисляем софтмакс выходного слоя
        System.out.println("Output:");
        for(int i=0;i<OUTPUTS_AMOUNT;i++)
            System.out.println(outputs[i]);//Выводим значения выходного слоя
        int maxIndex = 0;
        for(int i=0;i<OUTPUTS_AMOUNT;i++){//Находим номер нейрона с наибольшим значением
            if(outputs[i]>outputs[maxIndex])
                maxIndex=i;
        }
        return maxIndex;//Возвращаем номер нейрона с наибольшим значением
    }

    private void Calculate(){//Метод прямого распространения
        for(int i=0;i<MIDDLE_LAYER_AMOUNT;i++){//Распространяем сигнал на скрытый слой
            hiden[i]=0;
            for(int j=0;j<INPUTS_AMOUNT;j++){
                hiden[i]+=inputs[j]*wEH[j][i];//Вычисляем значение каждого нейрона скрытого слоя
            }
            hiden[i]=sigmoid(hiden[i]);//Вычисляем активационную фукнцию
            ddxHiden[i]=ddxsig(hiden[i]);//Вычисляем производноую активационной фукнции
        }
        for(int i=0;i<OUTPUTS_AMOUNT;i++){//Распространяем сигнал на выходно слой
            outputs[i]=0;
            for(int j=0;j<MIDDLE_LAYER_AMOUNT;j++) {
                outputs[i] += hiden[j] * wHO[j][i];//Вычисляем значение каждого нейрона выходного слоя
            }
            outputs[i]=sigmoid(outputs[i]);//Вычисляем активационную фукнцию
            ddxOutput[i]=ddxsig(outputs[i]);//Вычисляем производноую активационной фукнции
        }

    }

    private void softmax(){//Метод применяющий функцию softmax для выходного слоя
        double sum=0;
        for(int i=0;i<OUTPUTS_AMOUNT;i++){//Суммируем экспоненты выходного слоя
            sum+=Math.exp(outputs[i]);
        }
        for(int i=0;i<OUTPUTS_AMOUNT;i++){//Вычисляем софтмакс для каждого нейрона
            outputs[i]=Math.exp(outputs[i])/sum;
        }
    }

    private double ddxsig(double S){//Метод вычисляющий производную активационной функции
        return sigmoid(S)*(1-sigmoid(S));
    }

    private double sigmoid(double S){//Метод вычисляющий активационную функцию - сигмоиду
        return 1.0f/(1+Math.exp(-1.0*S));
    }

    private ArrayList<int[]> loadDataFromFile(String fileName){//Метод загружающий обучающие данные из файла
        ArrayList<int[]> data = new ArrayList<>();
        try{
            File file = new File(fileName);//Читаем файл
            FileReader fr= new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null){
                int[] tmp = new int[256];
                int tmpIndex = 0;

                for(int i=0;i<512;i++){
                    if(line.charAt(i)=='1' || line.charAt(i)=='0' ){//Если конкретный символ равен 1 или 0 значит это данные и заносим их в массив int[]
                        tmp[tmpIndex]=Character.getNumericValue(line.charAt(i));
                        tmpIndex++;
                    }
                }
                line = reader.readLine();
                data.add(tmp);//Добавляем массив в список
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public XYChart.Series<String, Double> getErrorValues(){//Метод возвращающий набор (итерация, ошибка) для построения графика
        return errorValues;
    }
}