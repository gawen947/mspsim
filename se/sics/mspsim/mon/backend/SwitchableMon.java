/**
 * Copyright (c) 2015, David Hauweele <david@hauweele.net>
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
 * Switchable monitor backends. 
 *
 * Author  : David Hauweele
 * Created : Jan 20 2016
 * Updated : $Date:  $
 *           $Revision: $
 */

package se.sics.mspsim.mon.backend;

import java.nio.ByteOrder;

import se.sics.mspsim.mon.MonTimestamp;
import se.sics.mspsim.mon.switchable.SwitchableBackend;

public abstract class SwitchableMon extends MonBackend {
  private MonTimestamp recordOffset = null;
  private MonTimestamp infoOffset   = null;
  private MonTimestamp byteOffset   = null;
  
  protected SwitchableBackend backend = null;
  
  protected void initiated() {
    recordOffset = getRecordOffset();
    infoOffset   = getInfoOffset();
    byteOffset   = getByteOffset();
  }

  public void selectBackend(SwitchableBackend backend) {
    if(this.backend != null)
      /* replace a currently existing backend */
      this.backend.destroy();
    
    this.backend = backend;
    
    backend.init(recordOffset, infoOffset, byteOffset, getEndian());
  }
  
  public void recordState(int context, int entity, int state, MonTimestamp timestamp) {
    if(backend != null)
      backend.recordState(context, entity, state, timestamp);
    else
      skipState(context, entity, state, timestamp);
  }

  public void recordInfo(int context, int entity, byte[] info, MonTimestamp timestamp) {
    if(backend != null)
      backend.recordInfo(context, entity, info, timestamp);
    else
      skipInfo(context, entity, info, timestamp);
  }
  
  public void close() {
    if(this.backend != null) {
      this.backend.destroy();
      this.backend = null;
    }
  }
  
  /* subclasses must override this to provide their own implementation for skipped events. */
  protected abstract void skipState(int context, int entity, int state, MonTimestamp timestamp);
  protected abstract void skipInfo(int context, int entity, byte[] info, MonTimestamp timestamp);
}

