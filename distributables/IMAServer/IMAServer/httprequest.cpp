#include "httprequest.h"
#include "checzinterface.h"

HTTPRequest::HTTPRequest(QObject *parent) :
    QObject(parent)
{
    networkAccessManager = new QNetworkAccessManager();
    connect(networkAccessManager,SIGNAL(finished(QNetworkReply*)),this,SLOT(esoReply(QNetworkReply*)));

}

void HTTPRequest::SendPost(QString URL,QString arguements,QString service){
    QNetworkRequest qnr;

    QString realArguements = QUrl::toPercentEncoding (arguements);
    int contentLength = realArguements.length();
    qnr.setRawHeader("User-Agent","IMA/5.0");
    qnr.setRawHeader("Accept-Language","en-US,en;q=0.5");
    qnr.setRawHeader("Host","127.0.0.1:8383");
    qnr.setRawHeader("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
    qnr.setRawHeader("Connection","close");
    qnr.setRawHeader("Accept-Encoding","no");
    qnr.setRawHeader("Content-Type","x-www-form-urlencoded");
    qnr.setHeader(QNetworkRequest::ContentLengthHeader,contentLength);

    qnr.setUrl(QUrl(URL+service));
    QByteArray postData;
    postData.append(realArguements);

    Treply = networkAccessManager->post(qnr, postData);


    connect(Treply,SIGNAL(downloadProgress(qint64,qint64)),this,SLOT(esoReadyRead()));
    connect(Treply, SIGNAL(error(QNetworkReply::NetworkError)),this, SLOT(esoError(QNetworkReply::NetworkError)));

}

void HTTPRequest::lowSendPost(QString URL,QString arguements,QString service){


    /*QHttpRequestHeader header("POST", QUrl::toPercentEncoding(service));
    QHttp request = new QHttp(this);

    QString realArguements = QUrl::toPercentEncoding (arguements);
    int contentLength = realArguements.length();
    header.setValue("User-Agent","IMA/5.0");
    header.setValue("Accept-Language","en-US,en;q=0.5");
    header.setValue("Host","127.0.0.1:8383");
    header.setValue("Accept","text/html, image/gif, image/jpeg, *; q=.2, *//*; q=.2");
    /*header.setValue("Connection","close");
    header.setValue("Accept-Encoding","no");
    header.setValue("Content-Type","x-www-form-urlencoded");
    header.setValue("Content-Length",contentLength);

    request.setHost(URL+service);
    request.

    qnr.setUrl(QUrl(URL+service));
    QByteArray postData;
    postData.append(realArguements);
    Treply = networkAccessManager->post(qnr, postData);

    connect(Treply,SIGNAL(downloadProgress(qint64,qint64)),this,SLOT(esoReadyRead()));*/

}



void HTTPRequest::esoReply(QNetworkReply * reply){
    Treply = reply;
    Treply->disconnect();
    qDebug() << "PrePreSignalReply: " + reply->readAll();
    qDebug() << "PrePreSignalReply: " + reply->readAll();
    qDebug() << "PrePreSignalReply: " + reply->read(900);
}
void HTTPRequest::esoError(QNetworkReply::NetworkError error){
    Treply->disconnect();
    qDebug() << "Error : ";
    qDebug() <<  error;

}

void HTTPRequest::esoReadyRead(){
    qDebug() << "PreSignalReply: " + Treply->readAll();
    emit getReply(Treply);
}


void HTTPRequest::setCi(checzInterface* aci){
    ci=aci;
}
