#-------------------------------------------------
#
# Project created by QtCreator 2015-02-06T18:51:58
#
#-------------------------------------------------

QT       += core
QT       += network


QT       -= gui

TARGET = HTTPServer
CONFIG   += console
CONFIG   -= app_bundle
CONFIG   += serialport

TEMPLATE = app


SOURCES += main.cpp \
    connectionhandler.cpp \
    client.cpp \
    checzinterface.cpp \
    httprequest.cpp

HEADERS += \
    connectionhandler.h \
    client.h \
    checzinterface.h \
    httprequest.h
