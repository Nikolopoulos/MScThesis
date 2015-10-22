#-------------------------------------------------
#
# Project created by QtCreator 2015-09-24T19:11:52
#
#-------------------------------------------------

QT       += core
QT       += network

QT       -= gui

TARGET = IMAServer
CONFIG   += console
CONFIG   -= app_bundle
QT   += serialport
CONFIG   += serialport

TEMPLATE = app


SOURCES += main.cpp \
    checzinterface.cpp \
    client.cpp \
    connectionhandler.cpp \
    httprequest.cpp

HEADERS += \
    checzinterface.h \
    client.h \
    connectionhandler.h \
    httprequest.h
