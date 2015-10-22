/****************************************************************************
** Meta object code from reading C++ file 'httprequest.h'
**
** Created by: The Qt Meta Object Compiler version 67 (Qt 5.3.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../../eudemo/HTTPServer/httprequest.h"
#include <QtCore/qbytearray.h>
#include <QtCore/qmetatype.h>
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'httprequest.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 67
#error "This file was generated using the moc from 5.3.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
struct qt_meta_stringdata_HTTPRequest_t {
    QByteArrayData data[9];
    char stringdata[102];
};
#define QT_MOC_LITERAL(idx, ofs, len) \
    Q_STATIC_BYTE_ARRAY_DATA_HEADER_INITIALIZER_WITH_OFFSET(len, \
    qptrdiff(offsetof(qt_meta_stringdata_HTTPRequest_t, stringdata) + ofs \
        - idx * sizeof(QByteArrayData)) \
    )
static const qt_meta_stringdata_HTTPRequest_t qt_meta_stringdata_HTTPRequest = {
    {
QT_MOC_LITERAL(0, 0, 11),
QT_MOC_LITERAL(1, 12, 8),
QT_MOC_LITERAL(2, 21, 14),
QT_MOC_LITERAL(3, 36, 0),
QT_MOC_LITERAL(4, 37, 5),
QT_MOC_LITERAL(5, 43, 8),
QT_MOC_LITERAL(6, 52, 8),
QT_MOC_LITERAL(7, 61, 27),
QT_MOC_LITERAL(8, 89, 12)
    },
    "HTTPRequest\0getReply\0QNetworkReply*\0"
    "\0reply\0esoReply\0esoError\0"
    "QNetworkReply::NetworkError\0esoReadyRead"
};
#undef QT_MOC_LITERAL

static const uint qt_meta_data_HTTPRequest[] = {

 // content:
       7,       // revision
       0,       // classname
       0,    0, // classinfo
       4,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       1,       // signalCount

 // signals: name, argc, parameters, tag, flags
       1,    1,   34,    3, 0x06 /* Public */,

 // slots: name, argc, parameters, tag, flags
       5,    1,   37,    3, 0x0a /* Public */,
       6,    1,   40,    3, 0x0a /* Public */,
       8,    0,   43,    3, 0x0a /* Public */,

 // signals: parameters
    0x80000000 | 2, 0x80000000 | 2,    4,

 // slots: parameters
    QMetaType::Void, 0x80000000 | 2,    4,
    QMetaType::Void, 0x80000000 | 7,    3,
    QMetaType::Void,

       0        // eod
};

void HTTPRequest::qt_static_metacall(QObject *_o, QMetaObject::Call _c, int _id, void **_a)
{
    if (_c == QMetaObject::InvokeMetaMethod) {
        HTTPRequest *_t = static_cast<HTTPRequest *>(_o);
        switch (_id) {
        case 0: { QNetworkReply* _r = _t->getReply((*reinterpret_cast< QNetworkReply*(*)>(_a[1])));
            if (_a[0]) *reinterpret_cast< QNetworkReply**>(_a[0]) = _r; }  break;
        case 1: _t->esoReply((*reinterpret_cast< QNetworkReply*(*)>(_a[1]))); break;
        case 2: _t->esoError((*reinterpret_cast< QNetworkReply::NetworkError(*)>(_a[1]))); break;
        case 3: _t->esoReadyRead(); break;
        default: ;
        }
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        switch (_id) {
        default: *reinterpret_cast<int*>(_a[0]) = -1; break;
        case 0:
            switch (*reinterpret_cast<int*>(_a[1])) {
            default: *reinterpret_cast<int*>(_a[0]) = -1; break;
            case 0:
                *reinterpret_cast<int*>(_a[0]) = qRegisterMetaType< QNetworkReply* >(); break;
            }
            break;
        case 1:
            switch (*reinterpret_cast<int*>(_a[1])) {
            default: *reinterpret_cast<int*>(_a[0]) = -1; break;
            case 0:
                *reinterpret_cast<int*>(_a[0]) = qRegisterMetaType< QNetworkReply* >(); break;
            }
            break;
        case 2:
            switch (*reinterpret_cast<int*>(_a[1])) {
            default: *reinterpret_cast<int*>(_a[0]) = -1; break;
            case 0:
                *reinterpret_cast<int*>(_a[0]) = qRegisterMetaType< QNetworkReply::NetworkError >(); break;
            }
            break;
        }
    } else if (_c == QMetaObject::IndexOfMethod) {
        int *result = reinterpret_cast<int *>(_a[0]);
        void **func = reinterpret_cast<void **>(_a[1]);
        {
            typedef QNetworkReply * (HTTPRequest::*_t)(QNetworkReply * );
            if (*reinterpret_cast<_t *>(func) == static_cast<_t>(&HTTPRequest::getReply)) {
                *result = 0;
            }
        }
    }
}

const QMetaObject HTTPRequest::staticMetaObject = {
    { &QObject::staticMetaObject, qt_meta_stringdata_HTTPRequest.data,
      qt_meta_data_HTTPRequest,  qt_static_metacall, 0, 0}
};


const QMetaObject *HTTPRequest::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->dynamicMetaObject() : &staticMetaObject;
}

void *HTTPRequest::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_HTTPRequest.stringdata))
        return static_cast<void*>(const_cast< HTTPRequest*>(this));
    return QObject::qt_metacast(_clname);
}

int HTTPRequest::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        if (_id < 4)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 4;
    } else if (_c == QMetaObject::RegisterMethodArgumentMetaType) {
        if (_id < 4)
            qt_static_metacall(this, _c, _id, _a);
        _id -= 4;
    }
    return _id;
}

// SIGNAL 0
QNetworkReply * HTTPRequest::getReply(QNetworkReply * _t1)
{
    QNetworkReply* _t0 = 0;
    void *_a[] = { const_cast<void*>(reinterpret_cast<const void*>(&_t0)), const_cast<void*>(reinterpret_cast<const void*>(&_t1)) };
    QMetaObject::activate(this, &staticMetaObject, 0, _a);
    return _t0;
}
QT_END_MOC_NAMESPACE
