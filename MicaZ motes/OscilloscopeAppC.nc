/*
 * Copyright (c) 2014 Grit Science
 * All rights reserved.
 *
 */

/**
 * Hua Dissertation thesis. 
 *
 * See README.txt file in this directory for usage instructions.
 *
 * @author Basil Nikolopoulos
 */
configuration OscilloscopeAppC { }
implementation
{
  components OscilloscopeC, HplAtm128GeneralIOC, MainC, ActiveMessageC, LedsC,
    new TimerMilliC(), new PhotoC() as PhotoSensor, new TempC() as TempSensor, 
    new AMSenderC(AM_POLLACK) as pollResponder, new AMReceiverC(AM_POLLREQ) as pollReceiver,
    new AMSenderC(AM_READACK) as readResponder, new AMReceiverC(AM_READREQ) as readReceiver,
    new AMReceiverC(AM_SWITCHCHANGE) as switchToggle, new AMReceiverC(AM_SWITCHPOLL) as switchPoll, new AMSenderC(AM_SWITCHANSWER) as switchResponder;

  OscilloscopeC.Boot -> MainC;
  OscilloscopeC.RadioControl -> ActiveMessageC;
  OscilloscopeC.pollResponder -> pollResponder;
  OscilloscopeC.pollReceiver -> pollReceiver;
  OscilloscopeC.readResponder -> readResponder;
  OscilloscopeC.readReceiver -> readReceiver;
  OscilloscopeC.switchToggle -> switchToggle;
  OscilloscopeC.switchPoll -> switchPoll;
  OscilloscopeC.switchResponder -> switchResponder;
  OscilloscopeC.Timer -> TimerMilliC;
  OscilloscopeC.PhotoRead -> PhotoSensor;
  OscilloscopeC.TempRead -> TempSensor;
  OscilloscopeC.Leds -> LedsC;
  OscilloscopeC.IOC0 -> HplAtm128GeneralIOC.PortC0;

  
}
