
void setup() {
  // put your setup code here, to run once:
  pinMode(12, OUTPUT);
  pinMode(11, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(12, HIGH);
  digitalWrite(11, LOW);
  delay(500);
  digitalWrite(11, HIGH);
  delay(500);
  digitalWrite(12, LOW);
  digitalWrite(11, LOW);
  delay(500);
  digitalWrite(11, HIGH);
  delay(500);
}

