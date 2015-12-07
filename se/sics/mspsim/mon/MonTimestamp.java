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
 * Monitor event timestamp information.
 * Can also be used to record a duration and manage offsets.
 *
 * Author  : David Hauweele
 * Created : Dec 7 2015
 * Updated : $Date:  $
 *           $Revision: $
 */

package se.sics.mspsim.mon;

public class MonTimestamp {
  private long   c;
  private double ms;

  public MonTimestamp(long cycles, double timeMillis) {
    c  = cycles;
    ms = timeMillis;
  }

  public long getCycles() {
    return c;
  }

  public double getMillis() {
    return ms;
  }

  public MonTimestamp diff(MonTimestamp mon) {
    return new MonTimestamp(Math.abs(mon.getCycles() - c),
                            Math.abs(mon.getMillis() - ms));
  }

  public MonTimestamp reduce(MonTimestamp offset, int times) {
    return new MonTimestamp(c  - times * offset.getCycles(),
                            ms - times * offset.getMillis());
  }

  public MonTimestamp reduce(MonTimestamp offset) {
    return reduce(offset, 1);
  }
}









