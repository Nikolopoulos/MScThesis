
#ifndef HTTPREQUEST_H
#define HTTPREQUEST_H
#include "checzinterface.h"

#include <QObject>
#include <QNetworkRequest>
#include <QNetworkReply>
#include <QNetworkAccessManager>

#include <QUrl>

class HTTPRequest : public QObject
{
    Q_OBJECT
public:
    explicit HTTPRequest(QObject *parent = 0);
    void setCi(checzInterface* aci);
    checzInterface* ci;
    QNetworkAccessManager* networkAccessManager ;
    void SendPost(QString URL,QString arguements,QString service);
    void lowSendPost(QString URL,QString arguements,QString service);
    QNetworkReply* Treply;


signals:
    QNetworkReply* getReply(QNetworkReply * reply);

public slots:
    void esoReply(QNetworkReply * reply);
    void esoError(QNetworkReply::NetworkError);
    void esoReadyRead();

};

#endif // HTTPREQUEST_H
