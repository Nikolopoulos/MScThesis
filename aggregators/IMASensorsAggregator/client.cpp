#include "client.h"


Client::Client(QObject *parent) :
    QObject(parent)
{
}

void Client::setSocket(int Descriptor){
    socket = new QTcpSocket(this);

    connect(socket,SIGNAL(connected()),this,SLOT(connected()));
    connect(socket,SIGNAL(disconnected()),this,SLOT(disconnected()));
    connect(socket,SIGNAL(readyRead()),this,SLOT(readyRead()));

    socket->setSocketDescriptor(Descriptor);

    qDebug() << "client connected with descriptor "<< Descriptor;
}

void Client::setInterface(checzInterface* interface){
    lowLevel = interface;
    connect(lowLevel,SIGNAL(Result(QString)),
            this,SLOT(TaskResult(QString)),
            Qt::QueuedConnection);

}

void Client::connected(){
    qDebug() << "client connected event";
}

void Client::disconnected(){
    qDebug() << "client disconnected event";
}

void Client::readyRead(){
    //qDebug() << socket->readAll();
    QStringList tokens;
    tokens = QString(socket->readLine()).split(QRegExp(" "));
    try{
        qDebug() << tokens[1];
        lowLevel->getCalled(tokens[1]);
    }
    catch(){
        qDebug() << "error on ready read => incoming text was " +tokens;

    }
}

void Client::TaskResult(QString s){

    QByteArray Buffer;
    //Buffer.append("<html><head></head><body><h1>");
    Buffer.append(s);
    //Buffer.append("</h1></body></html>");

    socket->write(Buffer);
    socket->flush();
    socket->close();

}
