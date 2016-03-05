package se.sics.mspsim.mon.multinode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import se.sics.mspsim.mon.MonTimestamp;

public class MonOffset extends Node implements Event {
  MonTimestamp stateOffset;
  MonTimestamp dataOffset;
  MonTimestamp byteOffset;
  ByteOrder byteOrder;
  
  public MonOffset(double simTime, MonTimestamp nodeTime, short nodeID,
                    MonTimestamp stateOffset, MonTimestamp dataOffset, MonTimestamp byteOffset,
                    ByteOrder byteOrder) {
    super(simTime, nodeTime, nodeID);
    
    this.stateOffset = stateOffset;
    this.dataOffset  = dataOffset;
    this.byteOffset  = byteOffset;
    this.byteOrder   = byteOrder;
  }
  
  @Override
  public void write(OutputStream out) throws IOException {
    super.write(out);
    
    writeHeader(out, EventType.MON_OFFSET, (MonTimestamp.SIZE * 3) >> 3 + 1);
    writeBytes(out, stateOffset);
    writeBytes(out, dataOffset);
    writeBytes(out, byteOffset);
    
    if(byteOrder == ByteOrder.BIG_ENDIAN)
      writeBytes(out, (byte)'B'); /* BE */
    else
      writeBytes(out, (byte)'l'); /* le */
  }
}
