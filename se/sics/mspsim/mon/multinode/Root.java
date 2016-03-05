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
 * Any events that happens is a Root event.
 * We use this special event during serialization so that 
 * we can distinguish events when parsing the monitor trace.
 * This is mandatory because we want to be able to parse 
 * traces with unknown events (forward compatibility). 
 */

package se.sics.mspsim.mon.multinode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import se.sics.mspsim.mon.MonTimestamp;
import se.sics.mspsim.util.Utils;

public class Root implements Event {  
  /* We could probably use generics here. */
  public static void writeBytes(OutputStream out, byte value) throws IOException {
    out.write(value);
  }
  
  public static void writeBytes(OutputStream out, short value) throws IOException {
    out.write(Utils.toBytes(value, ByteOrder.BIG_ENDIAN));
  }
  
  public static void writeBytes(OutputStream out, int value) throws IOException {
    out.write(Utils.toBytes(value, ByteOrder.BIG_ENDIAN));
  }
  
  public static void writeBytes(OutputStream out, double value) throws IOException {
    out.write(Utils.toBytes(value, ByteOrder.BIG_ENDIAN));
  }
  
  public static void writeBytes(OutputStream out, MonTimestamp nodeTime) throws IOException {
    out.write(nodeTime.toBytes(ByteOrder.BIG_ENDIAN));
  }
  
  /* Write type code and length headers. */
  protected void writeHeader(OutputStream out, EventType type, int len) throws IOException {
    short type_and_len = (short)(type.code << 1);
    
    if(len > 255)
      type_and_len |= 1;

    writeBytes(out, type_and_len);

   if((type_and_len & 1) == 0)
     writeBytes(out, (byte)len);
   else
     writeBytes(out, (int)len);
  }
  
  public void write(OutputStream out) throws IOException {
    out.write((byte)EventType.ROOT.code);
  }
}
