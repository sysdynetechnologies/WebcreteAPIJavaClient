


import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

public class ConcreteGoHandler implements SOAPHandler<SOAPMessageContext>
{
	Node ticketHeader = null;
  public Set<QName> getHeaders()
  {
    return Collections.emptySet();
  }

  public boolean handleMessage(SOAPMessageContext messageContext)
  {
     Boolean outboundProperty = (Boolean) messageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);

     
     if (ticketHeader != null) {
    	 try {
			messageContext.getMessage().getSOAPHeader().addChildElement((SOAPElement) ticketHeader);
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
     }
     try {
		ticketHeader = messageContext.getMessage().getSOAPHeader().getFirstChild();
	} catch (SOAPException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    return true;
  }

  public boolean handleFault(SOAPMessageContext messageContext)
  {
    return true;
  }
  public void close(MessageContext messageContext)
  {
  }
  
  
}
