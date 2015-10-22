#include "connectionhandler.h"

connectionHandler::connectionHandler(QObject *parent) :
    QTcpServer(parent)
{
}

void connectionHandler::StartServer(){
    if(listen(QHostAddress::Any,8080)){
        qDebug() << "Server Started Successfully on port 8080";
    }

    else{
        qDebug() << "Server did not initialize";
    }
}
void connectionHandler::setInterface(checzInterface* interface){
    sensors=interface;
}
void connectionHandler::incomingConnection(int handle){
    Client* cl = new Client(this);
    cl->setSocket(handle);
    cl->setInterface(sensors);

}
