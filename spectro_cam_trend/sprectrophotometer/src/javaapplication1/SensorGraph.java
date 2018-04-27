/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javafx.scene.chart.Chart;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static javax.swing.Spring.height;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import processing.core.*;
import static processing.core.PApplet.map;
import static processing.core.PApplet.split;
import static processing.core.PApplet.trim;

/**
 *
 * @author Global Engineering
 */
public class SensorGraph {

    /**
     * @param args the command line arguments
     */
        static SerialPort chosenPort;
        static int x=0;
        static int xPos = 1;    
        static int lastxPos=1;
        static int lastheight=0;
        static float values[];
        static boolean donePlotting = true;
        static boolean haveData = true;
        static int width = 800;
        static int height = 600; 
// horizontal position of the graph 
//Variables to draw a continuous line.

    public static void main(String[] args) {

        //create and configure the window
        JFrame window =new JFrame();
        window.setTitle("Sensor Graph GUI");
        window.setSize(width, height);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //create a drop-down box and connect button, then  place then at the top of window
        JComboBox<String> portList = new JComboBox<>();
        JButton connectButton;
        connectButton = new JButton("Connect");
        JPanel topPanel = new JPanel();
        topPanel.add(portList);
        topPanel.add(connectButton);
        window.add(topPanel, BorderLayout.NORTH);
        
        //populate the drop-down box
        SerialPort[] portNames = SerialPort.getCommPorts();
        for (int i = 2; i < portNames.length; i++) {
            portList.addItem(portNames[i].getSystemPortName());
            
        //creare the line graph
            XYSeries series = new XYSeries("Light Sensor Readings");
//            
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);
            JFreeChart chart = ChartFactory.createXYLineChart("Light Sensor Readings", "time(seconds)", "ADC Reading", dataset,PlotOrientation.VERTICAL,true,false,true );
            ChartPanel chP = new ChartPanel(chart);
            window.add(chP,BorderLayout.CENTER);
            //show the window
        window.setVisible(true);
        
        
        
        
            // configure the connect button and use another thread to listen for data
//            connectButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent arg0) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        if(connectButton.getText().equals("Connect")) {
                            // attempt to connect to the serial port
                            chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
                            chosenPort.setBaudRate(115200);
                            chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                            if(chosenPort.openPort()){
                                connectButton.setText("Disconnect");
                                portList.setEnabled(false);
                            }
                            //create a new thread that listens for incoming text and populates the graph
                                Thread thread;
                            thread = new Thread(){
                                @Override public void run() {
                                    System.out.println("chanas1");
                                    try (Scanner scanner = new Scanner(chosenPort.getInputStream())) {
                                        while (scanner.hasNextLine()) {
//                                        System.out.println("2h");
//                                            try {
//                                            if (haveData) {
                                                xPos = 0;
                                                lastxPos = 0;
                                                donePlotting = false;
                                   //           background(0);
                                   series.getItemCount();
                                            System.out.println(series.getItemCount());
                                            String nextline= scanner.nextLine();
                                            
                                            String[] values= nextline.split(" ");
                                            
                                            System.out.println("=nombers of "+values.length);
                                            if (values.length!= 128){continue;}
                                                for (int i = 0; i < values.length; i++) {
                                                    
                                                int number=0;
                                                try {
                                                number= Integer.parseInt(values[i].trim());
                                                }catch (Exception e){System.err.println(e.getMessage());}
                                                number *= 4; // scale up a bit
//                                              System.out.println("    chanas    " + i);
                                                number = (int) map(number, 0, 1023, 0, height); //map to the screen height.
//                                              System.out.println(lastxPos+ "  "+ xPos+"   "+height); 
                                                series.add(i,number); 
                                                chP.repaint();
                                                chP.getChart().fireChartChanged();
                                                window.repaint();
                                                System.out.print((number)+" ");
                                                }//
                                                
                                                Thread.sleep(1000);
                                                series.clear();
                                                
                                                window.repaint();
                                                System.out.println("");
//                                                }

//                                            } catch (NumberFormatException e){}
                                        } 
                                    }catch (Exception e){System.out.println(e.getMessage());}
                                    
                                }
                            };
                            thread.start();
                        } else {
                            //disconnect from the serial port
                            chosenPort.closePort();
                            portList.setEnabled(true);
                            connectButton.setText("Connect");
                            series.clear();
                            x=0;
                        }
                }
//            });
        }
        
//    }
    
}
