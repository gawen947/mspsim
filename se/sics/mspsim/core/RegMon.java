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
 * $Id$
 *
 * -----------------------------------------------------------------
 *
 * A monitor device that can be accessed directly from memory,
 * drastically reducing the number of cycles to record an event.
 *
 * Author  : David Hauweele
 * Created : Fri Dec 4 16:06:00 2015
 * Updated : $Date$
 *           $Revision$
 */

package se.sics.mspsim.core;

import se.sics.mspsim.mon.backend.MonBackend;
import se.sics.mspsim.mon.MonTimestamp;

public class RegMon extends IOUnit {
  public static final int MONCTX = 0x1C0; /* context */
  public static final int MONENT = 0x1C2; /* entity */
  public static final int MONSTI = 0x1C4; /* state/info */
  public static final int MONCTL = 0x1C6; /* len/control */

  /* CTL register:
     9:   mode (0: event / 1: info)
     8:   record event/info
     7-0: info len
   */

  private int ctx;
  private int ent;
  private int sti;
  private int len;

  private MonBackend backend;

  public RegMon(MSP430Core cpu, MonBackend monBackend) {
    super("RMON", cpu, cpu.memory, 0);

    backend = monBackend;
  }

  public void write(int address, int value, boolean word, long cycles) {
    switch(address) {
    case MONCTX:
      ctx = value;
      break;
    case MONENT:
      ent = value;
      break;
    case MONSTI:
      sti = value;
      break;
    case MONCTL:
      len = value & 0xff;

      if((value & 0x100) != 0) {
        if((value & 0x200) != 0)
          recordInfo();
        else
          recordState();
      }
    }
  }

  public int read(int address, boolean word, long cycles) {
    return 0;
  }

  public void interruptServiced(int vector) {}

  private void recordState() {
    /* sti is state */
    backend.state(ctx, ent, sti,
                  new MonTimestamp(cpu.cycles, cpu.getTimeMillis()));
  }

  private void recordInfo() {
    byte[] info = new byte[len];

    /* sti is info ptr */
    for(int i = 0 ; i < len ; i++)
      /* cpu memory has a byte granularity.
         great for us. */
      info[i] = (byte)cpu.memory[sti + i];

    backend.info(ctx, ent, info,
                 new MonTimestamp(cpu.cycles, cpu.getTimeMillis()));
  }
}

