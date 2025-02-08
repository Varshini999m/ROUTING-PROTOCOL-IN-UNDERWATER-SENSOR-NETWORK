//! Simulation: of void

import org.arl.fjage.*
import org.arl.unet.sim.channels.*
import org.arl.unet.*
import org.arl.unet.phy.*
import org.arl.unet.mac.*
import org.arl.unet.net.Router.*
import org.arl.fjage.RealTimePlatform.*
import org.arl.unet.phy.Ranging.*
import java.lang.Long



modem.dataRate = [9600, 9600].bps
modem.frameLength = [9600/8, 9600/8].bytes
modem.headerLength = 0
modem.preambleDuration = 0
modem.txDelay = 0
 

channel.model = ProtocolChannelModel;
//platform = org.arl.fjage.RealTimePlatform;

channel.soundSpeed = 1500.mps           // c
channel.communicationRange = 100.m     // Rc
channel.detectionRange = 100.m         // Rd
channel.interferenceRange = 100.m      // Ri
channel.pDetection = 1                  // pd
channel.pDecoding = 1                  // pc

//logLevel = fine
logLevel 'org.arl.unet.sim', FINE

def T = 120.mins
long packet_id = 0;
sources = 1
//def x = sources *15000 ;
def x = 13000


///////////////////////////////////////////////////////////////////////////////
// display documentation

println '''
2-node network
--------------

Node A: tcp://localhost:1101, http://localhost:8081/
Node B: tcp://localhost:1102, http://localhost:8082/
'''

///////////////////////////////////////////////////////////////////////////////
// simulator configuration

//platform = RealTimePlatform   // use real-time mode



simulate T, {
  def n1 = node '1', address:1, location: [ 0.m, 0.m, 0.m], web: 8081, api: 1101, stack: "$home/etc/setup_sink"
  def n2 = node '2', address:2, location: [ 0.m, 0.m, -70.m], web: 8082, api: 1102, stack: "$home/etc/setup_node"
 

 n2.startup = {          
   def router = agentForService Services.ROUTING               
   def a = agent('node_agent');
   def address = a.addr;
   def data = [1,1,1,1]
   def data_size = data.size();


            add new TickerBehavior(x, {
            data[data_size] = address;
            data[data_size + 1] = ++packet_id;
            router << new TxFrameReq(to: 1, data: data, protocol:Protocol.DATA);                  
          })         
        } 
}
