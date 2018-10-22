#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>


/*********** Also include Pixel controller LED***********/
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

/************************* WiFi Access Point *********************************/

#define WLAN_SSID       "FRITZ!Box DuerrleWG"
#define WLAN_PASS       "DuerrleWG39"

/************************* Adafruit.io Setup MQTT *********************************/

#define MQTT_SERVER     "192.168.178.44"
#define DEVICE_NAME      "pat"




WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
char msg[50];
int value = 0;

/************************* LED Setup *********************************/
#define PIN             5       //Pin connected to the Data port
#define NUMPIXELS       150     //Number of Pixels in LED strip
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);


/*************************** Sketch Code ************************************/

// Bug workaround for Arduino 1.6.6, it seems to need a function declaration
// for some reason (only affects ESP8266, likely an arduino-builder bug).
void MQTT_connect();

void setup() {
  Serial.begin(115200);
  delay(10);
  Serial.println("NodeMCU started");


  // LED setup//////////////////////////////////////////////////
  // This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  pixels.begin(); // This initializes the NeoPixel library.
  Serial.println("LEDs setup");

  // MQTT //////////////////////////////////////////////////////
  Serial.println(F("Connecting to Wifi"));

  // Connect to WiFi access point.
  Serial.println(); Serial.println();
  Serial.print("Connecting to ");
  Serial.println(WLAN_SSID);

  WiFi.begin(WLAN_SSID, WLAN_PASS);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  Serial.println("WiFi connected");
  Serial.println("IP address: "); 
  Serial.println(WiFi.localIP());

  // Setup MQTT subscription for onoff feed.
  //mqtt.subscribe(&led_update);
  client.setServer(MQTT_SERVER, 1883);
  client.setCallback(callback);
  Serial.println("Initialization done");
  
}

uint32_t x=0;

//0 = off
//1 = static
//2 = rainbow
uint8_t ledMode = 0;
uint8_t modeState = 10;
uint8_t effectDelay = 1;
uint8_t staticR = 0;
uint8_t staticB = 0;
uint8_t staticG = 0;
//Stard and End pixel for the things set
double lastStart = 0;
double lastEnd = 1;

//for kitt light decay
uint8_t ledRedness[NUMPIXELS];
uint16_t leadLedKitt = 0;
//kit Length between 1 and 255
uint8_t kittLength = 230;

uint8_t speedMs = 30;

void loop() {
  // Ensure the connection to the MQTT server is alive (this will make the first
  // connection and automatically reconnect when disconnected).  See the MQTT_connect
  // function definition further below.
  if (!client.connected()) {
    reconnect();
    delay(5000);
  } else { 
  client.loop();

  uint16_t startLedReal = 1.0 * lastStart * NUMPIXELS;
  uint16_t endLedReal = 1.0 * lastEnd * NUMPIXELS;

  if (ledMode == 1){
     for(int i=startLedReal;i<endLedReal;i++){
        pixels.setPixelColor(i, pixels.Color(staticR,staticG,staticB));    
     }
     pixels.show();
  } else if (ledMode == 2){
      rainbowUpdate(modeState++ % 255, startLedReal, endLedReal);
  } else if (ledMode == 3){
      kittUpdateV3(modeState++ % 255, startLedReal, endLedReal);
  }

  delay(speedMs);
  }
}



void reconnect() {
  // Loop until we're reconnected
  //while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect("ESP8266Client")) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish("update/kitchen/window/led", "ESP EA3C35C connected!");
      // ... and resubscribe
      client.subscribe("output/kitchen/window/led");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      setup();
      
    }
  
}

 
void callback(char* topic, byte* payload, unsigned int length) {
  
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  if (strcmp(topic, "output/kitchen/window/led") == 0){
    Serial.println("Received update");  

    DynamicJsonBuffer jsonBuffer;
    JsonObject& root = jsonBuffer.parseObject((char *)payload);
      if (root.success()){
          Serial.println("Successfully parsed JSON");

          JsonObject& ledData = root["led"];

          //get LEDs
          JsonObject& leds = ledData["range"];
          lastStart = leds["start"];
          lastEnd = leds["end"];
          if (lastStart < 0 || lastStart > 1){
            lastStart = 0;
          }
          if (lastEnd < 0 || lastEnd > 1){
            lastEnd = 1;
            Serial.println("led/leds/end value is < 0 or > 1. Set to 1");
          }
          
          if (ledData.containsKey("static")){
              JsonObject& color = ledData["static"];
              staticR = color["r"];
              staticG = color["g"];
              staticB = color["b"];
              ledMode = 1;
          } else if (ledData.containsKey("rainbow")){
              Serial.println("Rainbow coming your way!!!"); 
              JsonObject& options = ledData["rainbow"];
              if (options.containsKey("speed"))
                speedMs = options["speed"];
              if (speedMs > 255) speedMs = 255;
              ledMode = 2;
          } else if (ledData.containsKey("kitt")){
              JsonObject& options = ledData["kitt"];
              if (options.containsKey("speed"))
                speedMs = options["speed"];
              if (speedMs > 255) speedMs = 255;
              if (options.containsKey("length")){
                if (options["length"] <= 255 && options["length"] > 0) 
                  kittLength = options["length"];
              }
            Serial.println("Kitt LEDs coming");
            uint16_t i;
            for (i = 0; i < NUMPIXELS; i++){
              ledRedness[i] = 0;
            }
            leadLedKitt = 0;
            ledMode = 3;
         }

          

      } else {
        Serial.println("Could not parse JSON");
        //Send to error topic
      }


  }


}

void rainbowUpdate(uint8_t state, uint16_t startLed, uint16_t endLed) {
  uint16_t i;
    for(i=startLed; i< endLed; i++) {
      //states larger than 255 are simply cut off after affing
      pixels.setPixelColor(i, Wheel((i+state) & 255));
    }
    pixels.show();
}

void kittUpdateV3(uint8_t state, uint16_t startLed, uint16_t endLed){
  uint16_t i;
  uint16_t numLeds = endLed - startLed;
  //no states here only the ledLed is always iterated
  leadLedKitt++;
  if (leadLedKitt > numLeds * 2) leadLedKitt = 0;
 
  //TODO make more efficient
  uint16_t  mainLed = leadLedKitt;
  if (leadLedKitt > numLeds){
    mainLed = (mainLed - numLeds) * (-1) + numLeds;
  }
  mainLed = mainLed + startLed;
  double multiplicationFactor =  -179.0/5080 * kittLength + (50979.0/5080);
  Serial.println(multiplicationFactor);
  Serial.println(kittLength);


  for (i = startLed; i <= endLed; i++){
    //decay everyLed
    if (ledRedness[i] > 0){
      //Change the divided by two factor to change decay length
      ledRedness[i] /= multiplicationFactor;
      uint8_t subtract = 50;
      //if can be left away but lights got a Rattenschwanz  
      if (ledRedness[i] < 5) ledRedness[i] = 0;
      pixels.setPixelColor(i, pixels.Color(ledRedness[i], 0,0));

    }
    //set the leading LED
    if (i == mainLed){
      ledRedness[i] = 255;
      pixels.setPixelColor(i, pixels.Color(ledRedness[i], 0,0));
    }
  }
  pixels.show();
}

/*
void kittUpdateV2(uint8_t state, uint16_t startLed, uint16_t endLed){
  uint16_t i;
  uint16_t numLeds = endLed - startLed;

   //if too few LEDs for states make that each state controls a different LED
 
  //TODO make more efficient
  uint16_t  mainLed = ((numLeds * state) / 128);
  if (state > 128){
    mainLed = (mainLed - numLeds) * (-1) + numLeds;
  }
  mainLed = mainLed + startLed;


  for (i = startLed; i <= endLed; i++){
    //decay everyLed
    if (ledRedness[i] > 0){
      uint8_t subtract = 50;
      if (ledRedness[i] < subtract){ 
        subtract = ledRedness[i];
      }
      ledRedness[i] = ledRedness[i] - subtract;
      pixels.setPixelColor(i, pixels.Color(ledRedness[i], 0,0));

    }
    //set the leading LED
    if (i == mainLed){
      ledRedness[i] = 255;
      pixels.setPixelColor(i, pixels.Color(ledRedness[i], 0,0));
    }
  }
  pixels.show();
}

*/
/*
void kittUpdate(uint8_t state, uint16_t startLed, uint16_t endLed){
  uint16_t i;
  uint16_t numLeds = endLed - startLed;

  //TODO make more efficient
  uint16_t  mainLed = (numLeds * (state)) / 128;
  if (state > 128){
    mainLed = (mainLed - numLeds) * (-1) + numLeds;
  }
  Serial.println(mainLed);
  Serial.println(state);
     
  for (i = startLed; i < endLed; i++){
    
        double red;
        int16_t distance = mainLed - i;
        if (distance < 0 ) distance = distance * (-1);
        if (state < 128 && i < mainLed)
          red = 255 - (distance * 25);
        else if (state >= 128 && i > mainLed)
          red = 255 - (distance * 25);
        else 
          red = 0;
        if ( red < 0 ) red = 0;
        pixels.setPixelColor(i, pixels.Color(red, 0,0));
  }
  pixels.show();
}*/
// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return pixels.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return pixels.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return pixels.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}
