#include "checzinterface.h"
#include "httprequest.h"
checzInterface::checzInterface()
{

}

void checzInterface::run(){
    //init and stuff
    qDebug() << "Chezh interface started";
    openport();
    nodeList = NULL;
    while(1){

        //beBusy();
        readport();
    }
}
void checzInterface::receivePostReply(QNetworkReply * reply){

    qDebug()<< "this is the reply:*****"+reply->readAll()+"*****";
}

void checzInterface::getCalled(QString s){
    QString help = "Usage:\t'host'/sensors \t\t\t\treturns list of sensors available\n\t'host'/sensor/'id'\t\t\treturns sensor data\n\t'host'/sensor/'id'/temp|humi|pres\treturns temperature|humidity|presure data for sensor with id 'id'";

    //Thank god QT works with == on QStrings
    if(s=="/sensors" ||s=="/sensors/")
    {
        QString* reffed = getSensors();
        QString derefed = *reffed;
        //qDebug() << "emiting " << derefed;
        emit Result(derefed);
    }
    else if(s.startsWith("/sensor/")&&s!="/sensor/")
    {

        QStringList tokens;
        tokens = s.split(QRegExp("/"));
        qDebug() << "token 2 is " << tokens[2];
        int id = tokens[2].toInt();
        qDebug() << "noparsed t2 is "<< id;
        sensor_t* temp = findByID(id);
        qDebug() << "0";
        if((tokens.length()<4)){
            qDebug() << "1";
            QString* reffed = getSensor(temp);
            qDebug() << "2";
            QString derefed = *reffed;
            qDebug() << "3";
            qDebug() << "emiting " << derefed;
            emit Result(derefed);
        }
        else if(tokens.length()>=4&&tokens[3]==""){
            qDebug() << "1";
            QString* reffed = getSensor(temp);
            qDebug() << "2";
            QString derefed = *reffed;
            qDebug() << "3";
            qDebug() << "emiting " << derefed;
            emit Result(derefed);
        }
        else{
            qDebug() << "5";
            if(tokens[3]=="temp"){
                qDebug() << "6";
                QString* reffed = new QString("{\"sensor\" : {");
                reffed->append(JSONizeTemp(temp));
                reffed->append("}}");
                QString derefed = *reffed;
                //qDebug() << "emiting " << derefed;
                emit Result(derefed);
            }
            else if(tokens[3]=="humi"){
                qDebug() << "7";
                QString* reffed = new QString("{\"sensor\" : {");
                reffed->append(JSONizeHum(temp));
                reffed->append("}}");
                QString derefed = *reffed;
                //qDebug() << "emiting " << derefed;
                emit Result(derefed);
            }
            else if(tokens[3]=="pres"){
               qDebug() << "8";
               QString* reffed = new QString("{\"sensor\" : {");
               reffed->append(JSONizePress(temp));
               reffed->append("}}");
               QString derefed = *reffed;
               //qDebug() << "emiting " << derefed;
               emit Result(derefed);
            }
            else{
                emit Result(help);
            }
        }



    }
    emit Result(help);

}

void checzInterface::openport() {

    foreach (const QSerialPortInfo &info, QSerialPortInfo::availablePorts()) {
            if(info.portName().endsWith("USB0")){
                serial = new QSerialPort(info);
                QIODevice::OpenMode readonly = QIODevice::ReadOnly;
                int result = serial->open(readonly);
                if (result){
                    serial->setBaudRate(serial->Baud115200);
                }
                else{
                    //handle exception
                    qDebug() << "Could not open port " << serial->portName();
                }
            }
    }
}
bool checzInterface::filter(char* line) {
    if (line[0] == 'D')
      return true;
    else
      return false;
}

sensor_t* checzInterface::splitData(char* line) {
  QString* temp = new QString(line);

  sensor_t* parsed = (sensor_t*)malloc(sizeof (sensor_t));

  QStringList tokens;
  tokens = temp->split(QRegExp(" "));
  if(tokens.length()>4){
  parsed->id = tokens[1].toInt(0,16);
  //qDebug() << "parsed id is " << parsed->id;

  parsed->sense1 = tokens[2].toInt();
 // qDebug() << "parsed s1 is " << parsed->sense1;

  parsed->sense2 = tokens[3].toInt();;
 //qDebug() << "parsed s2 is " << parsed->sense2;

  parsed->sense3 = tokens[4].toInt();;
 //qDebug() << "parsed s3 is " << parsed->sense3;

  //D 00A0 +0190 +0728 +0991

  return parsed;
  }
  else{

      return NULL;
  }
}

bool checzInterface::createSensor(int id, int sense1, int sense2, int sense3) {
  if (nodeList == NULL) {
    //REGISTER SERVICES OF SENSOR
    nodeList = (sensor_t*) malloc(sizeof (sensor_t));
    assert(nodeList);
    nodeList->id = id;
    nodeList->sense1 = sense1;
    nodeList->sense2 = sense2;
    nodeList->sense3 = sense3;
    nodeList->next = NULL;
    nodeList->prev = NULL;
    return true;
  } else {
    sensor_t* cur = nodeList;
    while (1) {
      if (cur->next == NULL) {
        sensor_t* newSens;

        newSens =(sensor_t*) malloc(sizeof (sensor_t));
        assert(newSens);
        newSens->id = id;
        newSens->sense1 = sense1;
        newSens->sense2 = sense2;
        newSens->sense3 = sense3;
        newSens->next = NULL;
        newSens->prev = cur;
        cur->next = newSens;
        return true;
      }
    }

  }
  return false;

}

sensor_t* checzInterface::findByID(int id) {
  sensor_t* cur = nodeList;
  qDebug() << "trying to locate "<<id;
  if (cur == NULL){
    qDebug() << "is " << cur;
    return NULL;
  }
  while (1) {
     qDebug() << "cur id is " << cur ->id;
    if (cur->id == id) {
        qDebug() << "returning "<< cur->id;
      return cur;
    } else {
      if (cur->next == NULL) {
          qDebug() << "returning null";
        return NULL;

      } else {
        cur = cur->next;
      }
    }
  }
}

void checzInterface::readport(){

    int bytesRead;
    char* line = (char*) malloc(sizeof(char)*1024);
    bytesRead = serial->readLine(line, 1024);
    if(bytesRead>0){
        //qDebug() << line;
        if (filter(line)) {
          //  qDebug() << "Filter passed inspection";
          sensor_t* data = splitData(line);
          //qDebug() << "Sensor data split";
          if(!data)
          {
              qDebug() << "**************************************************Saved by the bell :O*************************************";
              return;
          }
          sensor_t* found = findByID(data->id);
          //qDebug() << "Sensor tuples created";
          if (found == NULL) {
          //qDebug() << "Create sensor";
              HTTPRequest* arequest = new HTTPRequest(this);
              QObject::connect(arequest,SIGNAL(getReply(QNetworkReply*)),this,SLOT(receivePostReply(QNetworkReply*)));
              arequest->setCi(this);
              QString arguements[1];
              arguements[0]="";
              QString ip = "127.0.0.1";
              arequest->SendPost("http://localhost:8383","ip="+ip+"&port=8086&services={\"services\":[{\"uri\" : \"/sensors\", \"description\" : \"returns a list of sensors available\"}]}", "/register\0\n");

              createSensor(data->id, data->sense1, data->sense2, data->sense3);
            //qDebug() << "Sensor created";
          } else {
            found->sense1 = data->sense1;
            found->sense2 = data->sense2;
            found->sense3 = data->sense3;
          }
          //qDebug() << "wtf";
          qDebug() << line;
        }
    }
    else{
        //qDebug() << "No Data, sleeping 200ms";
        delay(200);
    }

}

void checzInterface::delay(long howLong){
    QTime dieTime = QTime::currentTime().addMSecs(howLong);
    while(QTime::currentTime() < dieTime){
        QCoreApplication::processEvents(QEventLoop::AllEvents, 100);
    }
}

int checzInterface::getTempById(int id) {
  sensor_t* givenSensor = findByID(id);
  return givenSensor == NULL ? -1 : givenSensor->sense1;
}

int checzInterface::getHumidityById(int id) {
  sensor_t* givenSensor = findByID(id);
  return givenSensor == NULL ? -1 : givenSensor->sense2;
}

int checzInterface::getPressureById(int id) {
  sensor_t* givenSensor = findByID(id);
  return givenSensor == NULL ? -1 : givenSensor->sense3;
}

QString* checzInterface::getSensors(){
    sensor_t* cur = nodeList;
    QString* JSON = new QString("{\"Sensors\":{[");
    if (cur == NULL){
      JSON->append("]}}");
      return JSON;
    }
    while (1) {
       JSON->append(getSensor(cur));
       if (cur->next == NULL) {
          JSON->append("]}}");
          return JSON;
        } else {
           JSON->append(",");
          cur = cur->next;
        }
    }
}

QString* checzInterface::getSensor(sensor_t* cur){
    if(cur == NULL)
    {
        return new QString("{}");
    }
    QString* JSON = new QString("{ \" sensor\" :");

    JSON->append(JSONizeId(cur));
    JSON->append(",");
    JSON->append(JSONizeTemp(cur));
    JSON->append(",");
    JSON->append(JSONizeHum(cur));
    JSON->append(",");
    JSON->append(JSONizePress(cur));
    JSON->append("}");

    return JSON;
}


QString* checzInterface::JSONizeId(sensor_t* cur){
    QString* JSON = new QString("");
    JSON->append("\"ID\": \"");
    JSON->append(QString::number(cur->id));
    JSON->append("\"");
    return JSON;
}
QString* checzInterface::JSONizeTemp(sensor_t* cur){
    QString* JSON = new QString("");
    JSON->append("\"Temperature\": \"");
    double temperature = getTempById(cur->id) / 10.0;
    JSON->append(QString::number(temperature));
    JSON->append("\"");
    return JSON;

}


double checzInterface::a2d2engi(int thermistor) {
        double adc = thermistor;
        double Rthr = 10000 * (1023 - adc) / adc;
        return Rthr;
    }

double checzInterface::a2d2celsius(int reading) {

        double temperature, a, b, c, Rthr;
        a = 0.001307050;
        b = 0.000214381;
        c = 0.000000093;
        Rthr = a2d2engi(reading);
        temperature = 1 / (a + b * qLn(Rthr) + c * qPow(qLn(Rthr), 3));
        temperature -= 273.15; //Convert from Kelvin to Celcius
       /* printf(
         “debug:
         a =  % f b =  % f Rt =  % f temp =  % f\n”, a, b, c,Rt, temperature
         );*/
        return temperature;

}



QString* checzInterface::JSONizeHum(sensor_t* cur){
    QString* JSON = new QString("");
    JSON->append("\"Humidity\": \"");
    JSON->append(QString::number(getHumidityById(cur->id)));
    JSON->append("\"");
    return JSON;
}
QString* checzInterface::JSONizePress(sensor_t* cur){
    QString* JSON = new QString("");
    JSON->append("\"Pressure\": \"");
    JSON->append(QString::number(getPressureById(cur->id)));
    JSON->append("\"");
    return JSON;
}

