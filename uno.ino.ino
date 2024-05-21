#include <Wire.h>               // Thư viện Wire sử dụng để giao tiếp I2C.
#include <Servo.h>

#define trigPinIn 3  // Định nghĩa chân trig cho CB bên trong cửa hàng.
#define echoPinIn 2  // Định nghĩa chân echo cho CB bên trong cửa hàng.                                               // Định nghĩa chân echo cho CB bên ngoài cửa hàng.
#define ledPin 6     // Định nghĩa chân cho đèn LED.
#define buzzerPin 7  // Định nghĩa chân cho còi buzzer.
#define GOC_DONG 0   // Góc đóng của servo (độ)
#define GOC_MO 90    // Góc mở của servo (độ)
const int servoPin = 4;

Servo servo;
int count = 0;          // Biến lưu trữ số lượng người trong cửa hàng.
bool isUpdate = false;  // Biến kiểm tra nếu có người vào trong cửa hàng.
bool updateLCD = true;  // Biến kiểm tra cập nhật màn hình LCD.
int servoState = 0;
char command = '3';
bool door_auto = true;
int door_status = 3;

long tb = 30;
long Min = 20;
long Max = 50;
long distanceOld;
unsigned long previousMillis = 0;  // Biến lưu trữ thời gian kể từ lần cuối cùng in dữ liệu ra Serial.
const long interval = 1000;          // Thời gian giữa các lần gửi dữ liệu ra Serial, 1 giây.

void setup() {
  Serial.begin(9600);  // Khởi tạo giao tiếp serial với tốc độ 9600 bps.
  pinMode(trigPinIn, OUTPUT);  // Chân trig bên trong là đầu ra.
  pinMode(echoPinIn, INPUT);   // Chân echo bên trong là đầu vào.                                      // Chân echo bên ngoài là đầu vào.
  pinMode(ledPin, OUTPUT);     // Chân cho đèn LED là đầu ra.
  pinMode(buzzerPin, OUTPUT);  // Chân cho loa còi là đầu ra.
  servo.attach(servoPin);
  delay(2000);
}

void beep(int duration) {
  digitalWrite(buzzerPin, HIGH);  // Bật loa còi.
  delay(duration);                // Delay theo thời gian được truyền vào.
  digitalWrite(buzzerPin, LOW);   // Tắt loa còi.
}

void loop() {
  long durationIn, distanceIn;

  digitalWrite(trigPinIn, LOW);           // Đặt chân trig bên trong xuống mức thấp.
  delayMicroseconds(2);                   // Delay 2 microseconds.
  digitalWrite(trigPinIn, HIGH);          // Đặt chân trig bên trong lên mức cao.
  delayMicroseconds(10);                  // Delay 10 microseconds.
  digitalWrite(trigPinIn, LOW);           // Đặt chân trig bên trong xuống mức thấp.
  durationIn = pulseIn(echoPinIn, HIGH);  // Đo thời gian phản hồi từ cảm biến echo bên trong.
  distanceIn = (durationIn / 2) / 29.1;   // Tính khoảng cách từ thời gian phản hồi.

  // Kiểm tra nếu đã qua một giây kể từ lần cuối cùng in dữ liệu ra Serial
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;  // Cập nhật thời gian lần cuối cùng in dữ liệu ra Serial

    char jsonBuffer[100];
    sprintf(jsonBuffer, "{"
                        "\"amount_of_people\": %d,"
                        "\"door_status\": %d,"
                        "\"distance\": %d"
                        "}",
            count, door_status, distanceIn);

    // In chuỗi JSON ra Serial để debug
    Serial.println(jsonBuffer);
  }

  if (Serial.available() > 0) {
    command = Serial.read();

    if (command == '1') {
      servo.write(GOC_MO);  // mở cửa khi nhận '1' từ Serial
      door_auto = false;
      door_status = 1;
    } else if (command == '2') {
      servo.write(GOC_DONG);  // mở cửa khi nhận '0' từ Serial
      door_auto = false;
      door_status = 2;
    } else if (command == '3') {
      door_auto = true;
      door_status = 3;
    }
  }
  if (door_auto == true) {
    if (distanceIn < Max && distanceIn > Min) {
      if (distanceIn > 25 && distanceIn < 35) {
        if (distanceIn >= distanceOld && isUpdate == false) {
          if (count > 0)
            count--;

          beep(500);
          isUpdate = true;
        } else if (distanceIn < distanceOld && isUpdate == false) {
          count++;
          beep(500);
          isUpdate = true;
        }
      } else {
        isUpdate = false;
      }
      servo.write(GOC_MO);
    } else {
      servo.write(GOC_DONG);
    }
    distanceOld = distanceIn;
    if (count <= 0) {
      digitalWrite(ledPin, LOW);  // Tắt đèn LED.
    } else {
      digitalWrite(ledPin, HIGH);  // Bật đèn LED.
    }
  }
}
