
#include <QCoreApplication>
#include "connectionhandler.h"
#include "httprequest.h"
int main(int argc, char *argv[])
{
            qDebug() << "1";
    QCoreApplication a(argc, argv);

            qDebug() << "2";
    checzInterface* sensors = new checzInterface();

    HTTPRequest* arequest = new HTTPRequest(sensors);
    QObject::connect(arequest,SIGNAL(getReply(QNetworkReply*)),sensors,SLOT(receivePostReply(QNetworkReply*)));
    arequest->setCi(sensors);
    QString arguements[1];
    arguements[0]="";
    QString ip = "127.0.0.1";
    arequest->SendPost("http://localhost:8383","ip="+ip+"&port=8086&services={\"services\":[{\"uri\" : \"/sensors\", \"description\" : \"returns a list of sensors available\"}]}", "/register");

            qDebug() << "3";
    QThreadPool::globalInstance()->start(sensors);
            qDebug() << "4";
    connectionHandler Server;
            qDebug() << "5";
    Server.setInterface(sensors);
            qDebug() << "6";
    Server.StartServer();
            qDebug() << "7";
    return a.exec();
}
