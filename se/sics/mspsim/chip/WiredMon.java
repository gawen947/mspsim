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
 * A simple but slow 2 wire monitor device.
 *
 * Author  : David Hauweele
 * Created : Nov 27 2015
 * Updated : $Date:  $
 *           $Revision: $
 */

package se.sics.mspsim.chip;

import se.sics.mspsim.core.Chip;
import se.sics.mspsim.core.MSP430Core;
import se.sics.mspsim.mon.MonBackend;
import se.sics.mspsim.mon.MonTimestamp;

/**
 Message format:

 [16: ctx][16: ent][16: state]
 [16: ctx][16: ent][-1][8: info-size][...: info]

 Special state -1 (0xffff) signal extra info.
*/
public class WiredMon extends Chip {
  private enum WiredMonState {
    CONTEXT,
    ENTITY,
    STATE,
    SIZE,
    INFO
  };

  private boolean clockHi = false;
  private boolean data    = false;

  private WiredMonState monState;
  private int bitCnt;
  private int context;
  private int entity;
  private int state;
  private int size;
  private byte infoByte;
  private int infoIdx;
  private byte[] info;

  private MonBackend backend;

  public WiredMon(String id, String name, MSP430Core cpu, MonBackend monBackend) {
    super(id, name, cpu);

    backend = monBackend;

    reset();
  }

  public void clockPin(boolean high) {
    if(clockHi == high) return;

    bitCnt++;

    switch(monState) {
    case CONTEXT:
      context <<= 1;
      context  |= data ? 1 : 0;

      if(bitCnt == 16)
        switchState(WiredMonState.ENTITY);

      break;
    case ENTITY:
      entity <<= 1;
      entity  |= data ? 1 : 0;

      if(bitCnt == 16)
        switchState(WiredMonState.STATE);

      break;
    case STATE:
      state <<= 1;
      state  |= data ? 1 : 0;

      if(bitCnt == 16) {
        if(state == 65535) /* extra info */
          switchState(WiredMonState.SIZE);
        else
          record();
      }

      break;
    case SIZE:
      size <<= 1;
      size  |= data ? 1 : 0;

      if(bitCnt == 8) {
        info = new byte[size];
        switchState(WiredMonState.INFO);
      }

      break;
    case INFO:
      infoByte <<= 1;
      infoByte  |= data ? 1 : 0;

      if(bitCnt == 8) {
        bitCnt = 0;

        info[infoIdx++] = infoByte;

        if(infoIdx == size)
          record();
      }

      break;
    }

    clockHi = high;
  }

  public void dataPin(boolean high) {
    data = high;
  }

  private void switchState(WiredMonState newState) {
    monState = newState;
    bitCnt   = 0;
  }

  private void reset() {
    bitCnt   = 0;
    monState = WiredMonState.CONTEXT;

    context = 0;
    entity  = 0;
    state   = 0;
    size    = 0;
    infoIdx = 0;
    info    = null;
  }

  private void record() {
    MonTimestamp timestamp = new MonTimestamp(cpu.cycles, cpu.getTimeMillis());

    if(info == null)
      backend.state(context, entity, state, timestamp);
    else
      backend.info(context, entity, info, timestamp);

    reset();
  }

  public int getConfiguration(int parameter) {
    return 0;
  }

  public int getModeMax() {
    return 0;
  }
}
