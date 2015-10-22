#ifndef HTTPREQUEST_H
#define HTTPREQUEST_H

#include <QObject>

class HTTPRequest : public QObject
{
    Q_OBJECT
public:
    explicit HTTPRequest(QObject *parent = 0);

signals:

public slots:

};

#endif // HTTPREQUEST_H
