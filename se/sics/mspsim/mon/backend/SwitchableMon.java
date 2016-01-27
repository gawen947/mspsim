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

import se.sics.mspsim.mon.MonError;
import se.sics.mspsim.mon.MonException;
import se.sics.mspsim.mon.MonTimestamp;
import se.sics.mspsim.mon.switchable.SwitchableMonBackend;
import se.sics.mspsim.mon.switchable.SwitchableMonBackendCreator;

public abstract class SwitchableMon extends MonBackend {
  private MonTimestamp recordOffset = null;
  private MonTimestamp infoOffset   = null;
  private MonTimestamp byteOffset   = null;
  
  private SwitchableMonBackend backend = null;
  
  protected void initiated() {
    recordOffset = getRecordOffset();
    infoOffset   = getInfoOffset();
    byteOffset   = getByteOffset();
  }

  public void selectBackend(SwitchableMonBackendCreator backendCreator) {
    if(this.backend != null)
      /* replace a currently existing backend */
      close();
    
    /* create the backend instance now */
    try {
      this.backend = backendCreator.create(recordOffset, infoOffset, byteOffset, getEndian());
      
      /* Tell subclasses about the newly created backend instance.
       * This subclass implement behaviors to react to events that
       * are triggered when no backend is currently selected. */
      initSkip(backend);
    } catch (MonException e) {
      /* If something bad happened during the backend creation,
       * tell the user and acts like if no backend was selected. */
      backendError(e);
      return;
    }
  }
  
  public void recordState(int context, int entity, int state, MonTimestamp timestamp) {
    try {
      if(backend != null)
        backend.recordState(context, entity, state, timestamp);
      else
        /* no backend selected, skip event and tell subclass */
        skipState(context, entity, state, timestamp);
    } catch(MonException e) {
      /* If something bad happened during the backend creation,
       * tell the user and acts like if no backend was selected. */
      backendError(e);
      return;
    }
  }

  public void recordInfo(int context, int entity, byte[] info, MonTimestamp timestamp) {
    try {
      if(backend != null)
        backend.recordInfo(context, entity, info, timestamp);
      else
        /* no backend selected, skip event and tell subclass */
        skipInfo(context, entity, info, timestamp);
    } catch(MonException e) {
      /* If something bad happened during the backend creation,
       * tell the user and acts like if no backend was selected. */
      backendError(e);
      return;
    }
  }
  
  public void close() {
    if(this.backend != null) {
      /* tell the backend to finalize any pending operation */
      try {
        this.backend.destroy();
      } catch (MonException e) {
        /* If we cannot destroy the backend, at least we can mark it as erroneous and unselected. */
        backendError(e);
      }

      unselect();      
    }
  }
  
  private void unselect() {
    if(this.backend != null) {
      /* tell subclass that the currently selected backend was just destroyed */
      destroySkip(this.backend);
      
      /* marks that no backend are currently selected */
      this.backend = null;
      
      System.out.println("(mon) backend disabled!");
    }
  }
  
  private void backendError(MonException e) {   
    unselect();

    /* This is ugly! I know...
     * If we have an error while recording events,
     * we want the complete simulation to stop.
     * Otherwise we would have to throw the exception
     * upwards (up to the user interface).
     * 
     * But this is still research code. So it's OK for now.
     * We better abort an erroneous simulation than risking
     * to base our observations on invalid results.
     * 
     * At least I put a fixme.
     * 
     * FIXME: propagate exception up to the UI. */
    throw new MonError("monitor backend error");
  }
  
  /* subclasses must override this to provide their own implementation for skipped events. */
  protected abstract void skipState(int context, int entity, int state, MonTimestamp timestamp) throws MonException;
  protected abstract void skipInfo(int context, int entity, byte[] info, MonTimestamp timestamp) throws MonException;
  protected abstract void initSkip(SwitchableMonBackend backend);
  protected abstract void destroySkip(SwitchableMonBackend backend);
}
