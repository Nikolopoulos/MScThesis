/*
 * Copyright (c) 2014 Grit Science
 * All rights reserved.
 *
 */

/**
 * Hua Dissertation Thesis.
 *
 * @author Basil Nikolopoulos
 */
#include "Timer.h"
#include "Oscilloscope.h"

module OscilloscopeC @safe()
{
  uses {
    interface Boot;
    interface SplitControl as RadioControl;
    interface AMSend as pollResponder;
    interface Receive as pollReceiver;
    interface AMSend as readResponder;
    interface Receive as readReceiver;
    interface Receive as switchToggle;
    interface Receive as switchPoll;
    interface AMSend as switchResponder;
    interface Timer<TMilli>;
    interface Read<uint16_t> as PhotoRead;
    interface Read<uint16_t> as TempRead;
    interface Leds;
    interface GeneralIO as IOC0;
  }
}
implementation
{
  uint8_t servicesTemp=0;

   message_t pollSendBuf;
   message_t readSendBuf;
   message_t switchSendBuf;
  
  poll_answer_t pollAnswer;
  switch_answer_t switchAnswer;
  
  uint8_t sensorToPoll;
  bool sensing;
  bool sendBusy;

  /* Current local state - interval, version and accumulated readings */
  readings_answer_t readingsAnswer;
  readings_request_t readingsRequest;
  poll_request_t pollRequest;
  poll_answer_t  pollAnswer;
  status_t status;

  uint8_t reading; /* 0 to NREADINGS */
  
  // Use LEDs to report various status issues.
  void report_problem() { 
	call Leds.led0Toggle(); 
  }
  void report_sent() { 
	call Leds.led1Toggle(); 
  }
  void report_received() {
	call Leds.led2Toggle(); 
  }

  event void Boot.booted() {
    status.id=TOS_NODE_ID;
    call IOC0.makeOutput();
    call IOC0.clr();
    status.switchState = 0;
    status.services=5;
    sensorToPoll=99;
    
    readingsAnswer.messageType=READINGANSWER;
    readingsAnswer.id=TOS_NODE_ID;
    
    if (call RadioControl.start() != SUCCESS){
      report_problem();      
    }
  }

  void startTimer() {
    call Timer.startPeriodic(DEFAULT_INTERVAL);
    reading = 0;
  }
  
  void switchPin(){
    if(status.switchState==0){
      call IOC0.set();
      status.switchState = 1;
    }
    else{
      call IOC0.clr();
      status.switchState = 0;
    
    }    
  }

  event void RadioControl.startDone(error_t error) {
  }

  event void RadioControl.stopDone(error_t error) {
  }

  
  event message_t* pollReceiver.receive(message_t* msg, void* payload, uint8_t len) {
    
    poll_request_t *gmsg = payload;
    //report_received();
    
    
    pollAnswer.messageType = ACK;
    pollAnswer.services=status.services;
    pollAnswer.id=status.id;
    
    memcpy(call pollResponder.getPayload(&pollSendBuf, sizeof(pollAnswer)), &pollAnswer, sizeof pollAnswer);
    if (call pollResponder.send(AM_BROADCAST_ADDR, &pollSendBuf, sizeof pollAnswer) != SUCCESS){
      //report_problem();     
    }

    return msg;
  }

    event message_t* switchPoll.receive(message_t* msg, void* payload, uint8_t len) {
    
		switch_poll_t *gmsg = payload;
		if(status.services>3){
			if(gmsg->id==status.id){
				//report_received();
			
				switchAnswer.messageType = SWITCHANSWER;
				switchAnswer.id=status.id;
				switchAnswer.state=status.switchState;
					
				memcpy(call switchResponder.getPayload(&switchSendBuf, sizeof(switchAnswer)), &switchAnswer, sizeof switchAnswer);
					if (call	 switchResponder.send(AM_BROADCAST_ADDR, &switchSendBuf, sizeof switchAnswer) != SUCCESS){
						//report_problem();     
					}
			
			}
		}

		return msg;
    }
  
	event message_t* switchToggle.receive(message_t* msg, void* payload, uint8_t len) {
    
		switch_change_t *gmsg = payload;
		if(status.services>3){
			if(gmsg->id==status.id){
		
				//report_received();
				switchPin();
	
				switchAnswer.messageType = SWITCHANSWER;
				switchAnswer.id=status.id;
				switchAnswer.state=status.switchState;
		
				memcpy(call switchResponder.getPayload(&switchSendBuf, sizeof(switchAnswer)), &switchAnswer, sizeof switchAnswer);
				if (call switchResponder.send(AM_BROADCAST_ADDR, &switchSendBuf, sizeof switchAnswer) != SUCCESS){
					//report_problem();     
				}
	  
			}
		}
		return msg;
	}

	
  event message_t* readReceiver.receive(message_t* msg, void* payload, uint8_t len) {
    
    readings_request_t *gmsg = payload;
    report_received();
    if(gmsg->id==status.id)
    {
      if(call Timer.isRunning()){
		call Timer.stop();
      }
      
      servicesTemp = status.services;
      sensorToPoll = gmsg->type; 
      if(servicesTemp>5){
		servicesTemp=servicesTemp-4;	
      }
      if(
			(sensorToPoll==PHOTO&&
				(
					servicesTemp>1
				)
			)
		||
			(sensorToPoll==TEMP&& 
				(
					(servicesTemp%2)==1
				)
			)
		)
	    {	    
			startTimer();
	    }
    }
    return msg;
  }

  event void Timer.fired() {
	readingsAnswer.type=sensorToPoll;
    if (reading == NREADINGS){		
		if (sizeof readingsAnswer<= call readResponder.maxPayloadLength())
		{
				// Don't need to check for null because we've already checked length
				// above
				memcpy(call readResponder.getPayload(&readSendBuf, sizeof(readingsAnswer)), &readingsAnswer, sizeof readingsAnswer);
				if (call readResponder.send(AM_BROADCAST_ADDR, &readSendBuf, sizeof readingsAnswer) != SUCCESS){
					report_problem();
				}
		}
		reading = 0;
	
    }
	
	if(sensorToPoll==PHOTO){
		if (call PhotoRead.read() != SUCCESS){
			report_problem();
		}
    }
	
    if(sensorToPoll==TEMP){
		if (call TempRead.read() != SUCCESS){
			report_problem();
		}
    }
    
  }

  event void readResponder.sendDone(message_t* msg, error_t error) {
    if (error == SUCCESS){
      report_sent();
	  if(call Timer.isRunning()){
		call Timer.stop();
      }
	}
    else{
      report_problem();
    }

  }
  
  event void pollResponder.sendDone(message_t* msg, error_t error) {
    if (error == SUCCESS)
      report_sent();
    else{
      //report_problem();
    }
  }
  
  event void switchResponder.sendDone(message_t* msg, error_t error) {
    if (error == SUCCESS)
      report_sent();
    else{
      //report_problem();
    }
  }

  event void PhotoRead.readDone(error_t result, uint16_t data) {
    if (result != SUCCESS)
    {
		data = 0xffff;
		report_problem();
    }
    if (reading < NREADINGS) 
      readingsAnswer.readings[reading++] = data;
  }
  
  event void TempRead.readDone(error_t result, uint16_t data) {
    if (result != SUCCESS)
    {
		data = 0xffff;
		report_problem();
    }
    if (reading < NREADINGS) 
      readingsAnswer.readings[reading++] = data;
  }
}
