const unsigned int ENA = 10;
const unsigned int INA1 = 8;
const unsigned int INA2 = 9;

const unsigned int ENB = 44;
const unsigned int INB1 = 6;
const unsigned int INB2 = 7;

int gauche = 0;
int droite = 0;

void setup() {
  // config outputs
  pinMode(ENA, OUTPUT);
  pinMode(INA1, OUTPUT);
  pinMode(INA2, OUTPUT);
  
  pinMode(ENB, OUTPUT);
  pinMode(INB1, OUTPUT);
  pinMode(INB2, OUTPUT);

  // initial status = stop & forward
  digitalWrite(INA1, HIGH);
  digitalWrite(INA1, LOW);
  digitalWrite(INB1, HIGH);
  digitalWrite(INB1, LOW);
  digitalWrite(ENA, LOW);
  digitalWrite(ENB, LOW);

  // init bluetooth
  Serial2.begin(9600);
}

void alimMoteurs(){
  //sens
  if(gauche >= 0){ // avance
    digitalWrite(INA1, HIGH);
    digitalWrite(INA2, LOW);
  }else{ // recule
    digitalWrite(INA1, LOW);
    digitalWrite(INA2, HIGH);
  }
  
  if(droite >= 0){ // avance
    digitalWrite(INB1, HIGH);
    digitalWrite(INB2, LOW);
  }else{ // recule
    digitalWrite(INB1, LOW);
    digitalWrite(INB2, HIGH);
  }

  // puissance
  analogWrite(ENA, gauche >= 0 ? gauche : -gauche);
  analogWrite(ENA, droite >= 0 ? droite : -droite);
}

void loop() {
  // récupère les données via bluetooth
  // PS : pour que les données soient
  // correctement reçues, il faut un
  // délai de 30 ms minimum entre chaque
  // transmission !
  // (30 ms est suffisant pour réagir
  // instantanément. Moins de 30 ms, peut
  // causer des problèmes)
  if(Serial2.available()){
    // g ou d recoit (unsigned) : [0, 255]
    // ce qui correspond à (signed) : [-127, 127]
    int g = Serial2.read();
    int d = Serial2.read();

    // [-inf, 0] -> 128
    // [0, inf] -> [0, inf]
    if(g < 0)
      g = 128;
    if(d < 0)
      d = 128;
    // [0, inf] -> [0, 255]
    if(g > 255)
      g = 255;
    if(d > 255)
      d = 255;

    if(g > 128){ // [128, 255]
      gauche = ((g - 0x7F) * 2) - 1; // [128, 255] -> [0, 255]
    }else{ // [0, 127]
      gauche = ((0x7F - g) * -2) - 1; // [0, 127] -> [-255, 1]
    }

    if(d > 128){ // [128, 255]
      droite = ((d - 0x7F) * 2) - 1; // [128, 255] -> [0, 255]
    }else{ // [0, 127]
      droite = ((0x7F - d) * -2) - 1; // [0, 127] -> [-255, 1]
    }
  }

  // amilentation des moteurs
  alimMoteurs();
}
