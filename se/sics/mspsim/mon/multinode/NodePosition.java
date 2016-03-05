package se.sics.mspsim.mon.multinode;

import java.io.IOException;
import java.io.OutputStream;

import se.sics.mspsim.mon.MonTimestamp;

public class NodePosition extends Node implements Event {
  private final double x;
  private final double y;
  
  public NodePosition(double simTime, MonTimestamp nodeTime, short nodeID,
                       double x, double y) {
    super(simTime, nodeTime, nodeID);
    
    this.x = x;
    this.y = y;
  }
  
  @Override
  public void write(OutputStream out) throws IOException {
    super.write(out);
    
    writeHeader(out, EventType.NODE_POSITION, (Double.SIZE * 2) >> 3);
    writeBytes(out, x);
    writeBytes(out, y);
  }
}
