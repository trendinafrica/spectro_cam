import processing.serial.*;
Serial myPort;    // The serial port
int xPos = 1;     // horizontal position of the graph 
//Variables to draw a continuous line.
int lastxPos=1;
int lastheight=0;
float values[];
boolean donePlotting = true;
boolean haveData = false;
int displayWidth = 1280;
int displayHeight = 800;
void setup () {
 // set the window size:
 size(displayWidth, displayHeight);    
 // List all the available serial ports
 println(Serial.list());
 // Check the listed serial ports in your machine
 // and use the correct index number in Serial.list()[].
 println(Serial.list().length);
 myPort = new Serial(this, Serial.list()[2], 115200); //
 // A serialEvent() is generated when a newline character is received :
 myPort.bufferUntil('\n');
 background(0);   // set inital background:
}
void draw () {
 if (haveData) {
  xPos = 0;
  lastxPos = 0;
  donePlotting = false;
  background(0);
  for (int i = 0; i < values.length; i++) {
    if (Float.isNaN(values[i])) {
     println("NaN, skipping");
     continue;
    }
    values[i] *= 4; // scale up a bit
    values[i] = map(values[i], 0, 1023, 0, height); //map to the screen height.
 
    //Drawing a line from Last inByte to the new one.
    stroke(255,255,255);   //stroke color
    strokeWeight(4);    //stroke wider
    print(lastxPos); print(" "); print(lastheight); print(" "); println(height - values[i]);
    line(lastxPos, lastheight, xPos, height - values[i]); 
    g.drawLine(lastxPos, lastheight, xPos, height - values[i]);// in netbeans
    lastxPos= xPos;
    lastheight= int(height-values[i]);
    xPos += 10; // scale out a bit
  }
  donePlotting = true; 
  haveData = false;
 }
}
void serialEvent (Serial myPort) {
 if (donePlotting) {
 // get the ASCII string:
 String inString = myPort.readStringUntil('\n');
 if (inString != null) {
  inString = trim(inString);        // trim off whitespaces.
  values = float(split(inString, " "));
  haveData = true;
  println(values.length);
 }
 }
}
