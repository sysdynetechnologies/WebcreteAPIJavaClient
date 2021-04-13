import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public class ConcreteGoHandlerResolver implements HandlerResolver {



	@Override
	public List<Handler> getHandlerChain(PortInfo arg0) {
		 List<Handler> handlers = new ArrayList<Handler>();
		 handlers.add(new ConcreteGoHandler());
	      // add handlers to list based on PortInfo information
	      return handlers;
	}
	
 }
