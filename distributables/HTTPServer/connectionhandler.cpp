#include "connectionhandler.h"

connectionHandler::connectionHandler(QObject *parent) :
    QTcpServer(parent)
{
    qDebug() << "Creating Server" ;
}

int PORT = 8086;
void connectionHandler::StartServer(){
    if(listen(QHostAddress::Any,PORT)){
        qDebug() << "Server Started Successfully on port " << PORT;
    }

    else{
        qDebug() << "Server did not initialize";
    }
}
void connectionHandler::setInterface(checzInterface* interface){
    qDebug() << "Creating interface";
    sensors=interface;
}
void connectionHandler::incomingConnection(qintptr handle){
    qDebug() << "Incoming connection 1";
    Client* cl = new Client(this);
    qDebug() << "Incoming connection 2";
    cl->setSocket(handle);
    qDebug() << "Incoming connection 3";
    cl->setInterface(sensors);
    qDebug() << "Incoming connection 4";
}
