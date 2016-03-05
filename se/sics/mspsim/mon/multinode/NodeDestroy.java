package se.sics.mspsim.mon.multinode;

import java.io.IOException;
import java.io.OutputStream;

import se.sics.mspsim.mon.MonTimestamp;

public class NodeDestroy extends Node implements Event {
  public NodeDestroy(double simTime, MonTimestamp nodeTime, short nodeID) {
    super(simTime, nodeTime, nodeID);
  }
  
  @Override
  public void write(OutputStream out) throws IOException {
    super.write(out);
    
    writeHeader(out, EventType.NODE_DESTROY, 0);
  }
}
