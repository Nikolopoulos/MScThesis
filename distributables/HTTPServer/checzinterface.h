#ifndef CHECZINTERFACE_H
#define CHECZINTERFACE_H

#include <QRunnable>
#include <assert.h>
#include <QThread>
#include <QObject>
#include <QDebug>
#include <QString>
#include <QTime>
#include <QCoreApplication>
#include <QtSerialPort/QSerialPort>
#include <QtSerialPort/QSerialPortInfo>
#include <QStringList>
#include <QNetworkRequest>
#include <QNetworkReply>
#include <QNetworkAccessManager>
#include <QtCore/qmath.h>

typedef struct sensor {
  int id;
  int sense1;
  int sense2;
  int sense3;
  struct sensor* prev;
  struct sensor* next;
} sensor_t;



class checzInterface : public QObject, public QRunnable
{
    Q_OBJECT

public:
    checzInterface();
    void getCalled(QString s);

protected:
    void run();
    void openport();
    void readport();
    sensor_t* findByID(int id);
    bool createSensor(int id, int sense1, int sense2, int sense3);
    int getTempById(int id) ;
    int getHumidityById(int id);
    int getPressureById(int id);
    void delay(long howLong);
    bool filter(char* line);
    sensor_t* splitData(char* line);
    QString* getSensors();
    QString* getSensor(sensor_t* cur);
    QString* JSONizeId(sensor_t* cur);
    QString* JSONizeTemp(sensor_t* cur);
    QString* JSONizeHum(sensor_t* cur);
    QString* JSONizePress(sensor_t* cur);
    double a2d2celsius(int reading);
    double a2d2engi(int thermistor);




    QSerialPort* serial;
    sensor_t* nodeList;

signals:
    void Result(QString Number);

public slots:
    void receivePostReply(QNetworkReply * reply);
};

#endif // CHECZINTERFACE_H
