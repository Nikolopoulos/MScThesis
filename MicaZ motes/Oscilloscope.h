/*
 * Copyright (c) 2006 Intel Corporation
 * All rights reserved.
 *
 * This file is distributed under the terms in the attached INTEL-LICENSE     
 * file. If you do not find these files, copies can be found by writing to
 * Intel Research Berkeley, 2150 Shattuck Avenue, Suite 1300, Berkeley, CA, 
 * 94704.  Attention:  Intel License Inquiry.
 */

// @author David Gay

#ifndef OSCILLOSCOPE_H
#define OSCILLOSCOPE_H

enum {
  /* Number of readings per message. If you increase this, you may have to
     increase the message_t size. */
  NREADINGS = 10,
  /*readings flags*/
  PHOTO = 1,
  TEMP = 0,
   /*message types*/
  POLL = 0,
  ACK = 1,
  READINGREQUEST = 3,
  READINGANSWER = 4,
  SWITCHPOLL=5,
  SWITCHCHANGE=6,
  
  SWITCHANSWER=7,
  /*services*/
  TEMP_S = 1,
  PHOTO_S = 2,
  PIN_S = 4,
  /* Default sampling period. */
  DEFAULT_INTERVAL = 50,

  AM_POLLREQ = 0x42,
  AM_POLLACK = 0x62,
  AM_READREQ = 0x55,
  AM_READACK = 0x75,
  AM_SWITCHPOLL = 0x51,
  AM_SWITCHCHANGE = 0x15,
  AM_SWITCHANSWER = 0x66,
  AM_POLL_B2M_MSG = 0x42,
  AM_POLL_M2B_MSG = 0x62,
  AM_READINGMSGREQUEST = 0x55,
  AM_READINGMSGANSWER = 0x75,
  AM_SWITCHPOLLREQUEST = 0x51
}; 

typedef nx_struct ReadingMsgAnswer {
  nx_uint16_t messageType;
  nx_uint16_t id; /* Mote id of sending mote. */
  nx_uint16_t type; /*type of reading 0=temp 1=photo*/
  nx_uint16_t readings[NREADINGS];
} readings_answer_t;

typedef nx_struct ReadingMsgRequest {
  nx_uint16_t messageType;
  nx_uint16_t id; /* Mote id of sending mote. */
  nx_uint16_t type; /*type of reading 0=temp 1=photo*/
} readings_request_t;

typedef nx_struct SwitchPollRequest {
  nx_uint16_t messageType;
  nx_uint16_t id; /* Mote id of recepient mote. */
} switch_poll_t;

typedef nx_struct SwitchAnswer {
  nx_uint16_t messageType;
  nx_uint16_t id; /* Mote id of recepient mote. */
  nx_uint16_t state; /* Mote id of recepient mote. */
} switch_answer_t;

typedef nx_struct SwitchChange {
  nx_uint16_t messageType;
  nx_uint16_t id; /* Mote id of recepient mote. */
} switch_change_t;


typedef nx_struct poll_b2m_Msg {
  nx_uint16_t messageType; /* State that it is a poll message. */
} poll_request_t;

typedef nx_struct poll_m2b_Msg {
  nx_uint16_t messageType;
  nx_uint16_t services;
  nx_uint16_t id; /* Mote id of sending mote. */
} poll_answer_t;

typedef nx_struct myStatus{
  nx_uint16_t id;
  nx_uint16_t switchState; /* Mote id of sending mote. */ 
  nx_uint16_t services; /* Mote services available. */ 
} status_t;

#endif

