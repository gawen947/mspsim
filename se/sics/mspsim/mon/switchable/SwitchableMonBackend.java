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
 * Switchable monitor backends must implement this interface.
 *
 * Author  : David Hauweele
 * Created : Jan 20 2016
 * Updated : $Date:  $
 *           $Revision: $
 */

package se.sics.mspsim.mon.switchable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import se.sics.mspsim.mon.MonException;
import se.sics.mspsim.mon.MonTimestamp;

abstract public class SwitchableMonBackend {
  protected final MonTimestamp recordOffset;
  protected final MonTimestamp infoOffset;
  protected final MonTimestamp byteOffset;
  protected final ByteOrder    byteOrder;
  
  public SwitchableMonBackend(MonTimestamp recordOffset, MonTimestamp infoOffset, MonTimestamp byteOffset, ByteOrder byteOrder) throws MonException {
    this.recordOffset = recordOffset;
    this.infoOffset   = infoOffset;
    this.byteOffset   = byteOffset;
    this.byteOrder    = byteOrder;
  }
  
  /* Useful static for file backends. */
  static protected byte[] toBytes(int value, ByteOrder byteOrder) {
    ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE >> 3);
    buf.order(byteOrder);
    buf.putInt(value);
    
    return buf.array();
  }

  static protected byte[] toBytes(short value, ByteOrder byteOrder) {
    ByteBuffer buf = ByteBuffer.allocate(Short.SIZE >> 3);
    buf.order(byteOrder);
    buf.putShort(value);
   
    return buf.array();
  }
  
  abstract public void recordState(int context, int entity, int state, MonTimestamp timestamp) throws MonException;
  abstract public void recordInfo(int context, int entity, byte[] info, MonTimestamp timestamp) throws MonException;

  abstract public void destroy() throws MonException;
}
