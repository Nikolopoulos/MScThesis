#ifndef CLIENT_H
#define CLIENT_H

#include <QStringList>
#include <QObject>
#include <QTcpSocket>
#include <QDebug>
#include <QThreadPool>
#include <QString>
#include "checzinterface.h"

class Client : public QObject
{
    Q_OBJECT
public:
    explicit Client(QObject *parent = 0);
    void setSocket (qintptr Descriptor);
    void setInterface(checzInterface* interface);
signals:


public slots:
    void connected();
    void disconnected();
    void readyRead();
    void TaskResult(QString s);

private:
    QTcpSocket* socket;
    checzInterface* lowLevel;

};

#endif // CLIENT_H
