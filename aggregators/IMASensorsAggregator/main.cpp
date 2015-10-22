#include <QCoreApplication>
#include "connectionhandler.h"
int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    checzInterface* sensors = new checzInterface();
    QThreadPool::globalInstance()->start(sensors);

    connectionHandler Server;
    Server.setInterface(sensors);
    Server.StartServer();

    return a.exec();
}
