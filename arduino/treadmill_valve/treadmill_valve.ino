#include <Encoder.h>

// DATA
const int PORT = 9600;
const int rwrdPin = 7; // yes?
const int TTLIn = 8;
const int DRINKING_TIME = 10;
const int TTL = 0;
const int lickdtcPin = 4;
const int LICK_ON = 100;
const int LICK_OFF = -100;


enum Signals { START, REWARD };

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
bool ttlPulse = false;
bool lickPulse = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(PORT);
  pinMode(rwrdPin, OUTPUT);
  pinMode(rwrdPin, OUTPUT);
  pinMode(TTLIn, INPUT);
  pinMode(lickdtcPin, INPUT);
  myEnc.write(0);
  oldPosition = 0;
}

void loop() {
  signal_handler();
  encoder_handler();
  rwrd_pin_handler();
  lick_sensor_handler();
  Ttl_handler();
}

void encoder_handler() {
  long newPosition = myEnc.read();
  if (newPosition != oldPosition) {
    Serial.print(newPosition - oldPosition);
    Serial.print('\n');
    oldPosition = newPosition;
  }
}

void rwrd_pin_handler() {
  if (!rewardIsOpened && rwrdsNeedToOpen > 0) {
    digitalWrite(rwrdPin, HIGH);
    rwrdStartTimer = millis();
    rwrdsNeedToOpen--;
    rewardIsOpened = true;
  }
  if (rewardIsOpened && (millis() - rwrdStartTimer >= DRINKING_TIME)) {
    digitalWrite(rwrdPin, LOW);
    rewardIsOpened = false;
  }
}

void Ttl_handler() {
  int ttlValue = digitalRead(TTLIn);
  if (!ttlPulse && ttlValue == HIGH) {
    Serial.print(TTL);
    Serial.print('\n');
    ttlPulse = true;
  }
  else if (ttlPulse && ttlValue == LOW) {
    ttlPulse = false;
  }
}



void lick_sensor_handler() {
  int lickValue = digitalRead(lickdtcPin);
  if (!lickPulse && lickValue == HIGH) { // PROBLEM do last off?
    Serial.print(LICK_ON);
    Serial.print('\n');
    lickPulse = true;
  }
  else if (lickPulse && lickValue == LOW) {
    Serial.print(LICK_OFF);
    Serial.print('\n');
    lickPulse = false;
  }
}

void signal_handler() {
  if (Serial.available())
  {
    int command = Serial.read();
    switch (command) {
      case REWARD:
        rwrdsNeedToOpen++;
        break;
    }
  }
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
