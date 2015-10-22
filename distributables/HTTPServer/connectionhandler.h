#ifndef CONNECTIONHANDLER_H
#define CONNECTIONHANDLER_H

#include <QTcpServer>
#include <QTcpSocket>
#include <QAbstractSocket>
#include "client.h"

class connectionHandler : public QTcpServer
{
    Q_OBJECT
public:
    explicit connectionHandler(QObject *parent = 0);
    void StartServer();
    void setInterface(checzInterface* interface);

protected:
    void incomingConnection(qintptr handle);

signals:

public slots:

private:
    checzInterface* sensors;

};

#endif // CONNECTIONHANDLER_H
