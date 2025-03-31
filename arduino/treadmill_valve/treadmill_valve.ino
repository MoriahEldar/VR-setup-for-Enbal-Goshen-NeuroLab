#include <Encoder.h>

// DATA
const int PORT = 9600;
const int rwrdPin = 4;
const int TTLIn = A5;
const int DRINKING_TIME = 10;
const int TTL = 0;

enum Signals { START, REWARD }

// Change these two numbers to the pins connected to your encoder.
//   Best Performance: both pins have interrupt capability
//   Good Performance: only the first pin has interrupt capability
//   Low Performance:  neither pin has interrupt capability
//   avoid using pins with LEDs attached
Encoder myEnc(2, 3);
long oldPosition = 0; // restart when number is to long
int rwrdsNeedToOpen = 0;
unsigned long rwrdStartTimer = 0;
bool rewardIsOpened = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(PORT);
  pinMode(rwrdPin, OUTPUT);
  pinMode(rwrdPin, OUTPUT);
  pinMode(TTLIn, INPUT);
  start();
}

void loop() {
  signal_handler();
  encoder_handler();
  rwrd_pin_handler();
  Ttl_handler();
  lick_sensor();
}

void encoder_handler() {
  long newPosition = myEnc.read();
  if (newPosition != oldPosition) {
    Serial.println(newPosition - oldPostion); // check
    oldPosition = newPosition;
  }
}

void rwrd_pin_handler() {
  if (!rewardIsOpened && rwrdsNeedToOpen > 0) {
    digitalWrite(rwrdPin, HIGH);
    rwrdStartTimer = currentTime;
    rwrdsNeedToOpen--;
  }
  if (rewardIsOpened && (millis() - rwrdStartTimer >= DRINKING_TIME)) {
    digitalWrite(rwrdPin, LOW);
  }
}

void Ttl_handler() {
  int ttlValue = digitalRead(TTLIn);
  if (ttlValue == HIGH) {
    Serial.println(TTL);
    // check if i need to change digital read value
  }
}

void start() {
  myEnc.write(0);
  oldPosition = 0;
  Serial.println("start");
}

void signal_handler() {
  if (Serial.available())
  {
    int command = Serial.read();
    switch (command) {
      case START:
        start();
        break;
      case REWARD:
        rwrdsNeedToOpen++;
        break;
    }
  }
}

void lick_sensor() {
 // how to do that?
}

void send_lick_signal() {

}

void open_valve() {

}

void reward_control() {
  // TODO also when gets signal from the computer
  // but also when its configured ידני

}

void treadmill_control() {

}

void start_signal() {

}

void stop_signal() {

}
// use Sensor
