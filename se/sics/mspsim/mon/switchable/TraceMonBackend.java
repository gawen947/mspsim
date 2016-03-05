/**
 * Copyright (c) 2016, David Hauweele <david@hauweele.net>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of MSPSim.
 *
 * $Id: $
 *
 * -----------------------------------------------------------------
 *
 * Records events into a trace file (see TraceFile).
 */


package se.sics.mspsim.mon.switchable;

import java.io.IOException;
import java.nio.ByteOrder;

import se.sics.mspsim.mon.MonException;
import se.sics.mspsim.mon.MonTimestamp;
import se.sics.mspsim.mon.multinode.Event;
import se.sics.mspsim.mon.multinode.MonData;
import se.sics.mspsim.mon.multinode.MonOffset;
import se.sics.mspsim.mon.multinode.MonState;
import se.sics.mspsim.mon.multinode.TraceFile;

public class TraceMonBackend extends SwitchableMonBackend {
  /* Use an instance of this class to tell SwitchableMon how to create this backend. */
  static public class Creator implements SwitchableMonBackendCreator {
    private final String filePath;
    
    public Creator(String filePath) {
      this.filePath = filePath;
    }
    
    @Override   
    public SwitchableMonBackend create(MonTimestamp recordOffset, MonTimestamp infoOffset, MonTimestamp byteOffset, ByteOrder byteOrder) throws MonException {
      return new TraceMonBackend(recordOffset, infoOffset, byteOffset, byteOrder, filePath);
    }
  }
  
  private final TraceFile trace;
  
  public TraceMonBackend(MonTimestamp recordOffset, MonTimestamp infoOffset,
                            MonTimestamp byteOffset, ByteOrder byteOrder, 
                            String filePath) throws MonException {
    super(recordOffset, infoOffset, byteOffset, byteOrder);
    
    try {
      trace = new TraceFile(filePath);
      
      MonTimestamp nullTime = new MonTimestamp(0, 0.);
      Event ev = new MonOffset(0., nullTime, (short)0, recordOffset, infoOffset, byteOffset, byteOrder);
      trace.write(ev);
    } catch (IOException e) {
      throw new MonException("cannot open/create '" + filePath + "'");
    }
    
    System.out.println("(mon) trace backend created!");
  }
  
  @Override
  public void recordState(int context, int entity, int state, MonTimestamp timestamp) throws MonException {
    Event ev = new MonState(0., timestamp, (short)0, (short)context, (short)entity, (short)state);
    try {
      trace.write(ev);
    } catch (IOException e) {
      throw new MonException("cannot write state event");
    }
  }

  @Override
  public void recordInfo(int context, int entity, byte[] info, MonTimestamp timestamp) throws MonException {
    Event ev = new MonData(0., timestamp, (short)0, (short)context, (short)entity, info);
    try {
      trace.write(ev);
    } catch (IOException e) {
      throw new MonException("cannot write data event");
    }   
  }

  @Override
  public void destroy() throws MonException {
    try {
      trace.destroy();
      System.out.println("(mon) file backend closes!");
    } catch (IOException e) {
      throw new MonException("close error");
    } 
  }
}
