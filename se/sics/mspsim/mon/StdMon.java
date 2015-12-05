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
 * Display events on stdout.
 *
 * Author  : David Hauweele
 * Created : Dec 3 2015
 * Updated : $Date:  $
 *           $Revision: $
 */

package se.sics.mspsim.mon;

public class StdMon extends MonBackend {
  private int swapb(int u16) { /* ntohs */
    return ((u16 << 8 | u16 >> 8) & 0xffff);
  }

  public void recordState(int context, int entity, int state,
                          long cycles, double timeMillis) {
    System.out.printf("(mon) @%d %fms RECORD %d %d %d\n", cycles, timeMillis, swapb(context), swapb(entity), swapb(state));
  }

  public void recordInfo(int context, int entity, byte[] info,
                         long cycles, double timeMillis) {
    System.out.printf("(mon) @%d %fms INFO %d %d [", cycles, timeMillis, swapb(context), swapb(entity));
    for(byte b : info)
      System.out.printf("%02x", b);
    System.out.printf("]\n");
  }
}











